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

public class AssetLoader
{


    public static BufferedImage loadImage(String fileName)
    {
        try
        {

            InputStream is = AssetLoader.class.getResourceAsStream("/assets/" + fileName);
            if (is != null)
            {
                return ImageIO.read(is);
            }
            else
            {
                System.err.println("Imagem não encontrada no caminho: /assets/" + fileName);

                System.err.println("ClassLoader paths: " +
                    System.getProperty("java.class.path"));
            }
        } catch (IOException e)
        {
            System.err.println("Erro ao carregar imagem: " + fileName);
            e.printStackTrace();
        }
        return null;
    }

    public static List<BufferedImage> loadAnimationFrames(String fileName)
    {
        List<BufferedImage> frames = new ArrayList<>();
        try (InputStream is = AssetLoader.class.getResourceAsStream("/assets/" + fileName))
        {
            if (is == null)
            {
                System.err.println("Erro: Não foi possível encontrar o arquivo de animação: " + fileName);
                return frames; // Retorna lista vazia
            }


            ImageInputStream iis = ImageIO.createImageInputStream(is);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (!readers.hasNext())
            {
                System.err.println("Nenhum leitor encontrado para: " + fileName);
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
            System.err.println("Erro ao carregar animação: " + fileName);
            e.printStackTrace();
            frames.clear(); 
        }
        return frames;
    }

    public static List<BufferedImage> loadSpritesheetFrames(String fileName, int frameWidth, int frameHeight, int numFrames /*, mais params? */) 
    {
        List<BufferedImage> frames = new ArrayList<>();
        BufferedImage sheet = loadImage(fileName);

        if (sheet == null) {
            return frames; // Retorna lista vazia se a folha não carregar
        }

        int currentX = 0;
        for (int i = 0; i < numFrames; i++)
        {
             if (currentX + frameWidth <= sheet.getWidth())
             {
                 BufferedImage frame = sheet.getSubimage(currentX, 0, frameWidth, frameHeight);
                 frames.add(frame);
                 currentX += frameWidth;
             } else {
                 System.err.println("Aviso: Spritesheet " + fileName + " não tem frames suficientes conforme esperado.");
                 break;
             }
        }
      

        return frames;
    }

}