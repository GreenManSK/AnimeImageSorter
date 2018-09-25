package net.greenmanov.anime.ImageSorter.helpers;

import net.greenmanov.iqdb.parsers.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Image {
    private static final Logger LOGGER = LogManager.getLogger(Image.class.getName());
    protected String name;
    protected Date date;
    protected String source;
    protected String infoSource;
    protected List<Tag> tags;
    protected int width, height;
    protected long size;

    public Image() {
    }

    public Image(Path file, Date date, String source, String infoSource, List<Tag> tags) {
        loadFileData(file);
        this.date = date;
        this.source = source;
        this.infoSource = infoSource;
        this.tags = tags;
    }

    protected void loadFileData(Path file) {
        this.name = file.getFileName().toString();
        this.size = file.toFile().length();
        try {
            BufferedImage image = ImageIO.read(file.toFile());
            this.width = image.getWidth();
            this.height = image.getHeight();
        } catch (IOException|IllegalArgumentException e) {
            LOGGER.warn("Can't get resolution of file " + this.name, e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(String infoSource) {
        this.infoSource = infoSource;
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return size == image.size &&
                Objects.equals(name, image.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size);
    }
}
