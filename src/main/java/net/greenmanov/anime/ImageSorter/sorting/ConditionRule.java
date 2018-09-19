package net.greenmanov.anime.ImageSorter.sorting;

import net.greenmanov.anime.ImageSorter.helpers.Image;
import net.greenmanov.iqdb.parsers.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringWriter;

public class ConditionRule implements IRule {
    private static final Logger LOGGER = LogManager.getLogger(ConditionRule.class.getName());

    private String rule;
    private int priority;
    private static final ScriptEngine engine;

    static {
        engine = new ScriptEngineManager().getEngineByName("javascript");
        ScriptContext context = engine.getContext();
        StringWriter writer = new StringWriter();
        context.setWriter(writer);
    }

    public ConditionRule(String rule, int priority) {
        this.rule = rule;
        this.priority = priority;
    }

    @Override
    public boolean match(Tag tag) {
        return false;
    }

    @Override
    public boolean match(Image image) {
        engine.put("width", image.getWidth());
        engine.put("height", image.getHeight());
        engine.put("size", image.getSize());
        try {
            return (boolean) engine.eval(this.rule);
        } catch (ScriptException e) {
            LOGGER.error("Can't evaluate condition '" + rule + "'", e);
        }
        return false;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
