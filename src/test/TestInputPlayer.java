package test;

import gui.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

public class TestInputPlayer {

    public static void main(String[] args)
    {
        // Create key and mouse mappings
        Map<Integer, String> keyMap = new HashMap<>();
        keyMap.put(KeyEvent.VK_RIGHT, "RIGHT");
        keyMap.put(KeyEvent.VK_LEFT, "LEFT");
        keyMap.put(KeyEvent.VK_SPACE, "EVASIVE");

        Map<Integer, String> mouseMap = new HashMap<>();
        mouseMap.put(MouseEvent.BUTTON1, "MOUSE_LEFT");

        // Create input event and frame
        InputEvent inputEvent = new InputEvent(keyMap, mouseMap);
        JFrame frame = new JFrame("Input Test");
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inputEvent.registerInputHandlers(frame);

        // Infinite loop to check inputs
        while (true) {
            if (inputEvent.isActionActive("RIGHT")) {
                System.out.println("RIGHT key is pressed");
            }
            if (inputEvent.isActionActive("LEFT")) {
                System.out.println("LEFT key is pressed");
            }
            if (inputEvent.isActionActive("EVASIVE")) {
                System.out.println("SPACE key is pressed");
            }
            if (inputEvent.isActionActive("MOUSE_LEFT")) {
                System.out.println("Left mouse button is pressed");
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}