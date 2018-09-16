package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.Image;
import net.greenmanov.iqdb.parsers.Tag;

final public class RuleAll implements IRule {

    protected int priority;

    public RuleAll(int priority) {
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
}
