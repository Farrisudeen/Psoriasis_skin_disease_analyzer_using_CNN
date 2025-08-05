package com.example.skindetect.util;

import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class ImagePreprocessor {
    public static TFloat32 preprocess(Path imagePath) throws Exception {
        BufferedImage img = ImageIO.read(imagePath.toFile());
        BufferedImage resized = new BufferedImage(224, 224, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(img, 0, 0, 224, 224, null);
        g2d.dispose();

        float[] data = new float[1 * 224 * 224 * 3];
        int idx = 0;
        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int rgb = resized.getRGB(x, y);
                data[idx++] = ((rgb >> 16) & 0xFF) / 255f;
                data[idx++] = ((rgb >> 8) & 0xFF) / 255f;
                data[idx++] = (rgb & 0xFF) / 255f;
            }
        }
        return TFloat32.tensorOf(Shape.of(1, 224, 224, 3), dataBuffer -> dataBuffer.write(data));
    }
}
