package core;

import java.util.ArrayList;
import core.behaviorItems.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import java.util.function.Function;
import java.util.concurrent.*;
import gui.IInputEvent;

/**
 * The `GameManager` class is responsible for managing the game's enemies,
 * player, and their interactions.
 * It handles the creation, configuration, and management of enemy objects, as
 * well as their behaviors
 * and attack/movement patterns.
 *
 * <p>
 * Responsibilities:
 * </p>
 * - Generate and manage enemy game objects.
 * - Assign movement and attack patterns to enemies.
 * - Enable enemies in the game engine.
 * - Execute group attack strategies.
 *
 * @see GameEngine
 * @see IGameObject
 * @see IEnemyMovement
 * @see IAttackStrategy
 * @see IGroupAttackStrategy
 * @see Transform
 * @see Poligono
 * @see EnemyBehavior
 * @see Shape
 * @see AssetLoader
 *
 * @author Brandon Mejia
 * @version 2025-04-20
 */
public class GameManager
{
    private CopyOnWriteArrayList<IGameObject> gameObjects = new CopyOnWriteArrayList<>(); // List of enemy game objects
    private IGameObject player; // The player game object
    private IGroupAttackStrategy groupAttackStrategy; // Strategy for group attacks

    private GameEngine engine; // The game engine managing game objects
    private IInputEvent input; // Input event mapping for keys and mouse buttons
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final double scale =4;




    /**
     * Validates the invariants for the `GameManager` class.
     * Ensures that the provided `GameEngine` and `IGameObject` instances are not null.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param engine The `GameEngine` instance managing game objects. Must not be null.
     */
    private void invariante(GameEngine engine)
    {
        if(engine != null)
            return;

        System.out.println("GameManager:iv");
        System.exit(0);
    }

    /**
     * Constructs a `GameManager` instance.
     *
     * @param engine The game engine to manage game objects.
     */
    public GameManager(GameEngine engine)
    {
        invariante(engine);
        this.engine = engine;
        createPlayer( new Shape(AssetLoader.loadAnimationFrames("player.gif"), 150));
        this.input = engine.getGui().getInput();

        this.groupAttackStrategy = new EnterGameGroup();
        ((EnterGameGroup)this.groupAttackStrategy).setScheduler(this.scheduler);


        Function<Integer, Integer> spawnIndexFunction = (Integer i) ->
        {
            
            int groupSize = 8;
            int groupNumber = i / groupSize;
            int patternIndex = groupNumber % 4;

            return patternIndex;
        };

        this.generateEnemies(groupAttackStrategy.getNumberOfEnemies(), spawnIndexFunction);
        this.groupAttackStrategy.onInit(this.gameObjects, player);

        this.generateMenuObjects();
    }


