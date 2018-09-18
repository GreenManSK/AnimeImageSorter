package net.greenmanov.anime.ImageSorter.fetching;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import net.greenmanov.iqdb.parsers.TagType;
import net.greenmanov.iqdb.parsers.impl.DynamicParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class HumanInfoFetcher extends AFetcher {
    private static final Logger LOGGER = LogManager.getLogger(HumanInfoFetcher.class.getName());
    protected TextIO textIO;
    protected TextTerminal textTerminal;

    public HumanInfoFetcher() {
        textIO = TextIoFactory.getTextIO();
        textTerminal = textIO.getTextTerminal();
    }

    /**
     * Fetch info about file and moves it if needed
     *
     * @param filePath      Path to file
     * @param to            Dir for parsed files
     * @param minSimilarity Minimal similarity to be considered info match
     * @param noMatchDir    Dir for files without match, can be null
     */
    @Override
    protected void fetchFile(Path filePath, Path to, int minSimilarity, Path noMatchDir) throws InterruptedException {
        textTerminal.println("Image: " + filePath.getFileName());
        ImageAction action;
        do {
            action = getImageAction();
            if (action == ImageAction.OPEN) {
                try {
                    Desktop.getDesktop().open(filePath.toFile());
                } catch (IOException e) {
                    LOGGER.error("Can't open file " + filePath, e);
                }
            }
        } while (action == ImageAction.OPEN);
        if (action == ImageAction.ADD) {
            addImage(filePath, to);
        } else if (noMatchDir != null) {
            try {
                moveFile(filePath, noMatchDir);
            } catch (IOException e) {
                LOGGER.error("Can't move file " + filePath + " to " + noMatchDir, e);
            }
        }
        textIO.dispose();
    }

    /**
     * Adds image to database
     *
     * @param file Image file
     * @param to   Dir for added image
     */
    protected void addImage(Path file, Path to) {
        char c;
        do {
            textTerminal.println("Use [U]RL or [T]ype [T]ags");
            c = textIO.newCharInputReader().read();
            c = Character.toLowerCase(c);
        } while (c != 'u' && c != 't');
        if (c == 'u') {
            addByUrl(file, to);
        } else {
            addByHand(file, to);
        }
    }
    /**
     * Add image with fetching tags from human being
     *
     * @param file Image file
     * @param to   Dir for added image
     */
    protected void addByHand(Path file, Path to) {
        List<Tag> tags = new ArrayList<>();
        TagType action = null;
        do {
            action = textIO.newEnumInputReader(TagType.class).read("Tag type:");
            String tagInput = textIO.newStringInputReader().withMinLength(0).read("Tags[,]:");
            if (!tagInput.isEmpty()) {
                final TagType tagType = action;
                Arrays.stream(tagInput.split(",")).map(String::trim).forEach(s -> tags.add(new Tag(tagType, s)));
            } else {
                action = null;
            }
        } while (action != null);
        Image image = new Image(file, now(), null, null, tags);
        try {
            database.add(image);
        } catch (IOException e) {
            LOGGER.error("Can't save database!", e);
        }
        if (to != null) {
            try {
                moveFile(file, to);
            } catch (IOException e) {
                LOGGER.error("Can't move image " + file, e);
            }
        }

    }

    /**
     * Add image with fetching tags from URL
     *
     * @param file Image file
     * @param to   Dir for added image
     */
    protected void addByUrl(Path file, Path to) {
        do {
            String url = textIO
                    .newStringInputReader()
                    .withMinLength(0)
                    .read("URL:");
            if (url.isEmpty()) {
                return;
            }
            DynamicParser parser = new DynamicParser();
            try {
                parser.parse(url);
                Image image = new Image(file, now(), parser.getSource(), parser.getImage(), parser.getTags());
                database.add(image);
                if (to != null) {
                    moveFile(file, to);
                }
                return;
            } catch (IOException e) {
                LOGGER.warn("Can't parse URL " + url, e);
                textTerminal.println("Can't parse URL " + url);
            }
        } while (true);
    }

    /**
     * Gets action for image
     *
     * @return open, skip or add
     */
    protected ImageAction getImageAction() {
        while (true) {
            textTerminal.println("[A]DD, [S]KIP, [O]PEN");
            char c;
            c = textIO.newCharInputReader().read();
            c = Character.toLowerCase(c);
            switch (c) {
                case 'a':
                    return ImageAction.ADD;
                case 's':
                    return ImageAction.SKIP;
                case 'o':
                    return ImageAction.OPEN;
            }
        }
    }

    protected enum ImageAction {
        ADD, SKIP, OPEN
    }
}
