package net.greenmanov.anime.ImageSorter.helpers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Class used for resizing of images
 */
public class ImageResizer {
    /**
     * Resize image and saves it into temporary file
     *
     * @param image        Path to image file
     * @param maxDimension Maximal dimension of image, other will be computed to save image ratio
     * @return File with resized image
     */
    public static File resizeImage(Path image, int maxDimension) throws IOException {
        BufferedImage inputImage = ImageIO.read(image.toFile());
        if (inputImage == null)
            return null;

        int width, height;
        if (inputImage.getWidth() >= inputImage.getHeight()) {
            width = maxDimension;
            height = (width * inputImage.getHeight() / inputImage.getWidth());
        } else {
            height = maxDimension;
            width = (height * inputImage.getWidth() / inputImage.getHeight());
        }

        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, width, height, null);
        g2d.dispose();

        String imageName = image.getFileName().toString();
        String formatName = imageName.substring(imageName.lastIndexOf(".") + 1);

        File temp = File.createTempFile(imageName, "." + formatName);
        ImageIO.write(outputImage, formatName, temp);

        return temp;
    }
}
