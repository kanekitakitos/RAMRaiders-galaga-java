package gui;

import core.objectsInterface.IGameObject;
import geometry.Ponto;
import core.objectsInterface.IShape;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.FontMetrics;

/**
 * A custom JPanel for rendering game objects and background in a 2D game.
 * Handles drawing of game objects with transformations, animations and
 * hitboxes.
 *
 * <p>
 * This panel maintains a list of game objects to render and optionally displays
 * a background shape. It performs coordinate transformations to center objects
 * and applies rotations based on object transforms.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * GamePanel panel = new GamePanel(800, 600);
 * panel.updateGameObjects(gameObjects);
 * panel.setHitbox(true);
 * frame.add(panel);
 * </pre>
 *
 * @preConditions:
 *                 - Width and height parameters must be positive integers
 *                 - GameObjects in objectsToRender must have valid transform
 *                 and shape components
 *                 - Background shape must have valid image frames if provided
 *
 * @postConditions:
 *                  - Panel is initialized with specified dimensions
 *                  - Game objects are rendered with proper transformations
 *                  - Background is drawn if provided
 *                  - Info panel is drawn on the right side
 *                  - Hitboxes are displayed if enabled
 * @see 
 *
 * @author Brandon Mejia
 * @version 2025-03-25
 */
public class GamePanel extends JPanel
{
    private volatile List<IGameObject> objectsToRender = Collections.emptyList();
    private volatile List<IGameObject> infoToRender = Collections.emptyList();

    private IShape backgroundShape;
    private boolean hitbox = false;
    private boolean menu = true;



    /**
     * Validates the invariants for the GamePanel class.
     * Ensures that the width and height parameters are positive integers.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param width  The width of the panel in pixels. Must be greater than 0.
     * @param height The height of the panel in pixels. Must be greater than 0.
     */
    private void invariantes(int width, int height)
    {
        if(width > 0 && height > 0)
            return;

        System.out.println("GamePanel:iv");
        System.exit(0);
    }


    /**
     * Creates a game panel with specified dimensions and black background.
     *
     * @param width  The width of the panel in pixels
     * @param height The height of the panel in pixels
     */
    public GamePanel(int width, int height)
    {
        invariantes(width, height);

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
    }

    /**
     * Creates a game panel with specified dimensions and background shape.
     *
     * @param width           The width of the panel in pixels
     * @param height          The height of the panel in pixels
     * @param backgroundShape The shape to use as background
     */
    public GamePanel(int width, int height, IShape backgroundShape)
    {
        invariantes(width, height);

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        this.backgroundShape = backgroundShape;
    }

    /**
     * Updates the list of game objects to be rendered.
     * Triggers a repaint of the panel.
     *
     * @param newObjects The new list of game objects to render
     */
    public void updateGameObjects(List<IGameObject> newObjects)
    {

        List<IGameObject> layerZero = new ArrayList<>();
        List<IGameObject> otherLayers = new ArrayList<>();

        for (IGameObject go : newObjects)
        {
            if (go.transform().layer() == 0)
                layerZero.add(go);
            else
                otherLayers.add(go);

        }

        this.infoToRender = layerZero;
        this.objectsToRender = otherLayers;
        repaint();
    }


    public void setShape(IShape shape)
    {
        this.backgroundShape = shape;
    }


    /**
     * Sets whether hitboxes should be displayed for game objects.
     *
     * @param hitbox True to show hitboxes, false to hide them
     */
    public void setHitbox(boolean hitbox)
    {
        this.hitbox = hitbox;
    }

    /**
     * Sets whether Menu should be displayed for game.
     *
     * @param menu True to show hitboxes, false to hide them
     */
    public void setMenu(boolean menu)
    {
        this.menu = menu;
        if(!this.menu)
        {
            this.infoToRender.clear();
            this.objectsToRender.clear();
        }
    }

    public boolean isMenu()
    {
        return this.menu;
    }


