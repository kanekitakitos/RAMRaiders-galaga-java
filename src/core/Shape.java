package core;

import core.objectsInterface.IShape;
import java.awt.image.BufferedImage;

public class Shape implements IShape
{
    private BufferedImage imagem;
    private int idImagem;

    public Shape()
    {
        this.imagem = null;
        this.idImagem = 0;
    }

    public Shape(BufferedImage imagem)
    {
        this.imagem = imagem;
        this.idImagem = 0;
    }

    public BufferedImage getImagem()
    {
        return imagem;
    }

    public void setImagem(BufferedImage imagem)
    {
        this.imagem = imagem;
    }

    public int getIdImagem()
    {
        return idImagem;
    }

    public void setIdImagem(int idImagem)
    {
        this.idImagem = idImagem;
    }
}