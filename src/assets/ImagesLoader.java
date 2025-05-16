package assets;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for loading game assets like images, animations, and
 * spritesheets.
 * Provides static methods to load single images, animation frames, and
 * spritesheet frames.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * BufferedImage image = AssetLoader.loadImage("player.png");
 * List<BufferedImage> animation = AssetLoader.loadAnimationFrames("explosion.gif");
 * List<BufferedImage> spritesheet = AssetLoader.loadSpritesheetFrames("tiles.png", 32, 32, 10);
 * </pre>
 *
 * @preConditions:
 *                 - Asset files must exist in the `/assets/` directory.
 *                 - File names must be valid and include file extensions.
 *                 - For spritesheets, frameWidth and frameHeight must be
 *                 positive integers.
 *                 - For spritesheets, numFrames must be positive and not exceed
 *                 available frames.
 *
 * @postConditions:
 *                  - Successfully loaded images will be returned as
 *                  BufferedImage objects.
 *                  - Failed loads will return null for single images or empty
 *                  lists for animations.
 *                  - Loaded animations/spritesheets will maintain frame order.
 *                  - All resources will be properly closed after loading.
 *
 * @author Brandon Mejia
 * @version 2025-05-06
 */
public class ImagesLoader {

    /**
     * Loads a single image from the assets directory.
     *
     * @param fileName The name of the image file to load.
     * @return The loaded image as a BufferedImage, or null if loading fails.
     */
    public static BufferedImage loadImage(String fileName) {
        try {
            InputStream is = ImagesLoader.class.getResourceAsStream("/assets/images/" + fileName);
            if (is != null) {
                return ImageIO.read(is);
            } else {
                System.err.println("Image not found at path: /assets/images" + fileName);
                System.err.println("ClassLoader paths: " + System.getProperty("java.class.path"));
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + fileName);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads frames from an animated image file.
     *
     * @param fileName The name of the animation file to load.
     * @return A list of animation frames as BufferedImages.
     */
    public static List<BufferedImage> loadAnimationFrames(String fileName) {
        List<BufferedImage> frames = new ArrayList<>();
        try (InputStream is = ImagesLoader.class.getResourceAsStream("/assets/images/" + fileName)) {
            if (is == null) {
                System.err.println("Error: Could not find animation file: " + fileName);
                return frames;
            }

            ImageInputStream iis = ImageIO.createImageInputStream(is);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (!readers.hasNext()) {
                System.err.println("No reader found for: " + fileName);
                iis.close();
                return frames;
            }

            ImageReader reader = readers.next();
            reader.setInput(iis);

            int numFrames = reader.getNumImages(true);

            for (int i = 0; i < numFrames; i++) {
                BufferedImage frame = reader.read(i);
                frames.add(frame);
            }

            reader.dispose();
            iis.close();

        } catch (IOException e) {
            System.err.println("Error loading animation: " + fileName);
            e.printStackTrace();
            frames.clear();
        }
        return frames;
    }

    /**
     * Extracts frames from a spritesheet image.
     *
     * @param fileName    The name of the spritesheet file.
     * @param frameWidth  The width of each frame in pixels.
     * @param frameHeight The height of each frame in pixels.
     * @param numFrames   The number of frames to extract.
     * @return A list of extracted frames as BufferedImages.
     */
    public static List<BufferedImage> loadSpritesheetFrames(String fileName, int frameWidth, int frameHeight,
            int numFrames) {
        List<BufferedImage> frames = new ArrayList<>();
        BufferedImage sheet = loadImage(fileName);

        if (sheet == null) {
            return frames;
        }

        int currentX = 0;
        for (int i = 0; i < numFrames; i++) {
            if (currentX + frameWidth <= sheet.getWidth()) {
                BufferedImage frame = sheet.getSubimage(currentX, 0, frameWidth, frameHeight);
                frames.add(frame);
                currentX += frameWidth;
            } else {
                System.err.println("Warning: Spritesheet " + fileName + " does not have enough frames as expected.");
                break;
            }
        }

        return frames;
    }
}