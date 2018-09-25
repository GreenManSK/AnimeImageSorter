package net.greenmanov.anime.ImageSorter.json;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Path;

/**
 * JsonDatabase that autosave after each change
 */
public class AutosaveDatabase extends JsonDatabase {
    AutosaveDatabase(Path file) throws IOException {
        super(file);
    }

    public AutosaveDatabase(Path file, JSONArray images) {
        super(file, images);
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
