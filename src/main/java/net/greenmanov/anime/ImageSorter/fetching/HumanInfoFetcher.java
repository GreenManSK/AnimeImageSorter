package net.greenmanov.anime.ImageSorter.fetching;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.impl.DynamicParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class HumanInfoFetcher extends AFetcher {
    private static final Logger LOGGER = LogManager.getLogger(HumanInfoFetcher.class.getName());

    public HumanInfoFetcher() {
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
        System.out.println("Image: " + filePath.getFileName());
        ImageAction action;
        do {
            action = getImageAction(filePath);
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
    }

    /**
     * Adds image to database
     *
     * @param file Image file
     * @param to   Dir for added image
     */
    protected void addImage(Path file, Path to) {
        char c = 0;
        do {
            System.out.println("Use [U]RL or [T]ype [T]ags");
            try {
                c = (char) System.in.read();
                c = Character.toLowerCase(c);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        } while (c != 'u' && c != 't');
        if (c == 'u') {
            addByUrl(file, to);
        } else {

        }
    }

    /**
     * Add image with fetching tags from
     *
     * @param file Image file
     * @param to   Dir for added image
     */
    protected void addByUrl(Path file, Path to) {
        try (Scanner sc = new Scanner(System.in)) {
            do {
                System.out.println("URL: ");
                System.out.flush();
                cleanSc(sc);
                String url = sc.nextLine();
                if (url.isEmpty()) {
                    return;
                }
                DynamicParser parser = new DynamicParser();
//                try {
                    System.out.println(url);
//                    parser.parse(url);
//                    Image image = new Image(file, now(), parser.getSource(), parser.getImage(), parser.getTags());
//                    database.add(image);
//                    if (to != null) {
//                        moveFile(file, to);
//                    }
                    return;
//                } catch (IOException e) {
//                    LOGGER.warn("Can't parse URL " + url, e);
//                }
            } while (true);
        }
    }

    /**
     * Gets action for image
     *
     * @param filePath image file
     * @return open, skip or add
     */
    protected ImageAction getImageAction(Path filePath) {
        while (true) {
            System.out.println("[A]DD, [S]KIP, [O]PEN");
            char c;
            try {
                System.out.flush();
                c = (char) System.in.read();
                c = Character.toLowerCase(c);
                switch (c) {
                    case 'a':
                        return ImageAction.ADD;
                    case 's':
                        return ImageAction.SKIP;
                    case 'o':
                        return ImageAction.OPEN;
                }
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    protected void cleanSc(Scanner sc) {
        while (sc.hasNext()) {
            sc.next();
        }
    }

    protected enum ImageAction {
        ADD, SKIP, OPEN
    }
}
