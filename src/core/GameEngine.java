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

    // Input and Sound event handler
    private IInputEvent inputStatus;

    private IGuiBridge gui;
    private IGameObject player;

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
        this.inputStatus = this.gui.getInputEvent();
    }

    public void setPlayer(IGameObject player)
    {
        this.player = player;
    }

    /**
     * Adds a `GameObject` to the engine.
     * If the layer does not exist, it creates a new layer.
     *
     * @param go The `GameObject` to add.
     */
    public void add(IGameObject go)
    {
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
    public void destroy(IGameObject go)
    {
        int layer = go.transform().layer();
        if (layeredGameObjects.containsKey(layer))
        {
            layeredGameObjects.get(layer).remove(go);
            this.totalObjects--;
            if (layeredGameObjects.get(layer).isEmpty()) // Remove the list if it is empty
                layeredGameObjects.remove(layer);
        }
    }

    public CopyOnWriteArrayList<IGameObject> get(int layer)
    {
        if (!layeredGameObjects.containsKey(layer) )
            return null;

            
        return layeredGameObjects.get(layer);
    }

    /**
     * Returns the total number of `GameObject`s in the engine.
     *
     * @return The total number of `GameObject`s.
     */
    public int size()
    {
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
     */
    public void onUpdate()
    {
        ArrayList<IGameObject> objectsToMove = new ArrayList<>();
        ArrayList<IGameObject> attacksToAdd = new ArrayList<>();

        for (Map.Entry<Integer, CopyOnWriteArrayList<IGameObject>> entry : layeredGameObjects.entrySet()) {
            List<IGameObject> layerObjects = entry.getValue();
            if (layerObjects == null)
                continue;

            for (IGameObject go : layerObjects)
            {

                if(this.isDisabled(go))
                {
                    this.disabledGameObjects.add(go);
                    continue; // Skip this object if it is disabled
                }
                int originalLayer = entry.getKey();

                if (go.transform() != null)
                    originalLayer = go.transform().layer();

                go.behavior().onUpdate(this.inputStatus);

                IGameObject attack = go.behavior().attack(this.inputStatus);
                if (attack != null)
                    attacksToAdd.add(attack);

                int newLayer = go.transform().layer();
                if (originalLayer != newLayer)
                    objectsToMove.add(go);
            }
        }

        for (IGameObject go : objectsToMove)
        {
            destroy(go);
            add(go);
        }
        for (IGameObject go : attacksToAdd)
            addEnable(go);

        for (IGameObject go : this.disabledGameObjects)
            {
                this.destroy(go);
            }
            this.disabledGameObjects.clear();
    }

    /**
     * Checks for collisions for all enabled objects.
     * Calls `Behavior.onCollision(go)` for all enabled `GameObject`s,
     * passing in the list of all the objects that collided with each `IGameObject`.
     */
    @Override
    public void checkCollision()
    {
        // Primeiro, verifica colisões dentro da mesma camada
        for (CopyOnWriteArrayList<IGameObject> layerObjects : layeredGameObjects.values())
        {
            if (layerObjects.isEmpty()) continue;

            Map<IGameObject, ArrayList<IGameObject>> layerCollisionMap = new HashMap<>();
            for (IGameObject obj : layerObjects)
            {
                layerCollisionMap.put(obj, new ArrayList<>());
            }
    
            for (int i = 0; i < layerObjects.size(); i++)
            {
                IGameObject currentObject = layerObjects.get(i);
    
                for (int j = i + 1; j < layerObjects.size(); j++)
                {
                    IGameObject other = layerObjects.get(j);
    
                    if (shouldSkipCollision(currentObject, other)) continue;
    
                    if (currentObject.collider().colision(other.collider()))
                    {
                        layerCollisionMap.get(currentObject).add(other);
                        layerCollisionMap.get(other).add(currentObject);
                    }
    
                }
            }
    
            for (IGameObject gameObject : layerObjects)
            {
                ArrayList<IGameObject> collidedWith = layerCollisionMap.get(gameObject);
                if (!collidedWith.isEmpty()) {
                    gameObject.behavior().onCollision(collidedWith);
                }
            }
        }

        // Agora, verifica colisões entre camadas diferentes, focando no jogador
        if (player != null)
        {
            int playerLayer = player.transform().layer();
            
            for (Map.Entry<Integer, CopyOnWriteArrayList<IGameObject>> entry : layeredGameObjects.entrySet()) {
                int currentLayer = entry.getKey();
                
                // Pula se for a mesma camada do jogador, pois já foi verificada
                if (currentLayer == playerLayer || currentLayer == 0) continue;
                
                CopyOnWriteArrayList<IGameObject> layerObjects = entry.getValue();
                for (IGameObject obj : layerObjects)
                {
                    if (shouldSkipCollision(player, obj) || obj.name().contains("Bullet")) continue;
                    
                    if (player.collider().colision(obj.collider()))
                    {
                        // Cria listas temporárias para armazenar as colisões
                        ArrayList<IGameObject> playerCollisions = new ArrayList<>();
                        ArrayList<IGameObject> objCollisions = new ArrayList<>();
                        
                        playerCollisions.add(obj);
                        objCollisions.add(player);
                        
                        // Notifica ambos os objetos sobre a colisão
                        player.behavior().onCollision(playerCollisions);
                        obj.behavior().onCollision(objCollisions);
                    }
                }
            }
        }
    }

    private boolean shouldSkipCollision(IGameObject obj1, IGameObject obj2)
    {
        return (obj1.name().contains("Enemy") && obj2.name().contains("Enemy")) ||
            (obj1.name().contains("Bullet") && obj2.name().contains("Bullet"));

    }

    /**
     * Adds an enabled `GameObject` to the engine.
     *
     * @param go The `GameObject` to enable.
     */
    @Override
    public void addEnable(IGameObject go)
    {
        this.enable(go);
        add(go);    
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
    public void disable(IGameObject go)
    {
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
        return (go.behavior()).isEnabled();
    }

    /**
     * Checks if a `GameObject` is disabled.
     *
     * @param go The `GameObject` to check.
     * @return True if the `GameObject` is disabled, false otherwise.
     */
    @Override
    public boolean isDisabled(IGameObject go) {
        return !( go.behavior()).isEnabled();
    }

    /**
     * Destroys all `GameObject`s in the engine.
     * Iterates through all layers and removes all `GameObject`s.
     */
    @Override
    public void destroyAll()
    {
        ArrayList<IGameObject> objectsToDestroy = new ArrayList<>();
        
        // Primeiro, colete todos os objetos que precisam ser destruídos
        for (CopyOnWriteArrayList<IGameObject> layerObjects : layeredGameObjects.values())
        {
            if (layerObjects != null && !layerObjects.isEmpty())
            {
                objectsToDestroy.addAll(layerObjects);
            }
        }
        
        // Depois, destrua cada objeto individualmente
        for (IGameObject go : objectsToDestroy)
        {
            destroy(go);
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
    public void run()
    {
        final int FPS = 60;
        final long frameTime = 1000 / FPS;

        while (true)
        {
            long startTime = System.currentTimeMillis();

            this.onUpdate();
            this.checkCollision();
            this.gui.draw(getEnabledObjectsSnapshot());

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = frameTime - elapsed;
            if (sleepTime > 0) 
            {
                try
                {
                    Thread.sleep(sleepTime);
                }
                catch (InterruptedException e) 
                {
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
    public CopyOnWriteArrayList<IGameObject> getEnabledObjectsSnapshot()
    {
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

/**
         * Retrieves the GUI bridge used for input and rendering.
         *
         * @return The `IGuiBridge` instance associated with the game engine.
         */
        public IGuiBridge getGui()
        {
            return this.gui;
        }
}