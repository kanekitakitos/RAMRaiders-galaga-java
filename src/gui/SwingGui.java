package gui;

import core.objectsInterface.IGameObject;
import core.Shape;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A Swing-based GUI implementation for rendering game objects and handling
 * input events.
 * Manages a game window with a custom panel for drawing and input handling.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * SwingGui gui = new SwingGui(800, 600);
 * gui.draw(gameObjects);
 * IInputEvent input = gui.getInputState();
 * </pre>
 *
 * @preConditions:
 *                 - Width and height parameters must be positive integers.
 *                 - The list of game objects passed to the draw method must not
 *                 be null.
 *                 - Input handlers must be registered with a valid JFrame.
 *
 * @postConditions:
 *                  - A game window is created and displayed with the specified
 *                  dimensions.
 *                  - Game objects are rendered on the panel.
 *                  - Input events are captured and can be queried through the
 *                  input state.
 *
 * @see InputEvent
 *
 * @author Brandon Mejia
 * @version 2025-03-25
 */
public class SwingGui implements IGuiBridge
{
    private JFrame frame; // The main game window
    private GamePanel panel; // Custom panel for rendering game objects
    private IInputEvent inputState; // Input event handler


    /**
    * Validates the invariants for the SwingGui class.
    * Ensures that the width and height parameters are positive integers.
    * If the validation fails, an error message is printed, and the program exits.
    *
    * @param width  The width of the game window in pixels. Must be greater than 0.
    * @param height The height of the game window in pixels. Must be greater than 0.
    *
    */
    private void invariante(int width, int height)
    {
        if(width > 0 && height > 0)
            return;

        System.out.println("SwingGui:iv");
        System.exit(0);
    }


    /**
     * Constructs a SwingGui with the specified dimensions.
     *
     * @param width  The width of the game window in pixels.
     * @param height The height of the game window in pixels.
     */
    public SwingGui(int width, int height)
    {
        invariante(width, height);

        this.frame = new JFrame("Galaga - RAMRaiders");
        panel = new GamePanel(width, height);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.add(panel);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setResizable(false);
        this.inputState.registerInputHandlers(this.frame);
    }

    /**
     * Constructs a SwingGui with the specified dimensions and a background shape.
     *
     * @param width           The width of the game window in pixels.
     * @param height          The height of the game window in pixels.
     * @param backgroundShape The shape to use as the background.
     */
    public SwingGui(int width, int height, Shape backgroundShape)
    {
        invariante(width, height);

        this.frame = new JFrame("Galaga - RAMRaiders");
        panel = new GamePanel(width, height, backgroundShape);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.add(panel);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setResizable(false);

        this.inputState = generateInputEvent();

        this.inputState.registerInputHandlers(this.frame);
        this.frame.setFocusable(true);
    }

    private IInputEvent generateInputEvent()
    {
        Map<Integer, String> customKeyMap = new HashMap<>();
        customKeyMap.put(KeyEvent.VK_A, "LEFT");
        customKeyMap.put(KeyEvent.VK_LEFT, "LEFT");
        customKeyMap.put(KeyEvent.VK_RIGHT, "RIGHT");
        customKeyMap.put(KeyEvent.VK_D, "RIGHT");
        customKeyMap.put(KeyEvent.VK_C, "ATTACK");
        customKeyMap.put(KeyEvent.VK_X, "EVASIVE");
        customKeyMap.put(KeyEvent.VK_1, "PLAYER1");
        customKeyMap.put(KeyEvent.VK_2, "PLAYER2");
        customKeyMap.put(KeyEvent.VK_NUMPAD1, "PLAYER1");
        customKeyMap.put(KeyEvent.VK_NUMPAD2, "PLAYER2");

        Map<Integer, String> customMouseMap = new HashMap<>(); // Map for custom mouse bindings
        customMouseMap.put(MouseEvent.BUTTON1, "ATTACK");
        customMouseMap.put(MouseEvent.BUTTON3, "EVASIVE");

        return new InputEvent(customKeyMap, customMouseMap); // Create and return the input handler
    }

    /**
     * Renders the provided list of game objects on the panel.
     *
     * @param objectsToRender A thread-safe list of game objects to render.
     */
    @Override
    public void draw(CopyOnWriteArrayList<IGameObject> objectsToRender)
    {
        SwingUtilities.invokeLater(() -> panel.updateGameObjects(objectsToRender));
    }

    /**
     * Retrieves the current input state from the GUI.
     *
     * @return The input event handler representing the current input state.
     */
    @Override
    public IInputEvent getInputEvent()
    {
        return this.inputState;
    }

    /**
     * Toggles the display of hitboxes for game objects.
     *
     * @param hitbox True to show hitboxes, false to hide them.
     */
    public void setHitbox(boolean hitbox)
    {
        this.panel.setHitbox(hitbox);
    }

    /**
     * Toggles the display of Menu for game.
     *
     * @param Menu True to show Menu, false to hide them.
     */
    public void setMenu(boolean Menu)
    {
        this.panel.setMenu(Menu);
    }

    /**
     * Checks if the menu is currently displayed.
     *
     * @return True if the menu is displayed, false otherwise.
     */
    public boolean isMenu()
    {
        return this.panel.isMenu();
    }

}