package core.objectsInterface;
import java.awt.image.BufferedImage;
import java.util.List;
/**
 * The IShape interface defines the structure for objects that have
 * animation capabilities and can provide image frames for rendering.
 *
 * @author Brandon Mejia
 * @version 2025-05-07
 */
public interface IShape
{
    /**
     * Updates the animation state of the object. This method should be
     * called periodically to advance the animation frames.
     */
    void updateAnimation();

    /**
     * Retrieves the current image of the object. This image represents
     * the current frame of the animation.
     *
     * @return A BufferedImage object representing the current frame.
     */
    BufferedImage getImagem();

    /**
     * Retrieves the list of animation frames associated with the object.
     *
     * @return A list of BufferedImage objects representing the animation frames.
     */
    List<BufferedImage> getFrames();

    /**
     * Sets the list of animation frames and resets the current frame index.
     *
     * @param frames A list of BufferedImage objects representing the animation frames.
     * @param frameDelayMillis The delay between frames in milliseconds.
     */
    void setFrames(List<BufferedImage> frames, int frameDelayMillis);

    /**
     * Sets the logical width and height of the shape.
     *
     * @param width  The logical width to set.
     * @param height The logical height to set.
     */
    void setlogicalWidthAndHeight(double width, double height);
}
