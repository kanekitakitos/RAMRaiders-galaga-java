package core;

import core.objectsInterface.IGameEngine;
import core.objectsInterface.IGameObject;
import gui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * The `GameEngine` class manages game objects, their layers, and updates.
 * It allows adding, removing, and retrieving game objects, as well as checking
 * for collisions.
 * This class implements the `IGameEngine` interface.
 *
 * <p>
 * Responsibilities:
 * </p>
 * - Manage game objects organized by layers.
 * - Handle updates, collisions, and enabling/disabling of game objects.
 * - Provide utility methods for adding, removing, and retrieving game objects.
 *
 * @see GameObject
 * @see InputEvent
 * @see IGameEngine
 * @see IGameObject
 * @see IInputEvent
 * @see IGuiBridge
 *
 * @author Brandon Mejia
 *
 * @version 2025-04-16
 */
public class GameEngine implements IGameEngine
{
    // Stores game objects organized by layers
    private HashMap<Integer, CopyOnWriteArrayList<IGameObject>> layeredGameObjects;
    private ArrayList<IGameObject> disabledGameObjects;

    // Tracks the total number of game objects
    private int totalObjects;

    // Input event handler
    private IInputEvent inputStatus;
    private IGuiBridge gui;

    /**
     * Validates the invariant for the `GameEngine` class.
     * Ensures that the provided `IGuiBridge` instance is not null.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param gui The `IGuiBridge` instance used for input and rendering. Must not be null.
     */
    private void invariante(IGuiBridge gui)
    {
        if(gui != null)
            return;

        System.out.println("GameEngine:iv");
        System.exit(0);
    }


    /**
     * Constructs a new `GameEngine` instance.
     * Initializes the layered game objects map and sets the total object count to
     * zero.
     *
     * @param gui The GUI bridge used for input and rendering.
     */
    public GameEngine(IGuiBridge gui)
    {
        invariante(gui);

        this.layeredGameObjects = new HashMap<>();
        this.disabledGameObjects = new ArrayList<>();
        this.totalObjects = 0;
        this.gui = gui;
        this.inputStatus = this.gui.getInput();
    }

    /**
     * Adds a `GameObject` to the engine.
     * If the layer does not exist, it creates a new layer.
     *
     * @param go The `GameObject` to add.
     */
    public void add(IGameObject go) {
        int layer = go.transform().layer();
        if (!layeredGameObjects.containsKey(layer))
            layeredGameObjects.put(layer, new CopyOnWriteArrayList<>());

        layeredGameObjects.get(layer).add(go);
        this.totalObjects++;
    }

    /**
     * Removes a `GameObject` from the engine.
     * If the layer becomes empty after removal, it deletes the layer.
     *
     * @param go The `GameObject` to remove.
     */
    @Override
    public void destroy(IGameObject go) {
        int layer = go.transform().layer();
        if (layeredGameObjects.containsKey(layer)) {
            layeredGameObjects.get(layer).remove(go);
            this.totalObjects--;
            if (layeredGameObjects.get(layer).isEmpty()) // Remove the list if it is empty
                layeredGameObjects.remove(layer);
        }
    }

    /**
     * Retrieves a `GameObject` from a specific layer and index.
     *
     * @param layer           The layer of the `GameObject`.
     * @param indexGameObject The index of the `GameObject` within the layer.
     * @return The `GameObject` if found, otherwise null.
     */
    public IGameObject get(int layer, int indexGameObject) {
        if (!layeredGameObjects.containsKey(layer) || indexGameObject < 0 || indexGameObject > this.totalObjects)
            return null;

        CopyOnWriteArrayList<IGameObject> layerObjects = layeredGameObjects.get(layer);
        return layerObjects.get(indexGameObject);
    }

    /**
     * Returns the total number of `GameObject`s in the engine.
     *
     * @return The total number of `GameObject`s.
     */
    public int size() {
        return this.totalObjects;
    }

    /**
     * Updates all `GameObject`s in the engine, moving them between layers if
     * necessary.
     * This method iterates through all layers and their respective `GameObject`s,
     * using the update method of each `GameObject`. If a `GameObject` changes its
     * layer,
     * it is moved to the appropriate layer.
     *
     * @param dt The time delta for the update.
     */
    public void onUpdate(double dt) {
        ArrayList<IGameObject> objectsToMove = new ArrayList<>();
        ArrayList<IGameObject> attacksToAdd = new ArrayList<>();

        for (Map.Entry<Integer, CopyOnWriteArrayList<IGameObject>> entry : layeredGameObjects.entrySet()) {
            List<IGameObject> layerObjects = entry.getValue();
            if (layerObjects == null)
                continue;

            for (IGameObject go : layerObjects) {
                int originalLayer = entry.getKey();

                if (go.transform() != null)
                    originalLayer = go.transform().layer();

                go.behavior().onUpdate(dt, this.inputStatus);

                IGameObject attack = go.behavior().attack(this.inputStatus);
                if (attack != null)
                    attacksToAdd.add(attack);

                int newLayer = go.transform().layer();
                if (originalLayer != newLayer)
                    objectsToMove.add(go);
            }
        }

        for (IGameObject go : objectsToMove) {
            destroy(go);
            add(go);
        }
        for (IGameObject go : attacksToAdd)
            addEnable(go);

        for (IGameObject go : this.disabledGameObjects)
            this.destroy(go);
    }

