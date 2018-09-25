package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.json.JsonDatabase;
import net.greenmanov.anime.ImageSorter.json.JsonDatabaseProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Path;

public class Dir {
    private static final Logger LOGGER = LogManager.getLogger(Dir.class.getName());
    protected Path path;
    protected RuleSet ruleSet;
    protected JsonDatabase database;

    public Dir(Path path) throws IOException {
        this.path = path;
        load();
    }

    protected void load() throws IOException {
        database = JsonDatabaseProvider.getAutosaveDatabase(this.path.resolve(JsonDatabase.DEFAULT_NAME));
        try {
            ruleSet = RuleSet.loadRuleSet(this.path);
        } catch (JSONException e) {
            LOGGER.error("Invalid " + JsonDatabase.DEFAULT_NAME + " in path " + path,e);
            throw e;
        }
    }

    public Path getPath() {
        return path;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public JsonDatabase getDatabase() {
        return database;
    }
}
