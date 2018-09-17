package net.greenmanov.anime.ImageSorter.json;

import net.greenmanov.anime.ImageSorter.Image;
import net.greenmanov.iqdb.parsers.Tag;
import net.greenmanov.iqdb.parsers.TagType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Convertor between JSONObjcets and objects used by iqdb
 */
public class JsonConvertor {
    public static final String TAG_NAME = "name";
    public static final String TAG_TYPE = "type";

    public static final String IMAGE_NAME = "name";
    public static final String IMAGE_DATE = "date";
    public static final String IMAGE_SOURCE = "source";
    public static final String IMAGE_INFO_SOURCE = "infoSource";
    public static final String IMAGE_TAGS = "tags";
    public static final String IMAGE_WIDTH = "width";
    public static final String IMAGE_HEIGHT = "height";
    public static final String IMAGE_SIZE = "size";

    /**
     * Converts Tag to json
     *
     * @param tag Tag
     * @return json
     */
    public static JSONObject toJson(Tag tag) {
        JSONObject object = new JSONObject();
        object.put(TAG_NAME, tag.getValue().replace(' ', '_'));
        object.put(TAG_TYPE, tag.getTag().toString());
        return object;
    }

    /**
     * Converts json to tag
     *
     * @param json Json
     * @return tag
     */
    public static Tag toTag(JSONObject json) {
        return new Tag(
                TagType.valueOf(json.getString(TAG_TYPE)),
                json.getString(TAG_NAME).replace(' ', '_')
        );
    }

    /**
     * Converts Image to json
     *
     * @param image Image
     * @return json
     */
    public static JSONObject toJson(Image image) {
        JSONObject object = new JSONObject();
        object.put(IMAGE_NAME, image.getName());
        object.put(IMAGE_DATE, image.getDate().getTime());
        object.put(IMAGE_SOURCE, image.getSource());
        object.put(IMAGE_INFO_SOURCE, image.getInfoSource());
        object.put(IMAGE_WIDTH, image.getWidth());
        object.put(IMAGE_HEIGHT, image.getHeight());
        object.put(IMAGE_SIZE, image.getSize());
        JSONArray tags = new JSONArray();
        if (image.getTags() != null) {
            for (Tag tag : image.getTags()) {
                tags.put(toJson(tag));
            }
        }
        object.put(IMAGE_TAGS, tags);
        return object;
    }

    /**
     * Converts json to Image
     *
     * @param json Json
     * @return Image
     */
    public static Image toImage(JSONObject json) {
        Image image = new Image();
        image.setName(json.getString(IMAGE_NAME));
        image.setDate(new Date(json.getLong(IMAGE_DATE)));
        if (json.has(IMAGE_SOURCE)) {
            image.setSource(json.getString(IMAGE_SOURCE));
        }
        if (json.has(IMAGE_INFO_SOURCE)) {
            image.setInfoSource(json.getString(IMAGE_INFO_SOURCE));
        }
        if (json.has(IMAGE_WIDTH)) {
            image.setWidth(json.getInt(IMAGE_WIDTH));
        }
        if (json.has(IMAGE_HEIGHT)) {
            image.setHeight(json.getInt(IMAGE_HEIGHT));
        }
        if (json.has(IMAGE_SIZE)) {
            image.setSize(json.getLong(IMAGE_SIZE));
        }

        List<Tag> tags = new ArrayList<>();
        JSONArray jsonTags = json.getJSONArray(IMAGE_TAGS);
        jsonTags.forEach(jsonTag -> tags.add(toTag((JSONObject) jsonTag)));
        image.setTags(tags);

        return image;
    }
}