    /**
     * Checks for collisions for all enabled objects.
     * Calls `Behavior.onCollision(go)` for all enabled `GameObject`s,
     * passing in the list of all the objects that collided with each `IGameObject`.
     */
    @Override
    public void checkCollision() {
        ArrayList<IGameObject> output = new ArrayList<>();
        IGameObject currentObject = null;

        for (CopyOnWriteArrayList<IGameObject> layerObjects : layeredGameObjects.values()) {
            if (layerObjects == null || layerObjects.isEmpty())
                continue;

            int size = layerObjects.size();
            for (int i = 0; i < size; i++) {
                currentObject = layerObjects.get(i);
                for (int j = 0; j < size; j++) {
                    if (i == j)
                        continue;

                    IGameObject other = layerObjects.get(j);

                    if (currentObject.name().contains("Enemy") && other.name().contains("Enemy"))
                        continue;

                    //if (currentObject.name().contains("Bullet") && other.name().contains("Bullet"))
                    //    continue;

                    if (currentObject.collider().colision(other.collider()))
                        output.add(other);
                }

                if (!output.isEmpty()) {
                    currentObject.behavior().onCollision(output);
                    this.disabledGameObjects.addAll(output);
                    this.disabledGameObjects.add(currentObject);
                }

                output.clear();
            }
        }
    }

    /**
     * Adds an enabled `GameObject` to the engine.
     *
     * @param go The `GameObject` to enable.
     */
    @Override
    public void addEnable(IGameObject go) {
        this.enable(go);
        add((GameObject) go);
    }

    /**
     * Adds a disabled `GameObject` to the engine.
     *
     * @param go The `GameObject` to disable.
     */
    @Override
    public void addDisable(IGameObject go) {
        this.disable(go);
        this.disabledGameObjects.add(go);
    }

    /**
     * Enables a `GameObject` by calling its `onEnabled` behavior.
     *
     * @param go The `GameObject` to enable.
     */
    @Override
    public void enable(IGameObject go) {
        go.behavior().onEnabled();
    }

    /**
     * Disables a `GameObject` by calling its `onDisabled` behavior.
     *
     * @param go The `GameObject` to disable.
     */
    @Override
    public void disable(IGameObject go) {
        go.behavior().onDisabled();
    }

    /**
     * Checks if a `GameObject` is enabled.
     *
     * @param go The `GameObject` to check.
     * @return True if the `GameObject` is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled(IGameObject go) {
        return ((Behavior) go.behavior()).isEnabled();
    }

    /**
     * Checks if a `GameObject` is disabled.
     *
     * @param go The `GameObject` to check.
     * @return True if the `GameObject` is disabled, false otherwise.
     */
    @Override
    public boolean isDisabled(IGameObject go) {
        return !((Behavior) go.behavior()).isEnabled();
    }

    /**
     * Destroys all `GameObject`s in the engine.
     * Iterates through all layers and removes all `GameObject`s.
     */
    @Override
    public void destroyAll()
    {
        for (CopyOnWriteArrayList<IGameObject> layerObjects : layeredGameObjects.values())
        {
            if (layerObjects == null || layerObjects.isEmpty())
                continue;
            for (IGameObject go : layerObjects)
            {
                destroy(go);
            }
        }
    }


    /**
     * enable all `GameObject`s in the engine.
     * Iterates through all layers and enable all `GameObject`s.
     */
    @Override
    public void enableAll()
    {
        for (CopyOnWriteArrayList<IGameObject> layerObjects : layeredGameObjects.values())
        {
            if (layerObjects == null || layerObjects.isEmpty())
                continue;
            for (IGameObject go : layerObjects)
            {
                this.enable(go);
            }
        }
    }

    /**
     * Runs the game engine loop.
     * Continuously updates game objects, checks for collisions, and handles user
     * input.
     */
    @Override
    public void run() {
        final int FPS = 60;
        final long frameTime = 1000 / FPS;

        while (true) {
            long startTime = System.currentTimeMillis();

            this.onUpdate(FPS);
            this.checkCollision();
            this.gui.draw(getEnabledObjectsSnapshot());

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = frameTime - elapsed;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * Returns a thread-safe snapshot of the enabled (active) objects for rendering.
     *
     * @return A `CopyOnWriteArrayList` of enabled `GameObject`s.
     */
    public CopyOnWriteArrayList<IGameObject> getEnabledObjectsSnapshot() {
        CopyOnWriteArrayList<IGameObject> snapshot = new CopyOnWriteArrayList<>();
        synchronized (layeredGameObjects) {
            for (CopyOnWriteArrayList<IGameObject> layer : layeredGameObjects.values()) {
                for (IGameObject go : layer) {
                    if (isEnabled(go))
                        snapshot.add(go);
                }
            }
            return snapshot;
        }
    }


    public IGuiBridge getGui()
    {
        return gui;
    }
}