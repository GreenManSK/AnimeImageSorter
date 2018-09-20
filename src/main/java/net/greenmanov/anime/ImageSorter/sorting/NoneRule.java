package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import org.json.JSONObject;

public class NoneRule implements IRule {

    protected int priority;

    public NoneRule(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean match(Tag tag) {
        return false;
    }

    @Override
    public boolean match(Image image) {
        return false;
    }

    public static NoneRule fromJson(JSONObject object) {
        return new NoneRule(object.optInt(RULE_PRIORITY, 0));
    }
}
