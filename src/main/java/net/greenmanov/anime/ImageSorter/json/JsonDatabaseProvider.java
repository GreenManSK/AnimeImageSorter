package net.greenmanov.anime.ImageSorter.json;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonDatabaseProvider {

    private static Map<Path, JSONArray> databaseJsons = new HashMap<>();

    private JsonDatabaseProvider() {}

    public static JsonDatabase getDatabase(Path path) throws IOException {
        if (databaseJsons.containsKey(path)) {
            return new JsonDatabase(path, databaseJsons.get(path));
        }
        JsonDatabase db = new JsonDatabase(path);

        databaseJsons.put(path, db.images);

        return db;
    }

    public static AutosaveDatabase getAutosaveDatabase(Path path) throws IOException {
        if (databaseJsons.containsKey(path)) {
            return new AutosaveDatabase(path, databaseJsons.get(path));
        }
        AutosaveDatabase db = new AutosaveDatabase(path);

        databaseJsons.put(path, db.images);

        return db;
    }
}
