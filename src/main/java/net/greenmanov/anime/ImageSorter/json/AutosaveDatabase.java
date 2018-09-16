package net.greenmanov.anime.ImageSorter.json;

import net.greenmanov.anime.ImageSorter.Image;

import java.io.IOException;
import java.nio.file.Path;

/**
 * JsonDatabase that autosave after each change
 */
public class AutosaveDatabase extends JsonDatabase {
    public AutosaveDatabase(Path file) throws IOException {
        super(file);
    }

    /**
     * Adds image to database
     *
     * @param image Image
     */
    @Override
    public void add(Image image) throws IOException {
        super.add(image);
        save();
    }

    /**
     * Remove image from database by file name if it is in database
     *
     * @param name File name
     */
    @Override
    public void remove(String name) throws IOException {
        super.remove(name);
        save();
    }
}
