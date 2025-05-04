package gui;

import core.objectsInterface.IInputEvent;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

/**
 * Represents a class that handles keyboard and mouse input events.
 * Implements KeyListener and MouseListener to capture user interactions.
 *
 * @Pre-Conditions:
 * - An initialized JFrame must be provided to register input handlers
 * - The JFrame must have focus to receive keyboard/mouse events
 *
 * @Post-Conditions:
 * - Keyboard and mouse events will update internal state flags
 * - Input state can be queried through is* methods
 * - Input handlers will remain active as long as the JFrame exists
 *
 * @see <a href="https://javapointers.com/java/java-se/mouse-listener/"> Mouse Listener in Java </a>
 * @see <a href="https://javapointers.com/java/java-se/key-listener/"> Key Listener in Java </a>
 *
 * @author Brandon Mejia
 * @author Gabriel Pedroso
 * @author Miguel Correia
 * @version 2025-04-18
 */
public class InputEvent implements IInputEvent, KeyListener, MouseListener
{
    // Movement keys state
    private boolean RIGHT = false;
    private boolean LEFT = false;

    // Mouse buttons state
    private boolean MOUSE_RIGHT = false;
    private boolean MOUSE_LEFT = false;

    /**
     * @return true if the "Right" (D) key is pressed, false otherwise.
     */
    public boolean isRight() { return RIGHT; }

    /**
     * @return true if the "Left" (A) key is pressed, false otherwise.
     */
    public boolean isLeft() { return LEFT; }


    /**
     * @return true if the right mouse button is pressed, false otherwise.
     */
    public boolean isAttack() { return MOUSE_LEFT; }

    /**
     * @return true if the left mouse button is pressed, false otherwise.
     */
    public boolean isEvasiveManeuver() { return MOUSE_RIGHT; }

    /**
     * Sets the state of a specific key or mouse button.
     *
     * @param key   The key or mouse button identifier (e.g., "RIGHT", "LEFT", "MOUSE_RIGHT").
     * @param value The state to set (true for pressed, false for released).
     */
    public void setPlayerMove(String key, boolean value)
    {
        switch (key)
        {
            case "RIGHT" -> RIGHT = value;
            case "LEFT" -> LEFT = value;
            case "MOUSE_RIGHT", "ATTACK" -> MOUSE_RIGHT = value;
            case "MOUSE_LEFT", "EVASIVE" -> MOUSE_LEFT = value;
        }
    }

    /**
     * Invoked when a key is typed. Not implemented.
     *
     * @param e The KeyEvent associated with the key typed.
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Invoked when a key is pressed. Updates the state of movement keys.
     *
     * @param e The KeyEvent associated with the key pressed.
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> RIGHT = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> LEFT = true;
            case KeyEvent.VK_C ->  MOUSE_LEFT = true;
            case KeyEvent.VK_X ->  MOUSE_RIGHT = true;
        }
    }

    /**
     * Invoked when a key is released. Updates the state of movement keys.
     *
     * @param e The KeyEvent associated with the key released.
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> RIGHT = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> LEFT = false;
            case KeyEvent.VK_C ->  MOUSE_LEFT = false;
            case KeyEvent.VK_X ->  MOUSE_RIGHT = false;
        }
    }

    /**
     * Invoked when a mouse button is clicked. Not implemented.
     *
     * @param e The MouseEvent associated with the mouse click.
     */
    @Override
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when a mouse button is pressed. Updates the state of mouse buttons.
     *
     * @param e The MouseEvent associated with the mouse press.
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            MOUSE_LEFT = true;
        }
        else if (e.getButton() == MouseEvent.BUTTON3)
        {
            MOUSE_RIGHT = true;
        }
    }

    /**
     * Invoked when a mouse button is released. Updates the state of mouse buttons.
     *
     * @param e The MouseEvent associated with the mouse release.
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            MOUSE_LEFT = false;
        }
        else if (e.getButton() == MouseEvent.BUTTON3)
        {
            MOUSE_RIGHT = false;
        }
    }

    /**
     * Invoked when the mouse enters a component. Not implemented.
     *
     * @param e The MouseEvent associated with the mouse entering.
     */
    @Override
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component. Not implemented.
     *
     * @param e The MouseEvent associated with the mouse exiting.
     */
    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * Registers this InputEvent instance as a listener for keyboard and mouse events on the given frame.
     *
     * @param frame The JFrame to which the input handlers will be added.
     */
    public void registerInputHandlers(JFrame frame)
    {
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.setFocusable(true);
    }
}