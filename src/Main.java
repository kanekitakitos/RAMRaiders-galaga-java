
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

        // Create player at (2, 2)

        Transform t1 = new Transform(new Ponto(5,0) ,0, 0.0, 1.0);
        Ponto[] points1 = {new Ponto(-0.5, 0.5), new Ponto(0.5, 0.5), new Ponto(0.5, -0.5), new Ponto(-0.5, -0.5)};
        Retangulo collider1 = new Retangulo(points1, t1);
        Shape shape1 = new Shape(AssetLoader.loadImage("nave.gif"));
        Behavior behavior1 = new PlayerBehavior();
        GameObject player = new GameObject("Player", t1, collider1, behavior1, shape1);
        player.onInit();
        objects.add(player);

        // Create 40 enemies (minimum required for the Galaga pattern)
//        ArrayList<IGameObject> enemies = new ArrayList<>();
//        for (int i = 0; i < 40; i++)
//        {
//            Transform t = new Transform(new Ponto(0, 0), 0, 0.0, 1.0);
//            Ponto[] points = {new Ponto(-0.3, 0.3), new Ponto(0.3, 0.3), new Ponto(0.3, -0.3), new Ponto(-0.3, -0.3)};
//            Retangulo collider = new Retangulo(points, t);
//            Shape shape = new Shape();
//            Behavior behavior = new EnemyBehavior(); // Using EnemyBehavior instead of basic Behavior
//            GameObject enemy = new GameObject("Enemy " + i, t, collider, behavior, shape);
//            enemies.add(enemy);
//        }

        // Position enemies using SimpleGroupAttack
        //SimpleGroupAttack groupAttack = new SimpleGroupAttack();
        //groupAttack.onInit(enemies, player);
        //groupAttack.execute(enemies, player);

        //objects.addAll(enemies);

        return objects;
    }
    public static void main(String[] args)
    {
        SwingGui gui = new SwingGui(800, 600);
        ArrayList<IGameObject> objects = createSampleObjects();
        GameEngine engine = new GameEngine(gui);

        for (IGameObject obj : objects)
            engine.addEnable(obj);

        engine.run(); // Start the game loop
    }

}