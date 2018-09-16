package net.greenmanov.anime.ImageSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Filters {
    private static final Logger LOGGER = LogManager.getLogger(Filters.class.getName());

    /**
     * Check if file is image using mime type
     *
     * @param file File Path
     * @return true if image
     */
    protected static boolean isImage(Path file) {
        String mimetype = null;
        try {
            mimetype = Files.probeContentType(file);
        } catch (IOException e) {
            LOGGER.warn("Can't decide if file " + file.getFileName() + " is image", e);
            return false;
        }

        return mimetype != null && mimetype.split("/")[0].equals("image");
    }
}
