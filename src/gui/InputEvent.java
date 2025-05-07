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
  * <p>Example usage:</p>
  * <pre>
  *     Map<Integer, String> keyMap = new HashMap<>();
  *     keyMap.put(KeyEvent.VK_W, "MOVE_UP");
  *     Map<Integer, String> mouseMap = new HashMap<>();
  *     mouseMap.put(MouseEvent.BUTTON1, "SHOOT");
  *
  *     InputEvent inputEvent = new InputEvent(keyMap, mouseMap);
  *     inputEvent.registerInputHandlers(frame);
  *
  *     if (inputEvent.isActionActive("MOVE_UP")) {
  *         System.out.println("Moving up!");
  *     }
  * </pre>
  *
  * @Pre-Conditions:
  * - An initialized JFrame must be provided to register input handlers.
  * - The JFrame must have focus to receive keyboard/mouse events.
  *
  * @Post-Conditions:
  * - Keyboard and mouse events will update internal state flags.
  * - Input state can be queried through is* methods.
  * - Input handlers will remain active as long as the JFrame exists.
  *
  * @see <a href="https://javapointers.com/java/java-se/mouse-listener/">Mouse Listener in Java</a>
  * @see <a href="https://javapointers.com/java/java-se/key-listener/">Key Listener in Java</a>
  *
  * @author Brandon Mejia
  * @author Gabriel Pedroso
  * @version 2025-04-18
  */
 public class InputEvent implements IInputEvent, KeyListener, MouseListener {

     private InputMapping inputMapping = new InputMapping();
     private Map<Integer, String> keyToActionMap = new HashMap<>();
     private Map<Integer, String> mouseButtonToActionMap = new HashMap<>();

     /**
      * Constructs an InputEvent object with custom key and mouse mappings.
      *
      * @param customKeyMap A map of key codes to action strings.
      * @param customMouseMap A map of mouse button codes to action strings.
      */
     public InputEvent(Map<Integer, String> customKeyMap, Map<Integer, String> customMouseMap) {
         this.keyToActionMap = customKeyMap;
         this.mouseButtonToActionMap = customMouseMap;
     }

     /**
      * Checks if a specific action is currently active.
      *
      * @param action The name of the action to check.
      * @return True if the action is active, false otherwise.
      */
     @Override
     public boolean isActionActive(String action) {
         return inputMapping.isActionActive(action);
     }

     /**
      * Handles key press events and updates the action state.
      *
      * @param e The KeyEvent triggered by the key press.
      */
     @Override
     public void keyPressed(KeyEvent e) {
         String action = keyToActionMap.get(e.getKeyCode());
         if (action != null) {
             inputMapping.setActionState(action, true);
         }
     }

     /**
      * Handles key release events and updates the action state.
      *
      * @param e The KeyEvent triggered by the key release.
      */
     @Override
     public void keyReleased(KeyEvent e) {
         String action = keyToActionMap.get(e.getKeyCode());
         if (action != null) {
             inputMapping.setActionState(action, false);
         }
     }

     /**
      * Handles mouse button press events and updates the action state.
      *
      * @param e The MouseEvent triggered by the mouse button press.
      */
     @Override
     public void mousePressed(MouseEvent e) {
         int button = e.getButton();
         String action = mouseButtonToActionMap.get(button);
         if (action != null) {
             inputMapping.setActionState(action, true);
         }
     }

     /**
      * Handles mouse button release events and updates the action state.
      *
      * @param e The MouseEvent triggered by the mouse button release.
      */
     @Override
     public void mouseReleased(MouseEvent e) {
         int button = e.getButton();
         String action = mouseButtonToActionMap.get(button);
         if (action != null) {
             inputMapping.setActionState(action, false);
         }
     }

     /**
      * Registers input handlers for the specified JFrame.
      *
      * @param frame The JFrame to associate input handlers with.
      */
     @Override
     public void registerInputHandlers(JFrame frame) {
         frame.addKeyListener(this);
         frame.addMouseListener(this);
         frame.setFocusable(true);
     }

     /**
      * Handles key typed events. Not used in this implementation.
      *
      * @param e The KeyEvent triggered by the key typed.
      */
     public void keyTyped(KeyEvent e) {
         // Not used in this implementation
     }

     /**
      * Handles mouse click events. Not used in this implementation.
      *
      * @param e The MouseEvent triggered by the mouse click.
      */
     public void mouseClicked(MouseEvent e) {
         // Not used in this implementation
     }

     /**
      * Handles mouse enter events. Not used in this implementation.
      *
      * @param e The MouseEvent triggered by the mouse entering a component.
      */
     public void mouseEntered(MouseEvent e) {
         // Not used in this implementation
     }

     /**
      * Handles mouse exit events. Not used in this implementation.
      *
      * @param e The MouseEvent triggered by the mouse exiting a component.
      */
     public void mouseExited(MouseEvent e) {
         // Not used in this implementation
     }

     /**
      * Represents a helper class for managing action states.
      */
     private class InputMapping {

         private Map<String, Boolean> actionStates = new HashMap<>();

         /**
          * Sets the state of a specific action.
          *
          * @param action The name of the action.
          * @param state The state to set (true for active, false for inactive).
          */
         public void setActionState(String action, boolean state) {
             actionStates.put(action, state);
         }

         /**
          * Checks if a specific action is currently active.
          *
          * @param action The name of the action to check.
          * @return True if the action is active, false otherwise.
          */
         public boolean isActionActive(String action) {
             return actionStates.getOrDefault(action, false);
         }
     }
 }