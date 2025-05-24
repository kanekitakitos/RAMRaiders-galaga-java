package test;

import core.*;
import core.Shape;
import core.EnemyGroupAttack.EnterGameGroup;
import core.objectsInterface.IGameObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import geometry.Ponto;
import geometry.Retangulo;

import java.util.ArrayList;

public class GameVisualizer {
    private final int width;
    private final int height;
    private static final int CELL_SIZE = 60;
    private static final int PADDING = 40;

    public GameVisualizer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void showGameObjects(List<IGameObject> objects) {
        JFrame frame = new JFrame("Game Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((width + 2) * CELL_SIZE, (height + 2) * CELL_SIZE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fill background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw grid (light gray)
                g2d.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i <= width; i++) {
                    int x = PADDING + i * CELL_SIZE;
                    g2d.drawLine(x, PADDING, x, PADDING + height * CELL_SIZE);
                }
                for (int i = 0; i <= height; i++) {
                    int y = PADDING + i * CELL_SIZE;
                    g2d.drawLine(PADDING, y, PADDING + width * CELL_SIZE, y);
                }

                // Draw axes (thicker black lines)
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                // Y-axis (x=0)
                int xAxis = PADDING;
                g2d.drawLine(xAxis, PADDING, xAxis, PADDING + height * CELL_SIZE);
                // X-axis (y=0)
                int yAxis = PADDING + height * CELL_SIZE;
                g2d.drawLine(PADDING, yAxis, PADDING + width * CELL_SIZE, yAxis);

                // Draw origin (0,0) as a red circle
                int originX = PADDING;
                int originY = PADDING + height * CELL_SIZE;
                g2d.setColor(Color.RED);
                g2d.fillOval(originX - 5, originY - 5, 10, 10);

                // Draw coordinate numbers
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                for (int i = 0; i < width; i++) {
                    g2d.drawString(String.valueOf(i), PADDING + i * CELL_SIZE + CELL_SIZE/2, PADDING - 10);
                }
                for (int i = 0; i < height; i++) {
                    g2d.drawString(String.valueOf(i), PADDING - 20, PADDING + (height - i) * CELL_SIZE + CELL_SIZE/2);
                }
                // Label axes
                g2d.drawString("X", PADDING + width * CELL_SIZE + 10, yAxis + 15);
                g2d.drawString("Y", xAxis - 20, PADDING - 10);

                // Draw objects (no change)
                for (IGameObject obj : objects) {
                    double x = obj.transform().position().x();
                    double y = obj.transform().position().y();

                    // Choose color based on object name
                    Color color = Color.BLUE;
                    String name = obj.name().toLowerCase();
                    if (name.contains("player")) color = Color.GREEN;
                    else if (name.contains("target")) color = Color.RED;
                    else if (name.contains("enemy")) color = Color.BLUE;

                    g2d.setColor(color);

                    // Draw shape (convert y to Cartesian: y = height - y)
                    int drawX = (int) (PADDING + x * CELL_SIZE);
                    int drawY = (int) (PADDING + (height - y) * CELL_SIZE);

                    if (obj.collider() instanceof Retangulo) {
                        int size = (int) (CELL_SIZE * 0.6);
                        g2d.fillRect(drawX - size/2, drawY - size/2, size, size);
                    } else {
                        int size = (int) (CELL_SIZE * 0.6);
                        g2d.fillOval(drawX - size/2, drawY - size/2, size, size);
                    }

                    // Draw object name
                    g2d.setColor(Color.BLACK);
                    FontMetrics fm = g2d.getFontMetrics();
                    String label = obj.name();
                    int textX = drawX - fm.stringWidth(label)/2;
                    int textY = drawY + fm.getHeight()/2;
                    g2d.drawString(label, textX, textY);
                }
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }

    public static List<IGameObject> createSampleObjects() {
        List<IGameObject> objects = new ArrayList<>();

        // Create player
        Transform playerTransform = new Transform(new Ponto(5,0), 0, 90, 1.0);
        Ponto[] playerPoints = {new Ponto(-0.5, 0.5), new Ponto(0.5, 0.5), 
                              new Ponto(0.5, -0.5), new Ponto(-0.5, -0.5)};
        Retangulo playerCollider = new Retangulo(playerPoints, playerTransform);
        GameObject player = new GameObject("Player", playerTransform, playerCollider, 
                                        new PlayerBehavior(), new Shape());
        player.onInit();
        objects.add(player);

        // Create enemies
        List<IGameObject> enemies = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            Transform t = new Transform(new Ponto(0, 0), 0, 270, 1.0);
            Ponto[] points = {new Ponto(-0.3, 0.3), new Ponto(0.3, 0.3),
                            new Ponto(0.3, -0.3), new Ponto(-0.3, -0.3)};
            Retangulo collider = new Retangulo(points, t);
            GameObject enemy = new GameObject("Enemy " + i, t, collider,
                                           new EnemyBehavior(), new Shape());
            enemy.onInit();
            enemies.add(enemy);
        }

        // Position enemies using SimpleGroupAttack
        EnterGameGroup groupAttack = new EnterGameGroup();
        groupAttack.onInit(enemies, player);
        groupAttack.execute(enemies, player);

        objects.addAll(enemies);

        for (int i = 0; i < objects.size(); i++) {
            System.out.println(objects.get(i));
            System.out.println();
        }

        return objects;
    }

}
