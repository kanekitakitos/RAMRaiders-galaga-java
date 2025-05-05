package assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class AssetLoader {

    /**
     * Carrega uma imagem da pasta assets pelo nome do arquivo.
     * Exemplo de uso: AssetLoader.loadImage("nave.png")
     *
     * @param fileName Nome do arquivo dentro da pasta assets.
     * @return BufferedImage da imagem carregada, ou null se não encontrar.
     */
    public static BufferedImage loadImage(String fileName)
    {
        try {
            // Caminho corrigido - remove 'src/' pois assets está na raiz do classpath
            InputStream is = AssetLoader.class.getResourceAsStream("/assets/" + fileName);
            if (is != null)
            {
                return ImageIO.read(is);
            }
            else
            {
                System.err.println("Imagem não encontrada no caminho: /assets/" + fileName);
                // Debug adicional - mostra o classpath
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
}