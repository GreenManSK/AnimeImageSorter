package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.Image;
import net.greenmanov.iqdb.parsers.Tag;

import java.util.Objects;

public class Rule implements IRule {
    protected Tag tag;
    protected int priority;

    public Rule(Tag tag, int priority) {
        this.tag = tag;
        this.priority = priority;
    }

    public boolean match(Tag tag) {
        return tag.equals(this.tag);
    }

    public boolean match(Image image) {
        return image.getTags().stream().anyMatch(this::match);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return match(rule.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, priority);
    }
}
