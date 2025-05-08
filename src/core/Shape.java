package core;

import core.objectsInterface.IShape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * The `Shape` class represents a graphical shape or animation in the game.
 * It supports single-frame and multi-frame animations with configurable frame
 * delays.
 *
 * @preConditions:
 *                 - For parameterized constructors, the frameDelayMillis must
 *                 be non-negative.
 *                 - For the list constructor, the frames list may be null or
 *                 empty (handled gracefully).
 *                 - For the single frame constructor, the frame must not be
 *                 null.
 *                 - For frame updates, the system time must be accessible.
 *
 * @postConditions:
 *                  - The Shape instance is initialized with at least an empty
 *                  frame list.
 *                  - The currentFrameIndex is set to 0.
 *                  - The lastFrameTime is set to the current system time.
 *                  - Frame animations cycle properly when updated.
 *                  - getCurrentFrame returns null only when frames list is
 *                  empty.
 *                  - getImagem always returns the current frame or null if no
 *                  frames exist.
 *
 * @author Brandon Mejia
 * @version 2025-03-25
 */
public class Shape implements IShape
{

    private List<BufferedImage> frames; // List of frames for the animation
    private int currentFrameIndex; // Index of the current frame being displayed

    private long lastFrameTime; // Timestamp of the last frame update
    private int frameDelayMillis; // Delay between frames in milliseconds

    private double logicalWidth; // Logical width of the shape
    private double logicalHeight; // Logical height of the shape



    /**
     * Validates the invariant for the `Shape` class.
     * Ensures that the provided list of frames is not null or empty.
     *
     * @param frames A list of `BufferedImage` objects representing the animation frames. Must not be null or empty.
     *
     * @throws IllegalArgumentException if the frames list is null or empty.
     */
    private void invariante(List<BufferedImage> frames)
    {
        if(frames == null || frames.isEmpty())
        {
            throw new IllegalArgumentException("Frames list cannot be null or empty.");
        }
    }


    /**
     * Constructs a `Shape` with a list of frames and a frame delay.
     *
     * @param other A `Shape` object to copy frames from.
     */
    public Shape(Shape other)
    {

        this.frames = new ArrayList<>(other.frames);
        this.currentFrameIndex = other.currentFrameIndex;
        this.frameDelayMillis = other.frameDelayMillis;
        this.lastFrameTime = other.lastFrameTime;
    }

    /**
     * Constructs a `Shape` with a list of frames and a frame delay.
     *
     * @param frames           A list of `BufferedImage` objects representing the
     *                         animation frames.
     * @param frameDelayMillis The delay between frames in milliseconds.
     */
    public Shape(List<BufferedImage> frames, int frameDelayMillis)
    {
        invariante(frames);

        this.frames = new ArrayList<>();
        this.frames = frames;

        this.currentFrameIndex = 0;
        this.frameDelayMillis = frameDelayMillis;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Constructs a `Shape` with a single frame and a frame delay.
     *
     * @param frames           A single `BufferedImage` object representing the
     *                         frame.
     * @param frameDelayMillis The delay between frames in milliseconds.
     */
    public Shape(BufferedImage frames, int frameDelayMillis)
    {
        if (frames == null)
        {
            throw new IllegalArgumentException("Frame cannot be null.");
        }

        this.frames = new ArrayList<>();
        this.frames.add(frames);
        this.currentFrameIndex = 0;
        this.frameDelayMillis = frameDelayMillis;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Sets the logical width and height of the shape.
     *
     * @param width  The logical width to set.
     * @param height The logical height to set.
     */
    public void setlogicalWidthAndHeight(double width, double height) {
        this.logicalWidth = width;
        this.logicalHeight = height;
    }

    /**
     * Retrieves the current frame of the animation.
     *
     * @return The current `BufferedImage` frame, or `null` if no frames are
     *         available.
     */
    private BufferedImage getCurrentFrame() {
        if (frames.isEmpty()) {
            return null;
        }
        return frames.get(currentFrameIndex);
    }

    /**
     * Updates the animation logic. This method should be called during each game
     * loop cycle.
     */
    public void updateAnimation() {
        if (frames.size() <= 1 || frameDelayMillis <= 0)
            return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDelayMillis) {
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
            lastFrameTime = currentTime;
        }
    }

    /**
     * Retrieves the list of animation frames.
     *
     * @return A list of `BufferedImage` objects representing the animation frames.
     */
    public List<BufferedImage> getFrames() {
        return frames;
    }

    /**
     * Sets the list of animation frames and resets the current frame index.
     *
     * @param frames           A list of `BufferedImage` objects representing the
     *                         animation frames.
     * @param frameDelayMillis The delay between frames in milliseconds.
     */
    public void setFrames(List<BufferedImage> frames, int frameDelayMillis) {
        this.frames = frames;
        this.currentFrameIndex = 0;
        this.frameDelayMillis = frameDelayMillis;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Retrieves the current image of the shape.
     *
     * @return The current `BufferedImage` frame of the shape.
     */
    @Override
    public BufferedImage getImagem() {
        return getCurrentFrame();
    }

    /**
     * Retrieves the logical width of the shape.
     *
     * @return The logical width of the shape.
     */
    public double getLogicalWidth() {
        return logicalWidth;
    }

    /**
     * Retrieves the logical height of the shape.
     *
     * @return The logical height of the shape.
     */
    public double getLogicalHeight() {
        return logicalHeight;
    }

}