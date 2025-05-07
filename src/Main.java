
import core.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import gui.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import gui.InputEvent;


public class Main
{
    public static IGameObject createPlayer()
    {
        double scale = 4;
        int layer = 0;
        double angle = 90;
        Ponto position = new Ponto(0,-330);
        Ponto[] points = {new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5)};

        Transform t1 = new Transform(position ,layer, angle, scale);
        Poligono collider = new Poligono(points, t1);
        // Circulo collider = new Circulo(5, t1);
        Shape shape = new Shape(AssetLoader.loadAnimationFrames("nave.gif"),550);
        PlayerBehavior behavior = new PlayerBehavior();
        GameObject player = new GameObject("Player", t1, collider, behavior, shape);
        player.onInit();

        return player;
    }

    public static void createGameEngine(SwingGui GUI)
    {
        

        GameEngine engine = new GameEngine(GUI);

        IGameObject player = createPlayer();
        GameManager gameManager = new GameManager(engine, player);
        gameManager.enableEnemiesToEngine();

        engine.addEnable(player);
        gameManager.startRelocateEnemies();
        engine.run(); // Start the game loop
    }

    public static InputEvent getInput()
    {
        Map<Integer, String> customKeyMap = new HashMap<>();
            customKeyMap.put(KeyEvent.VK_A, "LEFT");
            customKeyMap.put(KeyEvent.VK_LEFT, "LEFT");
            customKeyMap.put(KeyEvent.VK_RIGHT, "RIGHT");
            customKeyMap.put(KeyEvent.VK_D, "RIGHT");
            customKeyMap.put(KeyEvent.VK_C, "ATTACK");
            customKeyMap.put(KeyEvent.VK_X, "EVASIVE");
    
    
            Map<Integer, String> customMouseMap = new HashMap<>();
            customMouseMap.put(MouseEvent.BUTTON1, "ATTACK");
            customMouseMap.put(MouseEvent.BUTTON3, "EVASIVE");
            return new InputEvent(customKeyMap, customMouseMap);
    }

    public static void main(String[] args)
        {
            Shape backGroundShape = new Shape(AssetLoader.loadAnimationFrames("background2.gif"), 1000);
            SwingGui gui = new SwingGui(1100, 800, backGroundShape);
            gui.setHitbox(true);

            gui.setInput(getInput());

            createGameEngine(gui);
        }


}