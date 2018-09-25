package net.greenmanov.anime.ImageSorter.fetching;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.anime.ImageSorter.helpers.ImageResizer;
import net.greenmanov.anime.ImageSorter.json.JsonDatabase;
import net.greenmanov.iqdb.api.*;
import net.greenmanov.iqdb.parsers.IParser;
import net.greenmanov.iqdb.parsers.impl.DynamicParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Fetch info about anime images from iqdb
 */
public class IqdbFetcher extends AFetcher {
    private static final Logger LOGGER = LogManager.getLogger(IqdbFetcher.class.getName());

    protected IIqdbApi api;
    protected boolean needDelay;
    protected int delay;

    public static final int MAX_SIZE = IIqdbApi.MAX_FILE_SIZE;
    public static final int RESIZE_IMAGE_DIMENSION_MAX = 1000;

    public IqdbFetcher() {
    }

    /**
     * Fetch info for all files in path, saves them into database and moves them
     *
     * @param from          Source dir
     * @param to            Dir for fetched files
     * @param minSimilarity Minimal similarity to be considered info match
     * @param delay         Delay between api calls in ms
     * @param noMatchDir    Dir for files without match, can be null
     * @throws InterruptedException When problem with thread sleeping for delay
     */
    @Override
    public void fetch(Path from, Path to, int minSimilarity, int delay, Path noMatchDir) throws InterruptedException {
        this.api = new IqdbApi();
        this.needDelay = false;
        this.delay = delay;
        super.fetch(from, to, minSimilarity, delay, noMatchDir);
    }

    /**
     * Fetch info about file and moves it if needed
     *
     * @param filePath      Path to file
     * @param to            Dir for parsed files
     * @param minSimilarity Minimal similarity to be considered info match
     * @param noMatchDir    Dir for files without match, can be null
     */
    protected void fetchFile(Path filePath, Path to, int minSimilarity, Path noMatchDir) throws InterruptedException {
        if (delay > 0 && this.needDelay) {
            Thread.sleep(delay);
        }
        this.needDelay = false;
        JsonDatabase database = getDatabase(filePath, to);
        Image img = database.get(filePath);
        if (img != null && img.getTags().size() > 0) {
            LOGGER.info("Already fetched: " + filePath.getFileName());
            return;
        }
        files++;
        LOGGER.info("Fetching file: " + filePath.getFileName());
        try {
            this.needDelay = true;
            List<Match> matches;
            try {
                matches = api.searchFile(filePath.toFile(), Options.DEFAULT);
            } catch (FileSizeLimitException e) {
                LOGGER.warn("Resizing image: " + filePath.getFileName());
                matches = searchResized(filePath);
            }

            for (Match match : matches) {
                if (match.getSimilarity() < minSimilarity) {
                    break;
                }
                // TODO: Remove when support for The Anime Gallery added
                if  (ServiceType.getTypeByUrl(match.getUrl()) == ServiceType.THE_ANIME_GALLERY) {
                    continue;
                }
                LOGGER.info("Parsing data: " + filePath.getFileName());
                IParser parser = new DynamicParser();
                parser.parse(match.getUrl());
                if (parser.getTags().size() == 0) {
                    continue;
                }
                Image image = new Image(filePath, now(), parser.getSource(), match.getUrl(), parser.getTags());
                database.add(image);
                fetchedFiles++;
                try {
                    moveFile(filePath, to);
                } catch (IOException e) {
                    LOGGER.error("Could not move file " + filePath.getFileName(), e);
                }
                LOGGER.info("Added: " + filePath.getFileName());
                return;
            }
            LOGGER.info("No match: " + filePath.getFileName());
            if (noMatchDir != null) {
                moveFile(filePath, noMatchDir);
            }
        } catch (IOException e) {
            LOGGER.error("Can't get info from iqdb for file" + filePath.getFileName(), e);
        }
    }

    /**
     * Resize image so its larger dimension matches RESIZE_IMAGE_DIMENSION_MAX and try to search with it
     * @param filePath Path to image
     * @return List of Matches for resized image
     */
    private List<Match> searchResized(Path filePath) throws IOException {
        try {
            File resized = ImageResizer.resizeImage(filePath, RESIZE_IMAGE_DIMENSION_MAX);
            if (resized == null) {
                return Collections.emptyList();
            }
            return api.searchFile(resized, Options.DEFAULT);
        } catch (FileSizeLimitException e) {
            LOGGER.warn("Resized image is still too large " + filePath, e);
        }
        return Collections.emptyList();
    }
}
