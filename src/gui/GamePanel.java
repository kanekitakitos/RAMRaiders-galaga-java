package gui;

import core.objectsInterface.IGameObject;
import geometry.Ponto;
import core.Shape; 
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;


public class GamePanel extends JPanel
{
    private volatile List<IGameObject> objectsToRender = Collections.emptyList();
    private Shape backgroundShape;
    private boolean hitbox= false;

    public GamePanel(int width, int height)
    {
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        setBackground(Color.BLACK);

    }

    public GamePanel(int width, int height,Shape backgroundShape)
    {
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        setBackground(Color.BLACK);
        this.backgroundShape = backgroundShape;

    }


    public void updateGameObjects(List<IGameObject> newObjects)
    {
        this.objectsToRender = newObjects;
        repaint();
    }

    public void setHitbox(boolean hitbox)
    {
        this.hitbox = hitbox;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawBackground(g2d);


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        for (IGameObject go : objectsToRender)
        {
            if ( go.transform() == null || go.shape() == null) continue;
                drawGameObject(g2d, go, getWidth(), getHeight());
        }



        drawInfo(g2d);
    }


    private void drawBackground(Graphics2D g2d)
    {
        if (this.backgroundShape != null) {
            this.backgroundShape.updateAnimation();
            BufferedImage currentBgFrame = this.backgroundShape.getImagem();
            if (currentBgFrame != null)
            {
                g2d.drawImage(currentBgFrame, 0, 0, getWidth()-getWidth()/3,getHeight(), this);
                return;
            }
            else
            {
                System.err.println("Frame atual do background é null. Desenhando fundo branco.");
            }
        }

    }

    private void drawGameObject(Graphics2D g2d, IGameObject go, int panelWidth, int panelHeight)
    {
        Ponto position = go.transform().position();
        double angle = go.transform().angle();

        core.Shape shape = go.shape();
        java.awt.image.BufferedImage img = shape.getImagem();
        if (img == null) return;

        AffineTransform oldTransform = g2d.getTransform();

        // this is the center of the plane
        // for X is panelWidth / 2.0 -panelWidth/6.0
        // for Y is panelHeight / 2.0

        double screenX = panelWidth / 2.0 -panelWidth/6.0 + position.x() ;
        double screenY = panelHeight / 2.0 - position.y() ;

        g2d.translate(screenX, screenY);
        g2d.rotate(Math.toRadians(-angle));

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        geometry.Poligono poligono = null;
            poligono = (geometry.Poligono) go.collider();
            geometry.Ponto[] vertices = poligono.vertices();
            for (geometry.Ponto v : vertices)
            {
                if (v.x() < minX) minX = v.x();
                if (v.x() > maxX) maxX = v.x();
                if (v.y() < minY) minY = v.y();
                if (v.y() > maxY) maxY = v.y();
            }
            double logicalWidth = (maxX - minX) *1.35;// its a bit bigger than the collider
            double logicalHeight = (maxY - minY) *1.35; // its a bit bigger than the collider


        g2d.drawImage(img, (int)(-logicalWidth/2), (int)(-logicalHeight/2), (int)logicalWidth, (int)logicalHeight, null);

        g2d.setTransform(oldTransform);

        if(hitbox)
            go.collider().draw(g2d, panelWidth / 2.0 -panelWidth/6.0, panelHeight / 2.0);

    }

   public void drawInfo(Graphics g2d)
    {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(getWidth() - getWidth()/3, 0, getWidth()/3, getHeight());
    }

}