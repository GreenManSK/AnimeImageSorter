package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.Image;
import net.greenmanov.iqdb.parsers.Tag;

public class RuleNone implements IRule {

    protected int priority;

    public RuleNone(int priority) {
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
}
