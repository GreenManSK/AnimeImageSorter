package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import org.json.JSONObject;

import java.util.List;

/**
 * Match across multiple tags in one category with AND
 */
public class MultiTagAndRule extends MultiTagRule {
    public MultiTagAndRule(List<TagRule> rules, int priority) {
        super(rules, priority);
    }

    @Override
    public boolean match(Tag tag) {
        return false;
    }

    @Override
    public boolean match(Image image) {
        boolean result = true;
        for (IRule rule : rules) {
            result = result && rule.match(image);
        }
        return result;
    }

    public static MultiTagRule fromJson(JSONObject object) {
        MultiTagRule rule = MultiTagRule.fromJson(object);
        return new MultiTagAndRule(rule.rules, rule.priority);
    }
}
