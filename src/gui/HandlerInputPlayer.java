package gui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles input events for a player, mapping keyboard and mouse inputs to
 * specific actions.
 * Provides methods to retrieve actions based on key or mouse button codes.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * HandlerInputPlayer inputHandler = new HandlerInputPlayer();
 * String action = inputHandler.getKeyAction(KeyEvent.VK_A); // Returns "LEFT"
 * String mouseAction = inputHandler.getMouseAction(MouseEvent.BUTTON1); // Returns "ATTACK"
 * </pre>
 *
 * @preConditions:
 *                 - Key and mouse maps must be properly initialized with valid
 *                 mappings.
 *
 * @postConditions:
 *                  - Input events are mapped to corresponding actions.
 *                  - Default action "UNKNOWN" is returned for unmapped inputs.
 *
 * @author Brandon Mejia
 * @version 2025-03-25
 */
public class HandlerInputPlayer extends InputEvent {
    private Map<Integer, String> keyMap; // Maps key codes to actions
    private Map<Integer, String> mouseMap; // Maps mouse button codes to actions

    /**
     * Constructs a HandlerInputPlayer object and initializes key and mouse
     * mappings.
     */
    public HandlerInputPlayer() {
        super(createKeyMap(), createMouseMap());
        this.keyMap = createKeyMap();
        this.mouseMap = createMouseMap();
    }

    /**
     * Creates a default key mapping for player actions.
     *
     * @return A map of key codes to action strings.
     */
    private static Map<Integer, String> createKeyMap() {
        Map<Integer, String> customKeyMap = new HashMap<>();
        customKeyMap.put(KeyEvent.VK_A, "LEFT");
        customKeyMap.put(KeyEvent.VK_LEFT, "LEFT");
        customKeyMap.put(KeyEvent.VK_RIGHT, "RIGHT");
        customKeyMap.put(KeyEvent.VK_D, "RIGHT");
        customKeyMap.put(KeyEvent.VK_C, "ATTACK");
        customKeyMap.put(KeyEvent.VK_X, "EVASIVE");
        return customKeyMap;
    }

    /**
     * Creates a default mouse button mapping for player actions.
     *
     * @return A map of mouse button codes to action strings.
     */
    private static Map<Integer, String> createMouseMap() {
        Map<Integer, String> customMouseMap = new HashMap<>();
        customMouseMap.put(MouseEvent.BUTTON1, "ATTACK");
        customMouseMap.put(MouseEvent.BUTTON3, "EVASIVE");
        return customMouseMap;
    }

    /**
     * Retrieves the action associated with a given key code.
     *
     * @param keyCode The key code to look up.
     * @return The action string associated with the key code, or "UNKNOWN" if not
     *         mapped.
     */
    public String getKeyAction(int keyCode) {
        return keyMap.getOrDefault(keyCode, "UNKNOWN");
    }

    /**
     * Retrieves the action associated with a given mouse button code.
     *
     * @param buttonCode The mouse button code to look up.
     * @return The action string associated with the mouse button code, or "UNKNOWN"
     *         if not mapped.
     */
    public String getMouseAction(int buttonCode) {
        return mouseMap.getOrDefault(buttonCode, "UNKNOWN");
    }
}