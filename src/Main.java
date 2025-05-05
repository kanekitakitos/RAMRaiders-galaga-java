
import core.*;
import core.objectsInterface.*;
import geometry.*;
import java.util.ArrayList;

import assets.*;
import gui.*;

public class Main
{
    public static ArrayList<IGameObject> createSampleObjects()
    {
        ArrayList<IGameObject> objects = new ArrayList<>();
        double scale = 6;

        // Create player at (2, 2)

        Transform t1 = new Transform(new Ponto(0,-10) ,0, 0.0, scale);
        //Ponto[] points = {new Ponto(0,4), new Ponto(2,2) , new Ponto(4,2), new Ponto(5.54,3.99), new Ponto(6,4),new Ponto(6,0),new Ponto(-6,0),new Ponto(-6,4), new Ponto(-5.6,3.99), new Ponto(-4,2) , new Ponto(-2,2)};
        //Ponto[] points = {new Ponto(0, 0), new Ponto(0, 6), new Ponto(6, 6), new Ponto(6, 0)};
        
        Ponto[] points1 = {new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5)};
        Poligono collider1 = new Poligono(points1, t1);
        Shape shape1 = new Shape(AssetLoader.loadAnimationFrames("nave-HanSolo.png"),550);
        Behavior behavior1 = new PlayerBehavior();
        GameObject player = new GameObject("Player", t1, collider1, behavior1, shape1);
        player.onInit();
        objects.add(player);

         //Create 40 enemies (minimum required for the Galaga pattern)
        ArrayList<IGameObject> enemies = new ArrayList<>();
        for (int i = 0; i < 40; i++)
        {
            Transform t = new Transform(new Ponto(0, 12), 0, 270, 2.0);
            Retangulo collider = new Retangulo(points1, t);
            Shape shape = new Shape(AssetLoader.loadAnimationFrames("nave.png"),550);
            Behavior behavior = new EnemyBehavior();
            GameObject enemy = new GameObject("Enemy " + i, t, collider, behavior, shape);
            enemy.onInit();
            enemies.add(enemy);
        }

        // Position enemies using SimpleGroupAttack
      

        
        //objects.addAll(enemies);

        Transform t = new Transform(new Ponto(100, -10), 0, 180.0, scale);
            Retangulo collider = new Retangulo(points1, t);
            Shape shape = new Shape(AssetLoader.loadAnimationFrames("nave.png"),550);
            Behavior behavior = new EnemyBehavior();
            GameObject enemy = new GameObject("Enemy 0", t, collider, behavior, shape);
            enemy.onInit();
            enemies.add(enemy);

            objects.add(enemy);

        return objects;
    }
    public static void main(String[] args)
    {

        Shape backGroundShape = new Shape(AssetLoader.loadAnimationFrames("background.gif"), 1000);

        SwingGui gui = new SwingGui(800, 600, backGroundShape);
        gui.setHitbox(true);


        ArrayList<IGameObject> objects = createSampleObjects();
        GameEngine engine = new GameEngine(gui);

        for (IGameObject obj : objects)
            engine.addEnable(obj);

        engine.run(); // Start the game loop
       
    }

}