package core.objectsInterface;
import java.awt.image.BufferedImage;
import java.util.List;


public interface IShape
{
    void updateAnimation();

    BufferedImage getImagem();

    List<BufferedImage> getFrames();

    void setFrames(List<BufferedImage> frames);
}
