package gui;

import javax.swing.JFrame;

/**
 * Interface for handling input events in a GUI.
 * Provides methods to check the state of specific actions and register input
 * handlers.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * IInputEvent inputEvent = new SomeInputEventImplementation();
 * boolean isActive = inputEvent.isActionActive("JUMP");
 * inputEvent.registerInputHandlers(frame);
 * </pre>
 *
 * @preConditions:
 *                 - The action string passed to isActionActive must not be
 *                 null.
 *                 - The JFrame passed to registerInputHandlers must be a valid,
 *                 initialized JFrame.
 *
 * @postConditions:
 *                  - isActionActive returns the current state of the specified
 *                  action.
 *                  - registerInputHandlers associates input handlers with the
 *                  provided JFrame.
 *
 * @author Brandon Mejia
 * @version 2025-03-25
 */
public interface IInputEvent {
    /**
     * Checks if a specific action is currently active.
     *
     * @param action The name of the action to check.
     * @return True if the action is active, false otherwise.
     */
    boolean isActionActive(String action);

    /**
     * Registers input handlers for the specified JFrame.
     *
     * @param frame The JFrame to associate input handlers with.
     */
    void registerInputHandlers(JFrame frame);
}