package gui;

import core.objectsInterface.IGameObject;

import javax.swing.*;
import java.util.List;

public class SwingGui implements IGuiBridge
{
    private JFrame frame;
    private GamePanel panel;
    private InputEvent inputState;

    public SwingGui(int width, int height)
    {
        frame = new JFrame("Galaga");
        panel = new GamePanel(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void draw(List<IGameObject> objects)
    {
        SwingUtilities.invokeLater(() -> panel.updateGameObjects(objects));
    }

    @Override
    public InputEvent getInputState()
    {
        return this.inputState;
    }


}