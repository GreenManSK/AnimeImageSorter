package net.greenmanov.anime.ImageSorter;

import net.greenmanov.anime.ImageSorter.json.AutosaveDatabase;
import net.greenmanov.anime.ImageSorter.json.JsonDatabase;
import net.greenmanov.iqdb.api.IIqdbApi;
import net.greenmanov.iqdb.api.IqdbApi;
import net.greenmanov.iqdb.api.Match;
import net.greenmanov.iqdb.api.Options;
import net.greenmanov.iqdb.parsers.IParser;
import net.greenmanov.iqdb.parsers.impl.DynamicParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fetch info about anime images
 */
public class InfoFetcher {
    private static final Logger LOGGER = LogManager.getLogger(InfoFetcher.class.getName());

    protected JsonDatabase database;
    protected IIqdbApi api;
    protected boolean needDelay;

    public InfoFetcher() {
    }

    /**
     * Fetch info for all files in path, saves them into database and moves them
     * @param from Source dir
     * @param to Dir for fetched files
     * @param minSimilarity Minimal similarity to be considered info match
     * @throws InterruptedException When problem with thread sleeping for delay
     */
    public void fetch(Path from, Path to, int minSimilarity) throws InterruptedException {
        fetch(from, to, minSimilarity, 1000, null);
    }

    /**
     * Fetch info for all files in path, saves them into database and moves them
     * @param from Source dir
     * @param to Dir for fetched files
     * @param minSimilarity Minimal similarity to be considered info match
     * @param delay Delay between api calls in ms
     * @param noMatchDir Dir for files without match, can be null
     * @throws InterruptedException When problem with thread sleeping for delay
     */
    public void fetch(Path from, Path to, int minSimilarity, int delay, Path noMatchDir) throws InterruptedException {
        checkDirectory(from);
        checkDirectory(to);
        if (noMatchDir != null) {
            checkDirectory(noMatchDir);
        }

        try {
            this.database = new AutosaveDatabase(to.resolve(JsonDatabase.DEFAULT_NAME));
            this.api = new IqdbApi();

            try (Stream<Path> paths = Files.walk(from)) {
                List<Path> pathList = paths
                        .filter(Files::isRegularFile)
                        .filter(this::isImage)
                        .collect(Collectors.toList());
                this.needDelay = false;
                for (Path file: pathList) {
                    if (!file.getParent().equals(from)) {
                        //Skip sub dirs
                        continue;
                    }
                    if (delay > 0 && this.needDelay) {
                        Thread.sleep(delay);
                    }
                    this.fetchFile(file, to, minSimilarity, noMatchDir);
                    this.needDelay = false;
                }
            } catch (IOException e) {
                LOGGER.error("Problem while reading files form directory", e);
            }

            this.database.save(true);
        } catch (IOException e) {
            LOGGER.error("Problem with loading JsonDatabase", e);
        }
    }

    /**
     * Fetch info about file and moves it if needed
     * @param filePath Path to file
     * @param to Dir for parsed files
     * @param minSimilarity Minimal similarity to be considered info match
     * @param noMatchDir Dir for files without match, can be null
     */
    protected void fetchFile(Path filePath, Path to, int minSimilarity, Path noMatchDir) {
        if (database.get(filePath.getFileName().toString()) != null) {
            LOGGER.info("Already fetched: " + filePath.getFileName());
            return;
        }
        LOGGER.info("Fetching file: " + filePath.getFileName());
        try {
            this.needDelay = true;
            List<Match> matches = api.searchFile(filePath.toFile(), Options.DEFAULT);
            Match best = matches.get(0);
            if (best == null || best.getSimilarity() < minSimilarity) {
                LOGGER.info("No match: " + filePath.getFileName());
                if (noMatchDir != null) {
                    moveFile(filePath, noMatchDir);
                }
            } else {
                LOGGER.info("Parsing data: " + filePath.getFileName());
                IParser parser = new DynamicParser();
                parser.parse(best.getUrl());
                Image image = new Image(filePath, now(), parser.getSource(), best.getUrl(), parser.getTags());
                database.add(image);
                try {
                    moveFile(filePath, to);
                } catch (IOException e) {
                    LOGGER.error("Could not move file " + filePath.getFileName(), e);
                }
                LOGGER.info("Added: " + filePath.getFileName());
            }
        } catch (IOException e) {
            LOGGER.error("Can't get info from iqdb for file" + filePath.getFileName(), e);
        }
    }

    /**
     * Check if file is image using mime type
     * @param file File Path
     * @return true if image
     */
    protected boolean isImage(Path file) {
        String mimetype = null;
        try {
            mimetype = Files.probeContentType(file);
        } catch (IOException e) {
            LOGGER.warn("Can't decide if file " + file.getFileName() + " is image", e);
            return false;
        }

        return mimetype != null && mimetype.split("/")[0].equals("image");
    }

    /**
     * Return actual date
     * @return Date object with actual time
     */
    protected Date now() {
        return new Date();
    }

    /**
     * Move file to directory
     *
     * @param file File path
     * @param dir  Directory path
     * @throws IOException
     */
    protected void moveFile(Path file, Path dir) throws IOException {
        Files.move(file, dir.resolve(file.getFileName()));
    }

    /**
     * Check if directory exists and throws exception if not
     *
     * @param dir Directory path
     * @throws IllegalArgumentException if directory do not exists
     */
    protected void checkDirectory(Path dir) throws IllegalArgumentException {
        if (!Files.exists(dir) && !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Directory " + dir.toString() + " do not exists");
        }
    }
}
