package net.greenmanov.anime.ImageSorter.json;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Json image database containing information about images in the file
 */
public class JsonDatabase {
    public static final String DEFAULT_NAME = ".database";
    protected static final int INDENT = 2;


    protected Path file;
    protected JSONArray images;

    /**
     * File path of the database
     *
     * @param file File path
     * @throws IOException if there is any problem with loading or creating json file
     */
    public JsonDatabase(Path file) throws IOException {
        this.file = file;
        if (Files.exists(file)) {
            loadFile(file);
        } else {
            createNewFile(file);
        }
    }

    /**
     * Creates file for saving json and creates json object for the object
     *
     * @param file File path
     * @throws IOException If any problem with creating the file
     */
    protected void createNewFile(Path file) throws IOException {
        images = new JSONArray();
        Files.createFile(file);
        save();
    }

    /**
     * Loads json from the file
     *
     * @param file File path
     * @throws IOException If any problem with reading the file
     */
    protected void loadFile(Path file) throws IOException {
        images = new JSONArray(String.join("\n", Files.readAllLines(file, StandardCharsets.UTF_8)));
    }

    /**
     * Save actual json content into file
     *
     * @throws IOException If any problem with saving the file
     */
    public void save() throws IOException {
        save(false);
    }

    /**
     * Save actual json content into file
     *
     * @param useIndent Specify if file content should be indented
     * @throws IOException If any problem with saving the file
     */
    public void save(boolean useIndent) throws IOException {
        Files.write(file, images.toString(useIndent ? INDENT : 0).getBytes());
    }

    /**
     * Adds image to database
     *
     * @param image Image
     */
    public void add(Image image) throws IOException {
        images.put(JsonConvertor.toJson(image));
    }

    /**
     * Gets image by file name
     *
     * @param name File name
     * @return Image or null if no info for this file exists
     */
    public Image get(String name) {
        for (Object object : images) {
            JSONObject json = (JSONObject) object;
            if (name.equals(json.getString(JsonConvertor.IMAGE_NAME))) {
                return JsonConvertor.toImage(json);
            }
        }
        return null;
    }

    /**
     * Gets image by file name
     *
     * @param file File
     * @return Image or null if no info for this file exists
     */
    public Image get(Path file) {
        return get(file.getFileName().toString());
    }

    /**
     * Remove image from database by file name if it is in database
     *
     * @param name File name
     */
    public void remove(String name) throws IOException {
        for (int i = 0; i < images.length(); i++) {
            JSONObject json = images.getJSONObject(i);
            if (name.equals(json.getString(JsonConvertor.IMAGE_NAME))) {
                images.remove(i);
                return;
            }
        }
    }
}
