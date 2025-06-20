package core;

import java.util.ArrayList;

import core.EnemyGroupAttack.EnterGameGroup;
import core.EnemyGroupAttack.IGroupAttackStrategy;
import core.EnemyGroupAttack.ZigzagGroup;
import core.behaviorItems.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import java.util.function.Function;
import java.util.concurrent.*;

import gui.IGuiBridge;
import gui.IInputEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.concurrent.TimeUnit;

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
 * @see <a href=
 *      "https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html">
 *      Synchronization </a>
 * @see <a href=
 *      "https://docs.oracle.com/javase/tutorial/essential/concurrency/atomic.html">
 *      Atomic Access </a>
 * @see <a href=
 *      "https://docs.oracle.com/javase/tutorial/essential/concurrency/atomicvars.html">
 *      Atomic Variables </a>
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
 * @see ImagesLoader
 *
 * @author Brandon Mejia
 * @version 2025-04-20
 */
public class GameManager {

    private CopyOnWriteArrayList<IGameObject> enemys = new CopyOnWriteArrayList<>(); // List of enemy game objects
    private IGameObject player = null; // The player game object
    private GameObject score = null;
    private IGroupAttackStrategy groupAttackStrategy; // Strategy for group attacks

    private GameEngine engine; // The game engine managing game objects
    private IInputEvent input; // Input event mapping for keys and mouse buttons
    private ISoundEffects soundEffects = new SoundEffects(); // The sound effects of the game object
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final double scale = 4;

    
    private void invariante(IGuiBridge gui) {
        if (gui != null)
            return;

        System.out.println("GameManager:iv");
        System.exit(0);
    }
    // -------------------------------------------------------------------------------------------------------------------------------


    /**
     * Constructs a new `GameManager` instance and initializes the game engine, player,
     * sound effects, group attack strategy, enemies, and menu objects.
     *
     * @param gui The GUI bridge interface used to interact with the game's graphical user interface.
     * @throws IllegalArgumentException if the provided `gui` is null.
     */
    public GameManager(IGuiBridge gui)
    {
        // Ensures the GUI bridge is not null; exits the program if it is.
        invariante(gui);

        // Initializes the game engine with the provided GUI bridge.
        this.engine = new GameEngine(gui);

        // Creates the player game object with a specific shape and animation.
        createPlayer(new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 150));

        // Retrieves the input event mapping from the GUI.
        this.input = engine.getGui().getInputEvent();

        // Adds sound effects for various game states.
        soundEffects.addSound("GAMEOVER", AudioLoader.loadAudio("gameOver.wav"));
        soundEffects.addSound("MENU", AudioLoader.loadAudio("menu.wav"));
        soundEffects.addSound("WIN", AudioLoader.loadAudio("win.wav"));

        // Randomly selects one of two sound files for the "STARTGAME" sound effect.
        String[] startGameSounds = { "gameSound.wav", "gameSound2.wav" };
        Random rand = new Random();
        String selectedSound = startGameSounds[rand.nextInt(startGameSounds.length)];
        soundEffects.addSound("STARTGAME", AudioLoader.loadAudio(selectedSound));

        // Initializes the group attack strategy and sets its scheduler.
        this.groupAttackStrategy = new EnterGameGroup();
        ((EnterGameGroup) this.groupAttackStrategy).setScheduler(this.scheduler);

        // Defines a function to determine the spawn index for each enemy based on its position in the group.
        Function<Integer, Integer> spawnIndexFunction = (Integer i) -> {
            int groupSize = 8; // Number of enemies in a group.
            int groupNumber = i / groupSize; // Determines the group number.
            int patternIndex = groupNumber % 4; // Cycles through 4 spawn patterns.

            return patternIndex;
        };

        // Generates enemies using the group attack strategy's enemy count and the spawn index function.
        this.generateEnemies(groupAttackStrategy.getNumberOfEnemies(), spawnIndexFunction);

        // Initializes the group attack strategy with the list of enemies and the player.
        this.groupAttackStrategy.onInit(this.enemys, player);

