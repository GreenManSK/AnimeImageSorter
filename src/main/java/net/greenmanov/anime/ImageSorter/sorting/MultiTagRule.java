package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import net.greenmanov.iqdb.parsers.TagType;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.greenmanov.anime.ImageSorter.sorting.TagRule.RULE_TAG_TYPE;
import static net.greenmanov.anime.ImageSorter.sorting.TagRule.RULE_TAG_VALUE;

/**
 * Match across multiple tags in one category with OR
 */
public class MultiTagRule implements IRule {
    protected List<TagRule> rules;
    protected int priority;

    public MultiTagRule(List<TagRule> rules, int priority) {
        this.rules = rules;
        this.priority = priority;
    }

    @Override
    public boolean match(Tag tag) {
        for (IRule rule : rules)
            if (rule.match(tag))
                return true;
        return false;
    }

    @Override
    public boolean match(Image image) {
        for (IRule rule : rules)
            if (rule.match(image))
                return true;
        return false;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public static MultiTagRule fromJson(JSONObject object) {
        List<String> tags =
                Arrays.stream(object.getString(RULE_TAG_VALUE).split(",")).map(String::trim).map(String::toLowerCase
        ).collect(Collectors.toList());
        List<TagRule> rules = new ArrayList<>();
        for (String tag : tags) {
            rules.add(
                    new TagRule(new Tag(TagType.valueOf(object.optString(RULE_TAG_TYPE)), tag),
                            object.optInt(RULE_PRIORITY, 0))
            );
        }
        return new MultiTagRule(rules, object.optInt(RULE_PRIORITY, 0));
    }
}
