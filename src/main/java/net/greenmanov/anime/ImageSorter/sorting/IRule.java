package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;

public interface IRule {

    String RULE_PRIORITY = "priority";

    boolean match(Tag tag);

    boolean match(Image image);

    int getPriority();
}