    /**
     * Generates enemy game objects and assigns them to the enemies list.
     *
     * @param count              The number of enemies to generate.
     * @param spawnIndexFunction A function to determine the spawn index for each
     *                           enemy.
     */
    public void generateEnemies(int count, Function<Integer, Integer> spawnIndexFunction)
    {

        if(spawnIndexFunction == null || count <= 0)
            throw new IllegalArgumentException("Invalid parameters for enemy generation");

        // Spawn constants
        int layer = this.player.transform().layer() + 1;
        double defaultY = this.player.transform().position().y() * 0.5;

        // Spawn positions and angles
        double bottomRight = 100;
        double bottomLeft = -bottomRight;
        double right = 400;
        double left = -right;
        double bottomY = 440;

        double[] spawnAngles = { 0.0, 180.0, 270.0, 270.0 };
        double[] spawnXCoords = { left, right, bottomLeft, bottomRight };
        double[] spawnYCoords = { defaultY, defaultY, bottomY, bottomY };

        Ponto[] pointsTriangulo = { new Ponto(0, 0), new Ponto(0, 12), new Ponto(12, 6) };

        for (int i = 0; i < count; i++)
        {
            int spawnIndex = spawnIndexFunction.apply(i);

            Ponto spawnPoint = new Ponto(spawnXCoords[spawnIndex], spawnYCoords[spawnIndex]);
            double spawnAngle = spawnAngles[spawnIndex];

            Transform t = new Transform(spawnPoint, layer, spawnAngle, scale);
            Poligono collider = new Poligono(pointsTriangulo, t);
            EnemyBehavior behavior = new EnemyBehavior();
            Shape shape;

            if( i > 7 && i <12 ) // the first 4 enemys
                shape = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 150);
            else if ( (i >= 0 && i <4) || (i >= 12 && i <= 23))
                shape = new Shape(AssetLoader.loadAnimationFrames("inimigo2.gif"), 150);
            else // final two lines
                shape = new Shape(AssetLoader.loadAnimationFrames("inimigo1.gif"), 150);

            GameObject enemy = new GameObject("Enemy " + i, t, collider, behavior, shape);
            enemy.onInit();
            enemy.behavior().subscribe(this.player);
            gameObjects.add(enemy);
        }

    }


    public void generateInfoStat()
    {
        // Spawn constants
        int layer = 0; // this layer is only for the info stat

        Ponto[] pointsQuadrado = { new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5)};
        // Example of life display
        PlayerBehavior playerBehavior = (PlayerBehavior) this.player.behavior();
        int lives = playerBehavior.getLife()-1; // This should be updated with the actual number of lives
        Ponto position = this.player.transform().position();
        for (int i = 0; i < lives; i++)
        {
            Shape shape = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 0);
            Transform transform = new Transform(new Ponto(position.x()+450 + i * 100, position.y()-20 ), layer, 90, scale);
            Poligono collider = new Poligono(pointsQuadrado, transform);
            
            GameObject lifeDisplay = new GameObject("Life "+(i+1), transform, collider, new EnemyBehavior(), shape);
            lifeDisplay.onInit();
            this.engine.add(lifeDisplay);
        }
    }


    public void generateMenuObjects()
    {
        ArrayList<IGameObject> objects = new ArrayList<>();
        
        // Spawn constants
        double scale = 32;
        double raio = 0.0001;
        int layer = 0;
        // Start Game
        Shape s1 = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 0);
        Transform t1 = new Transform(new Ponto(0.0, 0.0), layer, 0.0, scale);
        Circulo p1 = new Circulo(raio, t1);
        GameObject startGame = new GameObject("Start Game", t1, p1, new Behavior(), s1);
        startGame.onInit();
        objects.add(startGame);

        // RAMRaiders
        scale = 64;
        s1 = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(0.0, startGame.transform().position().y()+140), layer, 0.0, scale); // posição Y diferente
        p1 = new Circulo(raio, t1);
        GameObject nomeGrupo = new GameObject("RAM-Raiders", t1, p1, new Behavior(), s1);
        nomeGrupo.onInit();
        objects.add(nomeGrupo);

        // Nome do grupo
        scale = 15;
        s1 = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(0.0, startGame.transform().position().y()-370), layer, 0.0, scale); // posição Y diferente
        p1 = new Circulo(raio, t1);
        GameObject grupo = new GameObject("Gabriel Pedroso                      Brandon Mejia                      Miguel Correia", t1, p1, new Behavior(), s1);
        grupo.onInit();
        objects.add(grupo);
        

        // Skin 1 e 2
        scale = 20;
        s1 = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(-150, startGame.transform().position().y()-80), layer, 180, scale); // posição Y diferente
        p1 = new Circulo(raio, t1);
        GameObject skin1 = new GameObject("Press 1", t1, p1, new Behavior(), s1);
        skin1.onInit();
        objects.add(skin1);

        s1 = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(150.0, startGame.transform().position().y()-80), layer, 180, scale); // posição Y diferente
        p1 = new Circulo(raio, t1);
        GameObject skin2 = new GameObject("Press 2", t1, p1, new Behavior(), s1);
        skin2.onInit();
        objects.add(skin2);

        double offset = 90;
        raio = 10;
        s1 = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 150);
        t1 = new Transform(new Ponto(skin1.transform().position().x(), skin1.transform().position().y()-offset), layer+1, 90, this.scale); // posição Y diferente
        p1 = new Circulo(raio, t1);
        GameObject nave1 = new GameObject("nave", t1, p1, new Behavior(), s1);
        nave1.onInit();
        objects.add(nave1);

        s1 = new Shape(AssetLoader.loadAnimationFrames("nave-HanSolo.png"), 0);
        t1 = new Transform(new Ponto(skin2.transform().position().x(), skin2.transform().position().y()-offset), layer+1, 90, this.scale); // posição Y diferente
        p1 = new Circulo(raio-1, t1);
        GameObject nave2 = new GameObject("nave", t1, p1, new Behavior(), s1);
        nave2.onInit();
        objects.add(nave2);



        for (int i = 0; i < objects.size(); i++)
            this.engine.addEnable(objects.get(i));

    }

    /**
     * Retrieves the list of enemy game objects.
     *
     * @return The list of enemies.
     */
    public CopyOnWriteArrayList<IGameObject> getEnemys()
    {
        return gameObjects;
    }

    /**
     * Executes the group attack strategy to relocate enemies.
     */
    private void startRelocateEnemies()
    {
        this.groupAttackStrategy.execute(this.gameObjects, this.player);

        // Verifica periodicamente se o movimento foi completado, mas só executa o ataque uma vez
        //this.scheduler.scheduleAtFixedRate(new Runnable() {
        //    private boolean attackStarted = false;

        //    @Override
        //    public void run()
        //    {
        //        if (!attackStarted && groupAttackStrategy.isGroupAttackComplete())
        //        {
        //            attackStarted = true;
        //            ZigzagGroup zigzagGroup = new ZigzagGroup();
        //            zigzagGroup.setScheduler(scheduler);
        //            zigzagGroup.onInit(enemies, player);
        //            zigzagGroup.execute(enemies, player);

        //            groupAttackStrategy = zigzagGroup;
        //        }
        //    }
        //}, 10000, 100, TimeUnit.MILLISECONDS);
    }

    public void shutdown()
    {
        if (scheduler != null && !scheduler.isShutdown())
        {
            scheduler.shutdown();
        }
    }


    /**
     * Configures and returns the input mapping for the game.
     *
     * @return InputEvent The input event mapping for keys and mouse buttons.
     */
    public IInputEvent getInput()
    {

        return this.input; // Return the input event mapping
    }


    public void handlerSelectPlayer()
    {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(() ->
        {
            
            if(input.isActionActive("PLAYER1"))
            {
                System.out.println("1");
                // Jogador 1 selecionado
                finalizarSelecao();
                scheduler.shutdown();
                
            } else if(input.isActionActive("PLAYER2"))
            {
                this.player.shape().setFrames(AssetLoader.loadAnimationFrames("nave-HanSolo.png"), 150);
                finalizarSelecao();
                scheduler.shutdown();
                
            }
        }, 0, 1, TimeUnit.MILLISECONDS); // Verifica a cada 100ms
    }

    // Método auxiliar para finalizar a seleção
    private void finalizarSelecao()
    {
        this.engine.getGui().setMenu(false);
        this.engine.destroyAll();
        return;
    }

    public void startGame()
    {
        
        if(this.engine.getGui().isMenu())
            this.handlerSelectPlayer();

                System.out.println(this.gameObjects.size());
                for (int i = 0; i < this.gameObjects.size(); i++)
                    this.engine.add(this.gameObjects.get(i));

                this.engine.add(this.player);
                this.engine.enableAll();
                this.startRelocateEnemies();
                this.monitorPlayer();
            
            System.out.println("GAMEEEEE STARTTT");
            this.engine.run();
        
    }

    private void createPlayer(Shape shape)
    {
        double scale = 4; // Scale of the player object
        int layer = 1; // Layer of the player object
        double angle = 90; // Initial angle of the player object
        Ponto position = new Ponto(0, -330); // Initial position of the player object
        Ponto[] points = {new Ponto(0, 0), new Ponto(0, 12), new Ponto(12, 6)}; // Points for the collider

        Transform t1 = new Transform(position, layer, angle, scale); // Transform for the player
        Poligono collider = new Poligono(points, t1); // Polygon collider for the player
        // Circulo collider = new Circulo(5, t1); // Alternative circular collider (commented out)

        PlayerBehavior behavior = new PlayerBehavior(); // Behavior of the player
        GameObject player = new GameObject("Player", t1, collider, behavior, shape); // Create the player game object
        player.onInit(); // Initialize the player
        this.player = player; // Set the player as the current player
    }

    private void monitorPlayer()
    {
        this.generateInfoStat();
        ArrayList<IGameObject> lifeDisplays = new ArrayList<>(this.engine.get(0));
        this.scheduler.scheduleAtFixedRate(
            () ->
            {
                PlayerBehavior playerBehavior = (PlayerBehavior) this.player.behavior();
                int vidasAtuais = playerBehavior.getLife();
                int lifes = lifeDisplays.size()+1;

                if (lifes > vidasAtuais)
                {
                    IGameObject lifeDisplay = lifeDisplays.remove(lifeDisplays.size() - 1);
                    this.engine.destroy(lifeDisplay);
                }

                if (vidasAtuais <= 0)
                {
                    this.engine.destroyAll();
                    this.shutdown();
                    return;
                }
            }, 1, 100, TimeUnit.MILLISECONDS);
    }
}