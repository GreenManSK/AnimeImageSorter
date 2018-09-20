package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import net.greenmanov.iqdb.parsers.TagType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Match across multiple tags in one category only if there is no other tag in that category for image
 */
public class MultiTagOnlyRule extends MultiTagRule {
    public MultiTagOnlyRule(List<TagRule> rules, int priority) {
        super(rules, priority);
    }

    @Override
    public boolean match(Tag tag) {
        return false;
    }

    @Override
    public boolean match(Image image) {
        if (rules.isEmpty())
            return false;
        final TagType tagType = rules.get(0).tag.getTag();
        List<Tag> tags = image.getTags().stream().filter(t -> t.getTag() == tagType).collect(Collectors.toList());
        for (TagRule rule : rules) {
            tags.remove(rule.tag);
        }
        return tags.isEmpty();
    }
}
