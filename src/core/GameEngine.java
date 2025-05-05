package core;

 import core.objectsInterface.IGameEngine;
 import core.objectsInterface.IGameObject;
 import core.objectsInterface.IInputEvent;
import gui.*;

import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;

 /**
  * The GameEngine class manages game objects, their layers, and updates.
  * It allows adding, removing, and retrieving game objects, as well as checking for collisions.
  * This class implements the IGameEngine interface.
  *
  *   Responsibilities:
  * - Manage game objects organized by layers.
  * - Handle updates, collisions, and enabling/disabling of game objects.
  * - Provide utility methods for adding, removing, and retrieving game objects.
  *
  * @see GameObject
  * @see InputEvent
  *
  * @author Brandon Mejia
  * @author Gabriel Pedroso
  * @author Miguel Correia
  *
  * @version 2025-04-16
  */
 public class GameEngine implements IGameEngine
 {
     // Stores game objects organized by layers
     private HashMap<Integer, ArrayList<IGameObject>> layeredGameObjects;
     private ArrayList<IGameObject> disabledGameObjects;

     // Tracks the total number of game objects
     private int totalObjects;

     // Input event handler
     private InputEvent inputStatus;
     private IGuiBridge gui;

     /**
      * Constructs a new GameEngine instance.
      * Initializes the layered game objects map and sets the total object count to zero.
      */
     public GameEngine(IGuiBridge gui)
     {
         this.layeredGameObjects = new HashMap<>();
         this.disabledGameObjects = new ArrayList<>();
         this.totalObjects = 0;
         this.gui = gui;
         this.inputStatus = this.gui.getInputState();
     }

     /**
      * Adds a GameObject to the engine.
      * If the layer does not exist, it creates a new layer.
      *
      * @param go The GameObject to add.
      */
     public void add(IGameObject go)
     {
         int layer = go.transform().layer();
         if (!layeredGameObjects.containsKey(layer))
             layeredGameObjects.put(layer, new ArrayList<>());

         layeredGameObjects.get(layer).add(go);
         this.totalObjects++;
     }

     /**
      * Removes a GameObject from the engine.
      * If the layer becomes empty after removal, it deletes the layer.
      *
      * @param go The GameObject to remove.
      */
     @Override
     public void destroy(IGameObject go)
     {
         int layer = go.transform().layer();
         if (layeredGameObjects.containsKey(layer)) {
             layeredGameObjects.get(layer).remove(go);
             this.totalObjects--;
             if (layeredGameObjects.get(layer).isEmpty()) // Remove the list if it is empty
                 layeredGameObjects.remove(layer);
         }
     }

     /**
      * Retrieves a GameObject from a specific layer and index.
      *
      * @param layer The layer of the GameObject.
      * @param indexGameObject The index of the GameObject within the layer.
      * @return The GameObject if found, otherwise null.
      */
     public IGameObject get(int layer, int indexGameObject)
     {
         if (!layeredGameObjects.containsKey(layer) || indexGameObject < 0 || indexGameObject > this.totalObjects)
             return null;

         ArrayList<IGameObject> layerObjects = layeredGameObjects.get(layer);
         return layerObjects.get(indexGameObject);
     }

     /**
      * Returns the total number of GameObjects in the engine.
      *
      * @return The total number of GameObjects.
      */
     public int size() {
         return this.totalObjects;
     }




     /**
      * Updates all GameObjects in the engine, moving them between layers if necessary.
      * This method iterates through all layers and their respective GameObjects,
      * using the update method of each GameObject. If a GameObject changes its layer,
      * it is moved to the appropriate layer.
      */
     public void onUpdate(double dt)
     {
         // List for objects that need to change layers
         ArrayList<IGameObject> objectsToMove = new ArrayList<>();

         // Iterate through each layer in the map
         for (Map.Entry<Integer, ArrayList<IGameObject>> entry : layeredGameObjects.entrySet())
         {
             ArrayList<IGameObject> layerObjects = entry.getValue();
             if (layerObjects == null) continue;

             // Iterate through each GameObject in the layer
             for (IGameObject go : layerObjects)
             {
                 int originalLayer = entry.getKey();

                 if (go.transform() != null)
                     originalLayer = go.transform().layer();

                 // Update the GameObject
                 go.behavior().onUpdate(dt,this.inputStatus);

                 int newLayer = go.transform().layer();
                 if (originalLayer != newLayer)
                     objectsToMove.add(go);
             }
         }

         // Reposition objects that changed layers
         for (IGameObject go : objectsToMove)
         {
             destroy(go);
             add(go);
         }
     }

     /**
      * Checks for collisions for all enabled objects.
      * Calls Behavior.onCollision(go) for all enabled GameObjects,
      * passing in the list of all the objects that collided with each IGameObject.
      */
     @Override
     public void checkCollision()
     {
         ArrayList<IGameObject> output = new ArrayList<>();
         IGameObject currentObject = null;

         // Iterate through each layer in the map
         for (ArrayList<IGameObject> layerObjects : layeredGameObjects.values())
         {
             if (layerObjects == null || layerObjects.isEmpty()) continue;

             int size = layerObjects.size();
             for (int i = 0; i < size; i++)
             {
                 currentObject = layerObjects.get(i);
                 for (int j = 0; j < size; j++)
                 {
                     if (i == j) continue;

                     IGameObject other = layerObjects.get(j);
                     // Check for collision
                     if (currentObject.collider().colision(other.collider()))
                         output.add(other);
                 }

                 if(!output.isEmpty())
                    currentObject.behavior().onCollision(output);

                 output.clear();
             }
         }
     }

     /**
      * Adds an enabled GameObject to the engine.
      *
      * @param go The GameObject to enable.
      */
     @Override
     public void addEnable(IGameObject go)
     {
         this.enable(go);
         add((GameObject) go);
     }

     /**
      * Adds a disabled GameObject to the engine.
      *
      * @param go The GameObject to disable.
      */
     @Override
     public void addDisable(IGameObject go)
     {
         this.disable(go);
         this.disabledGameObjects.add(go);
     }

     /**
      * Enables a GameObject by calling its onEnabled behavior.
      *
      * @param go The GameObject to enable.
      */
     @Override
     public void enable(IGameObject go)
     {
         go.behavior().onEnabled();
     }

     /**
      * Disables a GameObject by calling its onDisabled behavior.
      *
      * @param go The GameObject to disable.
      */
     @Override
     public void disable(IGameObject go)
     {
         go.behavior().onDisabled();
     }

     /**
      * Checks if a GameObject is enabled.
      *
      * @param go The GameObject to check.
      * @return True if the GameObject is enabled, false otherwise.
      */
     @Override
     public boolean isEnabled(IGameObject go)
     {
         return ((Behavior) go.behavior()).isEnabled() == true;
     }

     /**
      * Checks if a GameObject is disabled.
      *
      * @param go The GameObject to check.
      * @return True if the GameObject is disabled, false otherwise.
      */
     @Override
     public boolean isDisabled(IGameObject go)
     {
         return ((Behavior) go.behavior()).isEnabled() == false;
     }

     /**
      * Destroys all GameObjects in the engine.
      * Iterates through all layers and removes all GameObjects.
      */
     @Override
     public void destroyAll()
     {
         for (ArrayList<IGameObject> layerObjects : layeredGameObjects.values())
         {
             if (layerObjects == null || layerObjects.isEmpty()) continue;
             for (IGameObject go : layerObjects) {
                 destroy(go);
             }
         }
     }

     /**
      * Runs the game engine loop.
      * Continuously updates game objects, checks for collisions, and handles user input.
      */
     @Override
     public void run()
     {
         final int FPS = 60;
         final long frameTime = 1000 / FPS; // em milissegundos

         while (true)
         {
             long startTime = System.currentTimeMillis();


             // Atualiza lógica do jogo
             this.onUpdate(FPS);

             // Checa colisões
             this.checkCollision();

             // Renderiza objetos ativos
             this.gui.draw(getEnabledObjectsSnapshot());

             // Controle de FPS
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
      * Devuelve una copia segura de los objetos habilitados (activos) para renderizar.
      * Es thread-safe usando synchronized sobre layeredGameObjects.
      */
     public ArrayList<IGameObject> getEnabledObjectsSnapshot()
     {
         ArrayList<IGameObject> snapshot = new ArrayList<>();
         synchronized (layeredGameObjects)
         {
             for (ArrayList<IGameObject> layer : layeredGameObjects.values())
             {
                 for (IGameObject go : layer)
                 {
                     if (isEnabled(go))
                         snapshot.add(go);
                 }
             }
             return snapshot;
         }
     }
 }