package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import org.json.JSONObject;

final public class AllRule implements IRule {

    protected int priority;

    public AllRule(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean match(Tag tag) {
        return true;
    }

    @Override
    public boolean match(Image image) {
        return true;
    }

    public static AllRule fromJson(JSONObject object) {
        return new AllRule(object.optInt(RULE_PRIORITY, 0));
    }
}
