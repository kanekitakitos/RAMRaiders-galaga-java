package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import java.util.HashMap;
import java.util.Map;


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

    private InputMapping inputMapping = new InputMapping();
    private Map<Integer, String> keyToActionMap = new HashMap<>();
    private Map<Integer, String> mouseButtonToActionMap = new HashMap<>();

    public InputEvent(Map<Integer, String> customKeyMap, Map<Integer, String> customMouseMap)
    {
        this.keyToActionMap = customKeyMap;
        this.mouseButtonToActionMap = customMouseMap;
    }

    @Override
    public boolean isActionActive(String action)
    {
        return inputMapping.isActionActive(action);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        String action = keyToActionMap.get(e.getKeyCode());
        if (action != null)
        {
            inputMapping.setActionState(action, true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        String action = keyToActionMap.get(e.getKeyCode());
        if (action != null)
            inputMapping.setActionState(action, false);

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int button = e.getButton();
        String action = mouseButtonToActionMap.get(button);
        if (action != null)
            inputMapping.setActionState(action, true);

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        int button = e.getButton();
        String action = mouseButtonToActionMap.get(button);
        if (action != null)
            inputMapping.setActionState(action, false);

    }

    @Override
    public void registerInputHandlers(JFrame frame)
    {
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.setFocusable(true);
    }

    public void keyTyped(KeyEvent e)
    {
        // Not used in this implementation
    }

    public void mouseClicked(MouseEvent e) {
        

    }

    public void mouseEntered(MouseEvent e) {
        
    }

    public void mouseExited(MouseEvent e) {
        
    }


    private  class InputMapping
    {
        private Map<String, Boolean> actionStates = new HashMap<>();

        public void setActionState(String action, boolean state)
        {
            actionStates.put(action, state);
        }

        public boolean isActionActive(String action)
        {
            return actionStates.getOrDefault(action, false);
        }
    }

}