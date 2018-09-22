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
//                match = match == null || match.getPriority() < rule.getPriority() ? rule : match;
                if (match == null || match.getPriority() < rule.getPriority()) {
                    match = rule;
                }
            } else {
                return null;
            }
        }
        return match;
    }

    /**
     * Adds all rules from set into this set
     * @param ruleSet RuleSet
     */
    public void add(RuleSet ruleSet) {
        this.rules.addAll(ruleSet.rules);
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
                    ruleSet.addRule(AllRule.fromJson(object));
                    break;
                case "NONE":
                    ruleSet.addRule(NoneRule.fromJson(object));
                    break;
                case "TAG":
                    ruleSet.addRule(TagRule.fromJson(object));
                    break;
                case "MULTI_TAG":
                    ruleSet.addRule(MultiTagRule.fromJson(object));
                    break;
                case "MULTI_TAG_AND":
                    ruleSet.addRule(MultiTagAndRule.fromJson(object));
                    break;
                case "MULTI_TAG_ONLY":
                    ruleSet.addRule(MultiTagOnlyRule.fromJson(object));
                    break;
                case "CONDITION":
                    ruleSet.addRule(ConditionRule.fromJson(object));
                    break;
            }
        }
        return ruleSet;
    }

    public int size() {
        return rules.size();
    }

}
