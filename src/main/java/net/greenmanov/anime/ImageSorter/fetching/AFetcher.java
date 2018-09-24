package net.greenmanov.anime.ImageSorter.fetching;

import net.greenmanov.anime.ImageSorter.helpers.Filters;
import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.anime.ImageSorter.json.AutosaveDatabase;
import net.greenmanov.anime.ImageSorter.json.JsonDatabase;
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

abstract public class AFetcher {
    private static final Logger LOGGER = LogManager.getLogger(AFetcher.class.getName());

    protected JsonDatabase database;

    protected boolean needDelay;
    protected int delay;

    protected long files = 0;
    protected long fetchedFiles = 0;

    /**
     * Fetch info for all files in path, saves them into database and moves them
     *
     * @param from Source dir
     * @param to   Dir for fetched files
     * @throws InterruptedException When problem with thread sleeping for delay
     */
    public void fetch(Path from, Path to) throws InterruptedException {
        fetch(from, to, 70, 1000, null);
    }

    /**
     * Fetch info for all files in path, saves them into database and moves them
     *
     * @param from          Source dir
     * @param to            Dir for fetched files
     * @param minSimilarity Minimal similarity to be considered info match
     * @throws InterruptedException When problem with thread sleeping for delay
     */
    public void fetch(Path from, Path to, int minSimilarity) throws InterruptedException {
        fetch(from, to, minSimilarity, 1000, null);
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
    public void fetch(Path from, Path to, int minSimilarity, int delay, Path noMatchDir) throws InterruptedException {
        this.needDelay = false;
        this.delay = delay;
        checkDirectory(from);
        checkDirectory(to);
        if (noMatchDir != null) {
            checkDirectory(noMatchDir);
        }

        try {
            this.database = new AutosaveDatabase(to.resolve(JsonDatabase.DEFAULT_NAME));

            try (Stream<Path> paths = Files.walk(from)) {
                List<Path> pathList = paths
                        .filter(Files::isRegularFile)
                        .filter(Filters::isImage)
                        .filter(file -> file.getParent().equals(from))
                        .collect(Collectors.toList());
                for (Path file : pathList) {
                    this.fetchFile(file, to, minSimilarity, noMatchDir);
                }
            } catch (IOException e) {
                LOGGER.error("Problem while reading files form directory", e);
            }

            this.database.save(true);
        } catch (IOException e) {
            LOGGER.error("Problem with loading JsonDatabase", e);
        }
        LOGGER.info("Finished fetching. Fetched: " + fetchedFiles + "/" + files);
    }

    /**
     * Fetch info about file and moves it if needed
     *
     * @param filePath      Path to file
     * @param to            Dir for parsed files
     * @param minSimilarity Minimal similarity to be considered info match
     * @param noMatchDir    Dir for files without match, can be null
     */
    abstract protected void fetchFile(Path filePath, Path to, int minSimilarity, Path noMatchDir) throws InterruptedException;


    /**
     * Return actual date
     *
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

    /**
     * Fetch data from URL
     *
     * @param url  URL to data
     * @param file Image file
     * @param to   Dir for added image
     */
    protected void fetchUrl(String url, Path file, Path to) throws InterruptedException {
        if (delay > 0 && this.needDelay) {
            Thread.sleep(delay);
        }
        this.needDelay = false;
        files++;
        LOGGER.info("Fetching file: " + file.getFileName());
        DynamicParser parser = new DynamicParser();
        try {
            this.needDelay = true;
            parser.parse(url);
            Image image = database.get(file);
            if (image != null) {
                database.remove(image.getName());
            }
            image= new Image(file, now(), parser.getSource(), parser.getImage(), parser.getTags());
            database.add(image);
            if (to != null) {
                moveFile(file, to);
            }
            LOGGER.info("Added: " + file.getFileName());
            fetchedFiles++;
        } catch (IOException e) {
            LOGGER.warn("Can't parse URL " + url, e);
        }
    }
}
