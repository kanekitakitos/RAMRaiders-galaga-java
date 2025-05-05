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
        this.frame = new JFrame("Galaga");
        panel = new GamePanel(width, height);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.add(panel);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);

        this.inputState = new InputEvent();
        this.inputState.registerInputHandlers(this.frame);
        this.frame.setFocusable(true);
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