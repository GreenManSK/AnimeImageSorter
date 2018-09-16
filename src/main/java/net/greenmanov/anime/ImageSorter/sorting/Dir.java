package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.json.AutosaveDatabase;
import net.greenmanov.anime.ImageSorter.json.JsonDatabase;

import java.io.IOException;
import java.nio.file.Path;

public class Dir {
    protected Path path;
    protected RuleSet ruleSet;
    protected JsonDatabase database;

    public Dir(Path path) throws IOException {
        this.path = path;
        load();
    }

    protected void load() throws IOException {
        database = new AutosaveDatabase(this.path.resolve(JsonDatabase.DEFAULT_NAME));
        ruleSet = RuleSet.loadRuleSet(this.path);
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