        // Generates the menu objects for the game.
        this.generateMenuObjects();
    }



    /**
     * Shuts down the scheduler service.
     * Ensures that the scheduler is not null and has not already been shut down
     * before attempting to shut it down.
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Retrieves the list of enemy game objects.
     *
     * @return The list of enemies.
     */
    public CopyOnWriteArrayList<IGameObject> getEnemys() {
        return enemys;
    }

    /**
     * Executes the group attack strategy to relocate enemies.
     */
    private void startRelocateEnemies()
    {
        this.groupAttackStrategy.execute(this.enemys, this.player);

        // Verifica periodicamente se o movimento foi completado e executa o zigzag a cada 20 segundos
        this.scheduler.scheduleAtFixedRate(new Runnable()
        {
            private boolean firstAttackStarted = false;

            @Override
            public void run()
            {
                if (!firstAttackStarted && groupAttackStrategy.isGroupAttackComplete())
                {
                    firstAttackStarted = true;
                    executeZigzagAttack();
                    
                    // Agenda a repetição do zigzag a cada 20 segundos
                    scheduler.scheduleAtFixedRate(() -> {
                        executeZigzagAttack();
                    }, 13, 60, TimeUnit.SECONDS);
                }
            }

            private void executeZigzagAttack() {
                ZigzagGroup zigzagGroup = new ZigzagGroup();
                zigzagGroup.onInit(enemys, player);
                zigzagGroup.setScheduler(scheduler);
                zigzagGroup.execute(enemys, player);
                groupAttackStrategy = zigzagGroup;
            }
        }, 1000, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Counts the number of active enemies in the game.
     * Removes disabled enemies from the game objects list.
     *
     * @return The count of active enemies.
     */
    public int countActiveEnemies()
    {
        int count = 0;
        for (IGameObject enemy : enemys)
        {
            if (enemy.transform().layer() == this.player.transform().layer() + 1 && this.engine.isEnabled(enemy))
                count++;

        }


        return count;
    }

    //private boolean alreadyAppliedToAllEnemies = false;
    /**
     * Monitors the player's status, including lives and score.
     * Updates the game state based on the player's life count and the number of
     * active enemies.
     * Schedules periodic checks to handle game over or victory conditions.
     */
    private void monitorPlayer()
    {
        this.generateInfoStat();
        ArrayList<IGameObject> lifeDisplays = new ArrayList<>();
        for (IGameObject obj : this.engine.get(0))
            if (obj.name().toLowerCase().contains("life"))
                lifeDisplays.add(obj);

        this.scheduler.scheduleAtFixedRate(() -> {

                    PlayerBehavior playerBehavior = (PlayerBehavior) this.player.behavior();
                    int vidasAtuais = playerBehavior.getLife();
                    Behavior behavior = (Behavior) this.score.behavior();
                    behavior.setScore(playerBehavior.getScore());

                    int lifes = lifeDisplays.size() + 1;

                    if (lifes > vidasAtuais && vidasAtuais > 0) {
                        IGameObject lifeDisplay = lifeDisplays.remove(lifeDisplays.size() - 1);
                        this.engine.destroy(lifeDisplay);
                    }

                    if (vidasAtuais <= 0)
                        this.generateGameOver();

                    int numberOfEnemies = this.countActiveEnemies();

                    if (numberOfEnemies == 0)
                        this.generateWin();

                    // Executa apenas uma vez
                    //if (numberOfEnemies <= 20 && !alreadyAppliedToAllEnemies)
                    //{
                    //    boolean passa = areAllEnemiesStopped();
                    //    if(passa)
                    //    {
                    //        System.out.println("ENTROOOO");
                    //        applyToAllEnemies();
                    //        alreadyAppliedToAllEnemies = true;
                    //    }
                    //}
                    // this.randomAttacksAndMovements();

                }, 100, 100, TimeUnit.MILLISECONDS);
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

    /**
     * Handles the player selection process in the game menu.
     * Loops the "MENU" sound and continuously checks for player input to select a
     * player.
     * Once a player is selected, it finalizes the selection and stops the loop.
     */
    private void handlerSelectPlayer()
    {
        AtomicBoolean running = new AtomicBoolean(true);
        this.soundEffects.loopSound("MENU");
        scheduler.scheduleAtFixedRate(() ->
        {
            if (!running.get())
                return;

            if (input.isActionActive("PLAYER1"))
            {
                handlerFinalSelectPlayer();
                running.set(false);
            } else if (input.isActionActive("PLAYER2"))
            {
                this.player.shape().setFrames(ImagesLoader.loadAnimationFrames("nave-HanSolo.png"), 150);
                handlerFinalSelectPlayer();
                running.set(false);
            }

        }, 0, 1, TimeUnit.MILLISECONDS);

    }

    /**
     * Finalizes the player selection process.
     * Disables the menu, destroys all existing game objects, and enables the
     * selected player and enemies.
     * Stops the "MENU" sound and starts the "STARTGAME" sound.
     * Initializes enemy relocation and player monitoring.
     */
    private void handlerFinalSelectPlayer()
    {
        this.engine.getGui().setMenu(false);
        this.engine.destroyAll();

        for (int i = 0; i < this.enemys.size(); i++)
            this.engine.addEnable(this.enemys.get(i));

        this.engine.addEnable(this.player);
        this.engine.setPlayer(player);

        this.soundEffects.stopSound("MENU");
        this.soundEffects.loopSound("STARTGAME");
        this.startRelocateEnemies();

        this.monitorPlayer();

    }


    public void applyToAllEnemies()
    {
        FlyTopDownMovement movement = null;
        for (IGameObject enemy : enemys)
        {

            if (enemy != null && engine.isEnabled(enemy))
            {
                EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
                // Sempre desativa o movimento atual se existir
                if (behavior.getMovement() != null)
                   behavior.setMovement(null);
                // Aplica o novo movimento (mesmo que seja null)
                movement = new FlyTopDownMovement();
                movement.setDirection(enemy.transform().position().x()*-1 > 0);
                behavior.setMovement(movement);
                Ponto p =enemy.transform().position();
                movement.setTarget(new Ponto(p.x(),p.y()-100));

                movement.setActive(true);

                behavior.setAttackStrategy(null);
            }
        }
    }

    public boolean areAllEnemiesStopped()
    {
        for (IGameObject enemy : enemys)
        {
            if (enemy != null)
            {
                if(!enemy.behavior().isEnabled())
                    continue;

                GameObject enemyObj = (GameObject) enemy;
                Ponto velocity = enemyObj.velocity();
                    if(velocity.x() != 0 || velocity.y() != 0)
                        return false; // Se um inimigo tiver velocidade, não está parado
            }
        }
        // Se chegou aqui, significa que todos os inimigos estão parados
        return true;
    }


    /**
     * Sets the visibility of hitboxes in the game.
     *
     * <p>
     * This method enables or disables the display of hitboxes in the game's GUI,
     * depending on the provided parameter.
     * </p>
     *
     * @param hitbox A boolean value indicating whether to show hitboxes (true) or hide them (false).
     */
    public void setHitbox(boolean hitbox)
    {
        this.engine.getGui().setHitbox(hitbox);
    }

    /**
     * Starts the game.
     * If the game is in the menu state, it initiates the player selection process.
     * Otherwise, it starts the game engine.
     */
    public void startGame() {
        if (this.engine.getGui().isMenu())
            this.handlerSelectPlayer();

        this.engine.run();
    }

    // -------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------

    /**
     * Generates enemy game objects and assigns them to the enemies list.
     *
     * @param count              The number of enemies to generate.
     * @param spawnIndexFunction A function to determine the spawn index for each
     *                           enemy.
     */
    public void generateEnemies(int count, Function<Integer, Integer> spawnIndexFunction) {

        if (spawnIndexFunction == null || count <= 0)
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

        for (int i = 0; i < count; i++) {
            int spawnIndex = spawnIndexFunction.apply(i);

            Ponto spawnPoint = new Ponto(spawnXCoords[spawnIndex], spawnYCoords[spawnIndex]);
            double spawnAngle = spawnAngles[spawnIndex];

            Transform t = new Transform(spawnPoint, layer, spawnAngle, scale);
            Poligono collider = new Poligono(pointsTriangulo, t);
            EnemyBehavior behavior = new EnemyBehavior();
            Shape shape;

            if (i > 7 && i < 12) // the first 4 enemys
                shape = new Shape(ImagesLoader.loadAnimationFrames("inimigo3.gif"), 150);
            else if ((i >= 0 && i < 4) || (i >= 12 && i <= 23))
                shape = new Shape(ImagesLoader.loadAnimationFrames("inimigo2.gif"), 150);
            else // final two lines
                shape = new Shape(ImagesLoader.loadAnimationFrames("inimigo1.gif"), 150);

            GameObject enemy = new GameObject("Enemy " + i, t, collider, behavior, shape);
            enemy.onInit();
            enemy.behavior().subscribe(this.player);
            enemy.setSoundEffects(createSoundEffects());
            enemy.soundEffects().addSound("MOVE", AudioLoader.loadAudio("move1.wav"));
            enemys.add(enemy);
        }

    }

    /**
     * Creates and configures sound effects for the game.
     * Adds predefined sound effects for attack and death actions.
     *
     * @return An instance of `ISoundEffects` containing the configured sound
     *         effects.
     */
    private ISoundEffects createSoundEffects() {
        SoundEffects soundEffects = new SoundEffects();
        soundEffects.addSound("ATTACK", AudioLoader.loadAudio("blaster.wav"));
        soundEffects.addSound("DEATH", AudioLoader.loadAudio("explosion.wav"));

        return soundEffects;
    }

    /**
     * Generates the information display for the game, including player lives and
     * score.
     * Synchronizes with the game engine to ensure thread safety during object
     * creation.
     */
    public void generateInfoStat() {
        synchronized (this.engine) {
            // Spawn constants
            int layer = 0; // This layer is only for the info stat
            double raio = 6;

            // Example of life display
            PlayerBehavior playerBehavior = (PlayerBehavior) this.player.behavior();
            int lives = playerBehavior.getLife() - 1; // This should be updated with the actual number of lives
            Ponto position = this.player.transform().position();
            GameObject lifeDisplay = null;

            for (int i = 0; i < lives; i++) {
                Shape shape = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
                Transform transform = new Transform(new Ponto(position.x() + 450 + i * 100, position.y() - 20), layer,
                        90, scale);
                Circulo collider = new Circulo(raio, transform);

                lifeDisplay = new GameObject("Life " + (i + 1), transform, collider, new EnemyBehavior(), shape);
                lifeDisplay.onInit();
                lifeDisplay.behavior().onInit();
                this.engine.add(lifeDisplay);
            }

            // Create and add the score display
            double scale = 60;
            Shape s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
            Transform t1 = new Transform(new Ponto(380, 320.0), layer, 0.0, scale);
            Circulo p1 = new Circulo(0.01, t1);
            GameObject score = new GameObject("Score", t1, p1, new Behavior(), s1);
            this.score = score;
            this.score.onInit();
            engine.addEnable(this.score);
        }
    }

    /**
     * Generates the menu objects for the game, including start game, group name,
     * and skin selection.
     * Adds the created objects to the game engine for rendering and interaction.
     */
    public void generateMenuObjects() {
        ArrayList<IGameObject> objects = new ArrayList<>();

        // Spawn constants
        double scale = 32;
        double raio = 0.0001;
        int layer = 0;

        // Start Game
        Shape s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        Transform t1 = new Transform(new Ponto(0.0, 0.0), layer, 0.0, scale);
        Circulo p1 = new Circulo(raio, t1);
        GameObject startGame = new GameObject("Start Game", t1, p1, new Behavior(), s1);
        startGame.onInit();
        objects.add(startGame);

        // RAMRaiders
        scale = 64;
        s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(0.0, startGame.transform().position().y() + 140), layer, 0.0, scale);
        p1 = new Circulo(raio, t1);
        GameObject nomeGrupo = new GameObject("RAM-Raiders", t1, p1, new Behavior(), s1);
        nomeGrupo.onInit();
        objects.add(nomeGrupo);

        // Group Name
        scale = 15;
        s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(0.0, startGame.transform().position().y() - 370), layer, 0.0, scale);
        p1 = new Circulo(raio, t1);
        GameObject grupo = new GameObject(
                "Gabriel Pedroso                      Brandon Mejia                      Miguel Correia", t1, p1,
                new Behavior(), s1);
        grupo.onInit();
        objects.add(grupo);

        // Skin 1 and 2
        scale = 20;
        s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(-150, startGame.transform().position().y() - 80), layer, 180, scale);
        p1 = new Circulo(raio, t1);
        GameObject skin1 = new GameObject("Press 1", t1, p1, new Behavior(), s1);
        skin1.onInit();
        objects.add(skin1);

        s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(150.0, startGame.transform().position().y() - 80), layer, 180, scale);
        p1 = new Circulo(raio, t1);
        GameObject skin2 = new GameObject("Press 2", t1, p1, new Behavior(), s1);
        skin2.onInit();
        objects.add(skin2);

        // Add ships for skins
        double offset = 90;
        raio = 10;
        s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 150);
        t1 = new Transform(new Ponto(skin1.transform().position().x(), skin1.transform().position().y() - offset),
                layer + 1, 90, this.scale);
        p1 = new Circulo(raio, t1);
        GameObject nave1 = new GameObject("nave", t1, p1, new Behavior(), s1);
        nave1.onInit();
        objects.add(nave1);

        s1 = new Shape(ImagesLoader.loadAnimationFrames("nave-HanSolo.png"), 0);
        t1 = new Transform(new Ponto(skin2.transform().position().x(), skin2.transform().position().y() - offset),
                layer + 1, 90, this.scale);
        p1 = new Circulo(raio - 1, t1);
        GameObject nave2 = new GameObject("nave", t1, p1, new Behavior(), s1);
        nave2.onInit();
        objects.add(nave2);

        // Add all objects to the engine
        for (int i = 0; i < objects.size(); i++)
            this.engine.addEnable(objects.get(i));
    }

    /**
     * Generates the "Game Over" screen objects and displays them.
     * Stops all sounds, destroys all game objects, and sets the menu state to true.
     */
    public void generateGameOver() {
        soundEffects.stopAllSounds();
        this.engine.destroyAll();
        this.engine.getGui().setMenu(true);
        soundEffects.loopSound("GAMEOVER");

        double scale = 64;
        double raio = 0.0001;
        int layer = 0;

        // "Game Over" message
        Shape s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        Transform t1 = new Transform(new Ponto(0.0, 0.0), layer, 0.0, scale);
        Circulo p1 = new Circulo(raio, t1);
        GameObject startGame = new GameObject("GAME OVER", t1, p1, new Behavior(), s1);
        startGame.onInit();

        // Additional message
        scale = 32;
        s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(0.0, -70.0), layer, 0.0, scale);
        p1 = new Circulo(raio, t1);
        GameObject message = new GameObject("Another one falls. The Empire endures.", t1, p1, new Behavior(), s1);
        message.onInit();

        engine.addEnable(startGame);
        engine.addEnable(message);

        this.shutdown();
    }

    /**
     * Generates the "Victory" screen objects and displays them.
     * Stops all sounds, destroys all game objects, and sets the menu state to true.
     */
    public void generateWin() {
        soundEffects.stopAllSounds();
        this.engine.destroyAll();
        this.engine.getGui().setMenu(true);
        this.soundEffects.playSound("WIN");

        double scale = 64;
        double raio = 0.0001;
        int layer = 0;

        // Victory message
        Shape s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        Transform t1 = new Transform(new Ponto(0.0, 0.0), layer, 0.0, scale);
        Circulo p1 = new Circulo(raio, t1);
        GameObject win = new GameObject("Victory is yours.", t1, p1, new Behavior(), s1);
        win.onInit();

        // Additional message
        scale = 32;
        s1 = new Shape(ImagesLoader.loadAnimationFrames("player.gif"), 0);
        t1 = new Transform(new Ponto(0.0, -70.0), layer, 0.0, scale);
        p1 = new Circulo(raio, t1);
        GameObject message = new GameObject("You've brought balance to the galaxy.", t1, p1, new Behavior(), s1);
        message.onInit();

        engine.addEnable(win);
        engine.addEnable(message);
        this.shutdown();
    }

    /**
     * Creates and initializes the player game object.
     * Configures the player's transform, collider, behavior, and sound effects.
     *
     * @param shape The shape of the player game object.
     */
    private void createPlayer(Shape shape) {
        double scale = 4; // Scale of the player object
        int layer = 1; // Layer of the player object
        double angle = 90; // Initial angle of the player object
        Ponto position = new Ponto(0, -330); // Initial position of the player object
        Ponto[] points = { new Ponto(0, 0), new Ponto(0, 12), new Ponto(12, 6) }; // Points for the collider

        Transform t1 = new Transform(position, layer, angle, scale); // Transform for the player
        Poligono collider = new Poligono(points, t1); // Polygon collider for the player

        PlayerBehavior behavior = new PlayerBehavior(); // Behavior of the player
        GameObject player = new GameObject("Player", t1, collider, behavior, shape); // Create the player game object
        player.onInit(); // Initialize the player
        this.player = player; // Set the player as the current player
        this.player.setSoundEffects(createSoundEffects());
        this.player.soundEffects().addSound("HIT", AudioLoader.loadAudio("playerHit.wav"));
    }

}


    