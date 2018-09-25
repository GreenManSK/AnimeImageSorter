package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Filters;
import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.anime.ImageSorter.json.JsonDatabase;
import net.greenmanov.anime.ImageSorter.json.JsonDatabaseProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Sorter {
    private static final Logger LOGGER = LogManager.getLogger(Sorter.class.getName());

    private Path originalTo;
    protected List<Dir> dirs;
    protected JsonDatabase database;

    public void sort(Path from, Path to) {
        LOGGER.info("Sorting: " + from);
        checkDirectory(from);
        checkDirectory(to);
        try {
            dirs = buildDirMap(to);
            try {
                database = JsonDatabaseProvider.getAutosaveDatabase(from.resolve(JsonDatabase.DEFAULT_NAME));
                try (Stream<Path> paths = Files.walk(from)) {
                    paths.filter(Files::isRegularFile)
                            .filter(Filters::isImage)
                            .filter(file -> file.getParent().equals(from))
                            .forEach(this::sortImage);
                } catch (IOException e) {
                    LOGGER.error("Problem while reading files form directory", e);
                }
            } catch (IOException e) {
                LOGGER.error("Can't load image database", e);
            }
            for (Dir dir : dirs) {
                dir.getDatabase().save(true);
            }
        } catch (IOException e) {
            LOGGER.error("Can't build directory map", e);
        }
    }

    /**
     * Sort image file
     *
     * @param file Path to image
     */
    protected void sortImage(Path file) {
        Image image = database.get(file);
        if (image == null) {
            LOGGER.info("Not in database: " + file.getFileName());
            return;
        }
        if (image.getTags().size() == 0) {
            LOGGER.info("Not tags for sorting: " + file.getFileName());
            return;
        }
        Dir best = getBest(image);
        if (best == null) {
            LOGGER.info("No matching directory: " + file.getFileName());
            return;
        }
        try {
            if (!database.equals(best.getDatabase())) {
                database.remove(file.getFileName().toString());
                best.getDatabase().add(image);
            }
            moveFile(file, best.getPath());
            LOGGER.info("Moving " + file + " to " + best.getPath());
        } catch (IOException e) {
            LOGGER.error("Problem with file " + file, e);
        }
    }


    /**
     * Move file to directory
     *
     * @param file File path
     * @param dir  Directory path
     * @throws IOException
     */
    protected void moveFile(Path file, Path dir) throws IOException {
        try {
            Files.move(file, dir.resolve(file.getFileName()));
        } catch (FileAlreadyExistsException e) {
            LOGGER.warn("File already exists: " + dir.resolve(file.getFileName()), e);
        }
    }

    /**
     * Get best matching dir
     *
     * @param image Image file
     * @return Best matching dir or null if none matchs
     */
    protected Dir getBest(Image image) {
        Dir result = null;
        int priority = Integer.MIN_VALUE;
        for (Dir dir : dirs) {
            IRule matchingRule = dir.getRuleSet().match(image);
            if (matchingRule != null && matchingRule.getPriority() > priority) {
                priority = matchingRule.getPriority();
                result = dir;
            } else if (matchingRule != null && matchingRule.getPriority() == priority) {
                if (dir.getRuleSet().size() > result.getRuleSet().size()) {
                    priority = matchingRule.getPriority();
                    result = dir;
                }
            }
        }
        return result;
    }

    /**
     * Build sorting map of directories
     *
     * @param to Root of directories
     * @return List of possible directories with RuleSets
     * @throws IOException
     */
    protected List<Dir> buildDirMap(Path to) throws IOException {
        if (to.equals(originalTo)) {
            return dirs;
        }
        originalTo = to;
        Map<Path, Dir> dirs = new HashMap<>();
        try (Stream<Path> paths = Files.walk(to)) {
            paths.filter(Files::isDirectory).forEach(path -> {
                try {
                    if (!path.resolve(RuleSet.DEFAULT_FILE).toFile().exists()) {
                        return;
                    }
                    Dir dir = new Dir(path);
                    dirs.put(path, dir);
                    Path parent = path.getParent();
                    while (dirs.containsKey(parent)) {
                        dir.getRuleSet().add(dirs.get(parent).getRuleSet());
                        parent = parent.resolve("../");
                    }
                } catch (IOException e) {
                    LOGGER.error("Can't build RuleSet for directory " + path, e);
                }
            });
            return new ArrayList<>(dirs.values());
        }
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
