package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.Image;
import net.greenmanov.iqdb.parsers.Tag;

public interface IRule {
    boolean match(Tag tag);

    boolean match(Image image);

    int getPriority();
}
