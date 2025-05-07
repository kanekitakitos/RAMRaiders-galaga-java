package gui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class HandlerInputPlayer extends InputEvent
{
    private Map<Integer, String> keyMap;
    private Map<Integer, String> mouseMap;

    public HandlerInputPlayer()
    {
        super(createKeyMap(), createMouseMap());
        this.keyMap = createKeyMap();
        this.mouseMap = createMouseMap();
    }

    private static Map<Integer, String> createKeyMap()
    {
        Map<Integer, String> customKeyMap = new HashMap<>();
        customKeyMap.put(KeyEvent.VK_A, "LEFT");
        customKeyMap.put(KeyEvent.VK_LEFT, "LEFT");
        customKeyMap.put(KeyEvent.VK_RIGHT, "RIGHT");
        customKeyMap.put(KeyEvent.VK_D, "RIGHT");
        customKeyMap.put(KeyEvent.VK_C, "ATTACK");
        customKeyMap.put(KeyEvent.VK_X, "EVASIVE");
        return customKeyMap;
    }

    private static Map<Integer, String> createMouseMap()
    {
        Map<Integer, String> customMouseMap = new HashMap<>();
        customMouseMap.put(MouseEvent.BUTTON1, "ATTACK");
        customMouseMap.put(MouseEvent.BUTTON3, "EVASIVE");
        return customMouseMap;
    }

    public String getKeyAction(int keyCode) {
        return keyMap.getOrDefault(keyCode, "UNKNOWN");
    }

    public String getMouseAction(int buttonCode) {
        return mouseMap.getOrDefault(buttonCode, "UNKNOWN");
    }
}