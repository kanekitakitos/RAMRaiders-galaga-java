package gui;

import core.objectsInterface.IGameObject;
import geometry.Ponto;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.List;

/**
 * Painel Swing customizado responsável por desenhar (renderizar) os GameObjects.
 */
public class GamePanel extends JPanel
{
    private volatile List<IGameObject> objectsToRender = Collections.emptyList();


    public GamePanel(int width, int height)
    {
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
    }

    public void updateGameObjects(List<IGameObject> newObjects)
    {
        this.objectsToRender = newObjects;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        for (IGameObject go : objectsToRender)
        {
            if ( go.transform() == null || go.shape() == null) continue;
            drawGameObject(g2d, go, panelWidth, panelHeight);
        }

        g2d.dispose();
    }

    private void drawGameObject(Graphics2D g2d, IGameObject go, int panelWidth, int panelHeight)
    {
        Ponto position = go.transform().position();
        double angle = go.transform().angle();
        double scale = go.transform().scale();


        AffineTransform oldTransform = g2d.getTransform();


        // Origem no centro, Y para cima
        g2d.translate(panelWidth / 2, panelHeight / 2);


        g2d.translate(position.x(), position.y());
        // Modificação: Negar o ângulo para inverter a direção da rotação (sentido horário)
        g2d.rotate(Math.toRadians(-angle));
        g2d.scale(scale, scale);


        core.Shape shape = go.shape();
        java.awt.image.BufferedImage img = shape.getImagem();
        if (img != null)
        {
            int imgW = img.getWidth();
            int imgH = img.getHeight();
            g2d.drawImage(img, -imgW/2, -imgH/2, null);
        }


        g2d.setTransform(oldTransform);
    }
}