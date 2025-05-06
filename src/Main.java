
import core.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import gui.*;





public class Main
{
    public static IGameObject createPlayer()
    {
        
        double scale = 6;
        int layer = 0;
        double angle = 90;
        Ponto position = new Ponto(0,-350);
        Ponto[] points = {new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5)};


        Transform t1 = new Transform(position ,layer, angle, scale);
        Poligono collider = new Poligono(points, t1);
        Shape shape = new Shape(AssetLoader.loadAnimationFrames("nave-HanSolo.png"),550);
        PlayerBehavior behavior = new PlayerBehavior();
        GameObject player = new GameObject("Player", t1, collider, behavior, shape);
        player.onInit();

        return player;
    }
    public static void main(String[] args)
    {

        Shape backGroundShape = new Shape(AssetLoader.loadAnimationFrames("background2.gif"), 1000);
        SwingGui gui = new SwingGui(1100, 800, backGroundShape);
        gui.setHitbox(true);

        GameEngine engine = new GameEngine(gui);

        IGameObject player = createPlayer();
        GameManager gameManager = new GameManager(engine, player);
        gameManager.enableEnemiesToEngine();

        engine.addEnable(player);
        gameManager.startRelocateEnemies();
        engine.run(); // Start the game loop
    }

}