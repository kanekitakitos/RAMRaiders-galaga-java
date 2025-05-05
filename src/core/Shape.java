package core;

import core.objectsInterface.IShape;
import java.awt.image.BufferedImage;
import java.util.ArrayList; // Importar ArrayList
import java.util.List;      // Importar List

public class Shape implements IShape
{
    // Substituir a imagem única por uma lista de frames
    private List<BufferedImage> frames;
    private int currentFrameIndex;
    // Opcional: adicionar controle de tempo para a animação
    private long lastFrameTime;
    private int frameDelayMillis; // Tempo entre frames em milissegundos

    // Construtor pode receber a lista de frames e a velocidade
    public Shape(List<BufferedImage> frames, int frameDelayMillis)
    {
        if (frames == null || frames.isEmpty())
        {
            // Lidar com caso de frames inválidos (lançar exceção ou usar um placeholder)
            this.frames = new ArrayList<>(); // Ou definir como null/placeholder
        }
        else
        {
            this.frames = frames;
        }
        this.currentFrameIndex = 0;
        this.frameDelayMillis = frameDelayMillis;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public Shape(BufferedImage frames, int frameDelayMillis)
    {
    
        this.frames = new ArrayList<>();
        this.frames.add(frames);
       
        this.currentFrameIndex = 0;
        this.frameDelayMillis = frameDelayMillis;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public Shape()
    {
        this.frames = new ArrayList<>();
        this.currentFrameIndex = 0;
        this.frameDelayMillis = 0;
        this.lastFrameTime = System.currentTimeMillis();
    }



    // Método para obter o frame atual da animação
    private BufferedImage getCurrentFrame()
    {
        if (frames.isEmpty())
        {
            return null; // Ou retornar uma imagem padrão/placeholder
        }
        return frames.get(currentFrameIndex);
    }

    // Método para atualizar a lógica da animação (chamar a cada ciclo do jogo)
    public void updateAnimation()
    {

        if (frames.size() <= 1 || frameDelayMillis <= 0)
        {
            return; // Não animar se houver 1 frame ou menos, ou sem delay
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDelayMillis)
        {
            currentFrameIndex = (currentFrameIndex + 1) % frames.size(); // Avança e volta ao início
            lastFrameTime = currentTime;
        }
    }

    // Métodos get/set podem ser ajustados conforme necessário
    public List<BufferedImage> getFrames()
    {
        return frames;
    }

    public void setFrames(List<BufferedImage> frames)
    {
        this.frames = frames;
        this.currentFrameIndex = 0; // Resetar animação ao definir novos frames
        // Resetar também lastFrameTime se necessário
    }

    @Override
    public BufferedImage getImagem()
    {
        return getCurrentFrame();
    }

}