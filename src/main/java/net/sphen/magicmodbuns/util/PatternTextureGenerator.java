package net.sphen.magicmodbuns.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraftforge.fml.loading.FMLPaths;
import net.sphen.magicmodbuns.screen.elements.Line;
import net.sphen.magicmodbuns.screen.elements.PatternObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class PatternTextureGenerator {
    private static final int SIZE = 64; // Texture size
    private static final int DOT_SPACING = SIZE / 6; // Spacing for 5x5 grid

    public static BufferedImage generateBufferedImage(PatternObject pattern) {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // Set up anti-aliasing for smooth lines
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transparent background
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, SIZE, SIZE);

        // Draw lines in white (chalk effect)
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3)); // Line thickness

        for (Line line : pattern.getLines()) {
            int startX = (line.start.gridX + 1) * DOT_SPACING;
            int startY = (line.start.gridY + 1) * DOT_SPACING;
            int endX = (line.end.gridX + 1) * DOT_SPACING;
            int endY = (line.end.gridY + 1) * DOT_SPACING;

            if (line.curved){
                // Calculate a control point — this creates a gentle arc
                int ctrlX = (startX + endX) / 2 + (startY - endY) / 4;
                int ctrlY = (startY + endY) / 2 + (endX - startX) / 4;

                // Compute the midpoint
                double midX = (startX + endX) / 2.0;
                double midY = (startY + endY) / 2.0;

                // Direction vector
                double dx = endX - startX;
                double dy = endY - startY;
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length == 0) continue; // Avoid zero-length lines

                // Perpendicular vector (flipped!)
                double normX = dy / length;
                double normY = -dx / length;

                // Curve offset strength — tweak this for curvature intensity
                double curveOffset = 6.0;

                // Control point for the quadratic curve
                double controlX = midX + normX * curveOffset;
                double controlY = midY + normY * curveOffset;

                // Use a GeneralPath to draw the curved line
                GeneralPath path = new GeneralPath();
                path.moveTo(startX, startY);
                path.quadTo(controlX, controlY, endX, endY);
                g.draw(path);
            } else {
                g.drawLine(startX, startY, endX, endY);
            }
        }

        g.dispose();

        return image;
    }

    public static NativeImage convertToNativeImage(BufferedImage image) {
        NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), true);
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                nativeImage.setPixelRGBA(x, y, pixels[y * image.getWidth() + x]);
            }
        }
        return nativeImage;
    }

    public static void saveTextureToFile(BufferedImage image, String fileName) {
        // This gets the main Minecraft directory (e.g., `.minecraft` or your modded instance folder)
        Path gameDir = FMLPaths.GAMEDIR.get();

        // You can also create your own subfolder within it, like "generated_textures"
        Path textureDir = gameDir.resolve("generated_textures");
        File dir = textureDir.toFile();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName + ".png");
        try {
            ImageIO.write(image, "PNG", file);
            System.out.println("Saved texture to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