    private void drawBackground(Graphics2D g2d)
    {
        if (this.backgroundShape == null)
        {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth() - getWidth() / 3, getHeight());
            return;
        }
            this.backgroundShape.updateAnimation();
            BufferedImage currentBgFrame = this.backgroundShape.getImagem();
            if (currentBgFrame != null)
            {
                g2d.drawImage(currentBgFrame, 0, 0, getWidth() - getWidth() / 3, getHeight(), this);
                return;
            }
            else
            {
                System.err.println("Frame atual do background é null. Desenhando fundo branco.");
            }

    }

    /**
     * Draws a single game object with proper transformations.
     * Handles positioning, rotation and scaling of the object's shape.
     *
     * @param g2d         The graphics context to draw on
     * @param go          The game object to draw
     * @param panelWidth  Width of the panel
     * @param panelHeight Height of the panel
     */
    private void drawGameObject(Graphics2D g2d, IGameObject go, int panelWidth, int panelHeight)
    {
        Ponto position = go.transform().position();
        double angle = go.transform().angle();

        core.Shape shape = go.shape();
        java.awt.image.BufferedImage img = shape.getImagem();
        if (img == null)
            return;

        AffineTransform oldTransform = g2d.getTransform();

        double screenX = panelWidth / 2.0 - panelWidth / 6.0 + position.x();
        double screenY = panelHeight / 2.0 - position.y();

        g2d.translate(screenX, screenY);
        g2d.rotate(Math.toRadians(-angle));

        double logicalWidth = shape.getLogicalWidth() * 2;
        double logicalHeight = shape.getLogicalHeight() * 2;

        g2d.drawImage(img, (int) (-logicalWidth / 2), (int) (-logicalHeight / 2),
                (int) logicalWidth, (int) logicalHeight, null);

        g2d.setTransform(oldTransform);

        if (hitbox)
            go.collider().draw(g2d, panelWidth / 2.0 - panelWidth / 6.0, panelHeight / 2.0);
    }


    private void drawString(Graphics2D g2d, IGameObject go, int panelWidth, int panelHeight)
    {
        String text = go.name();
        Ponto position = go.transform().position();
        int scale = (int)go.transform().scale();
    
        g2d.setFont(new java.awt.Font("Retro Gaming", java.awt.Font.BOLD, scale));
    
        // Coordenadas base
        double screenX = panelWidth / 2.0 + position.x();
        double screenY = panelHeight / 2.0 - position.y();
    
        // Centralizar a string
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int centeredX = (int)screenX - textWidth / 2;
        int centeredY = (int)screenY;

        // Desenha o texto principal (amarelo)
        g2d.setColor(Color.RED);
        g2d.drawString(text, centeredX-1, centeredY-2);
        g2d.setColor(Color.YELLOW);
        g2d.drawString(text, centeredX, centeredY);

        if (hitbox)
            go.collider().draw(g2d, panelWidth / 2.0, panelHeight / 2.0);
    }


    private void drawMenu(Graphics2D g2d)
    {
        if (this.backgroundShape == null)
        {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
            this.backgroundShape.updateAnimation();
            BufferedImage currentBgFrame = this.backgroundShape.getImagem();
            if (currentBgFrame != null)
            {
                g2d.drawImage(currentBgFrame, 0, 0, getWidth() , getHeight(), this);
            }
            else
            {
                System.err.println("Frame atual do background é null. Desenhando fundo branco.");
            }

            for (IGameObject go : infoToRender)
                {
                    if (go.transform() == null || go.shape() == null)
                        continue;
                        drawString(g2d, go, getWidth(), getHeight());

                }

                for (IGameObject go : objectsToRender)
                {
                    if (go.transform() == null || go.shape() == null)
                        continue;
                        drawGameObject(g2d, go, (int)(getWidth() + getWidth() / 2.0), getHeight());

                }

    }


    private void drawGame(Graphics2D g2d)
    {

        if(this.menu)
        {
            drawMenu(g2d);
        }
        else
        {
                for (IGameObject go : objectsToRender)
                {
                    if (go.transform() == null || go.shape() == null)
                        continue;

                    drawGameObject(g2d, go, getWidth(), getHeight());
                }

                g2d.setColor(Color.BLACK);
                g2d.fillRect(getWidth() - getWidth() / 3, 0, getWidth() / 3, getHeight());

                for (IGameObject go : infoToRender)
                {
                    if (go.transform() == null || go.shape() == null)
                        continue;

                    drawGameObject(g2d, go, getWidth(), getHeight());
                }


        }

    }



//--------------------------------------------------------------------------------------
    /**
     * Overrides paintComponent to render all game objects and background.
     * Applies transformations and draws objects centered on screen.
     *
     * @param g The graphics context to paint on
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);
        drawGame(g2d);
    }
}