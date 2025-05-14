package gui;

import core.objectsInterface.IGameObject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Interface for bridging GUI rendering and input handling in a game.
 * Provides methods for drawing game objects and retrieving input states.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * IGuiBridge guiBridge = new SomeGuiBridgeImplementation();
 * guiBridge.draw(gameObjects);
 * IInputEvent inputState = guiBridge.getInputState();
 * </pre>
 *
 * @preConditions:
 *                 - The list of game objects passed to the draw method must not
 *                 be null.
 *                 - The implementation of getInputState must return a valid
 *                 IInputEvent object.
 *
 * @postConditions:
 *                  - The draw method renders the provided game objects on the
 *                  GUI.
 *                  - The getInputState method retrieves the current input
 *                  state.
 *
 * @author Brandon Mejia
 * @version 2025-03-25
 */
public interface IGuiBridge {
    /**
     * Draws the provided list of game objects on the GUI.
     *
     * @param objectsToRender A thread-safe list of game objects to render.
     */
    void draw(CopyOnWriteArrayList<IGameObject> objectsToRender);

    /**
     * Retrieves the current input state from the GUI.
     *
     * @return An object representing the current input state.
     */
    IInputEvent getInput();


     /**
     * Sets a custom input event handler for the GUI.
     *
     * @param inputState The input event handler to set.
     */
    void setInput(IInputEvent inputState);

    boolean isMenu();

    /**
     * Toggles the display of Menu for game.
     *
     * @param Menu True to show Menu, false to hide them.
     */
    void setMenu(boolean Menu);
}