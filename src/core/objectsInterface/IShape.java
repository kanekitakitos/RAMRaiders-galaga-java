package core.objectsInterface;
import java.awt.image.BufferedImage;
import java.util.List;


public interface IShape
{
    void updateAnimation();

    BufferedImage getImagem();

    List<BufferedImage> getFrames();

    /**
     * Sets the list of animation frames and resets the current frame index.
     *
     * @param frames A list of `BufferedImage` objects representing the animation frames.
     * @param frameDelayMillis The delay between frames in milliseconds.
     */
    public void setFrames(List<BufferedImage> frames, int frameDelayMillis);
}
