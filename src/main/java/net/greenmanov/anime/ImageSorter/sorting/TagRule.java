package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import net.greenmanov.iqdb.parsers.TagType;
import org.json.JSONObject;

import java.util.Objects;


public class TagRule implements IRule {
    public static final String RULE_TAG_TYPE = "tagType";
    public static final String RULE_TAG_VALUE = "tagValue";

    protected Tag tag;
    protected int priority;

    public TagRule(Tag tag, int priority) {
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
        TagRule rule = (TagRule) o;
        return match(rule.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, priority);
    }

    public static TagRule fromJson(JSONObject object) {
        return new TagRule(new Tag(TagType.valueOf(object.optString(RULE_TAG_TYPE)),
                object.optString(RULE_TAG_VALUE)),
                object.optInt(RULE_PRIORITY, 0));
    }
}
