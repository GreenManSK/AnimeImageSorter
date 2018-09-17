package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import net.greenmanov.iqdb.parsers.TagType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Set of rules
 */
public class RuleSet {
    public static final String RULE_TYPE = "type";
    public static final String RULE_PRIORITY = "priority";
    public static final String RULE_TAG_TYPE = "tagType";
    public static final String RULE_TAG_VALUE = "tagValue";

    public static final String DEFAULT_FILE = "filter.json";

    protected List<IRule> rules;

    public RuleSet() {
        rules = new ArrayList<>();
    }

    public void addRule(IRule rule) {
        rules.add(rule);
    }

    public boolean match(IRule rule) {
        return rules.contains(rule);
    }

    public IRule match(Image image) {
        IRule match = null;
        for (IRule rule : rules) {
            if (rule.match(image)) {
                match = match == null || match.getPriority() < rule.getPriority() ? rule : match;
            } else {
                return null;
            }
        }
        return match;
    }

    /**
     * Load rule set from file or directory
     *
     * @param path Path
     * @return RuleSet
     * @throws IOException
     */
    public static RuleSet loadRuleSet(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            path = path.resolve(DEFAULT_FILE);
        }
        RuleSet ruleSet = new RuleSet();
        JSONArray rules = new JSONArray(String.join("\n", Files.readAllLines(path, StandardCharsets.UTF_8)));
        for (int i = 0; i < rules.length(); i++) {
            JSONObject object = rules.getJSONObject(i);
            switch (object.getString(RULE_TYPE)) {
                case "ALL":
                    ruleSet.addRule(new RuleAll(object.optInt(RULE_PRIORITY, 0)));
                    break;
                case "NONE":
                    ruleSet.addRule(new RuleNone(object.optInt(RULE_PRIORITY, 0)));
                    break;
                case "TAG":
                    ruleSet.addRule(
                            new Rule(new Tag(TagType.valueOf(object.optString(RULE_TAG_TYPE)),
                                    object.optString(RULE_TAG_VALUE)),
                                    object.optInt(RULE_PRIORITY, 0))
                    );
                    break;
                case "MULTI_TAG":
                    JSONArray tags = object.getJSONArray(RULE_TAG_VALUE);
                    for (int j = 0; j < tags.length(); j++) {
                        String value = tags.getString(j);
                        ruleSet.addRule(
                                new Rule(new Tag(TagType.valueOf(object.optString(RULE_TAG_TYPE)), value),
                                        object.optInt(RULE_PRIORITY, 0))
                        );
                    }
                    break;
            }
        }
        return ruleSet;
    }

}
