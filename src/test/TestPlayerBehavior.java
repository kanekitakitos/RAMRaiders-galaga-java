package test;

import core.*;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import geometry.Retangulo;
import gui.InputEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayerBehavior {
    private PlayerBehavior playerBehavior;
    private GameObject gameObject;
    private InputEvent inputEvent;

    @BeforeEach
    void setUp() {
        // Set up input mappings
        Map<Integer, String> keyMap = new HashMap<>();
        keyMap.put(KeyEvent.VK_RIGHT, "RIGHT");
        keyMap.put(KeyEvent.VK_LEFT, "LEFT");
        keyMap.put(KeyEvent.VK_SPACE, "EVASIVE");
        keyMap.put(KeyEvent.VK_Z, "ATTACK");

        Map<Integer, String> mouseMap = new HashMap<>();
        mouseMap.put(MouseEvent.BUTTON1, "ATTACK");

        inputEvent = new InputEvent(keyMap, mouseMap);
        playerBehavior = new PlayerBehavior();

        // Create game object
        Ponto[] points = {new Ponto(2.0, 4), new Ponto(2.0, 0), new Ponto(0.0, 0.0), new Ponto(0.0, 4.0)};
        Transform transform = new Transform(new Ponto(0, 0), 1, 90, 1);
        Retangulo polygon = new Retangulo(points, transform);

        gameObject = new GameObject("PLAYER", transform, polygon, playerBehavior, new Shape());
        gameObject.onInit();
    }

    @Test
    void onUpdate_movesPlayerRight() {
        inputEvent.keyPressed(new KeyEvent(new java.awt.Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_RIGHT, 'D'));
        Ponto initialPosition = gameObject.transform().position();

        playerBehavior.onUpdate(inputEvent);

        Ponto newPosition = gameObject.transform().position();
        assertTrue(newPosition.x() > initialPosition.x());
        assertEquals(initialPosition.y(), newPosition.y());
    }

    @Test
    void onUpdate_movesPlayerLeft() {
        inputEvent.keyPressed(new KeyEvent(new java.awt.Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_LEFT, 'A'));
        Ponto initialPosition = gameObject.transform().position();

        playerBehavior.onUpdate(inputEvent);

        Ponto newPosition = gameObject.transform().position();
        assertTrue(newPosition.x() < initialPosition.x());
        assertEquals(initialPosition.y(), newPosition.y());
    }

    @Test
    void onCollision_reducesLife() {
        ArrayList<IGameObject> collisions = new ArrayList<>();
        Transform enemyTransform = new Transform(new Ponto(0, 0), 1, 0, 1);
        Retangulo enemyCollider = new Retangulo(new Ponto[]{new Ponto(2.0, 4), new Ponto(2.0, 0),
                new Ponto(0.0, 0.0), new Ponto(0.0, 4.0)}, enemyTransform);
        GameObject enemy = new GameObject("ENEMY", enemyTransform, enemyCollider, new Behavior(), new Shape());
        collisions.add(enemy);

        int initialLife = playerBehavior.getLife();
        playerBehavior.onCollision(collisions);

        assertEquals(initialLife - 1, playerBehavior.getLife());
    }

    @Test
    void attack_createsProjectile() {
        inputEvent.keyPressed(new KeyEvent(new java.awt.Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_Z, 'Z'));

        IGameObject projectile = playerBehavior.attack(inputEvent);

        assertNotNull(projectile);
        assertTrue(projectile.name().startsWith("LINEAR_BULLET"));
    }

    @Test
    void evasiveManeuver_makesPlayerInvincible() {
        inputEvent.keyPressed(new KeyEvent(new java.awt.Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_SPACE, ' '));

        playerBehavior.evasiveManeuver(inputEvent);

        assertTrue(playerBehavior.isInvincible());
    }

    @Test
    void onCollision_doesNotReduceLifeWhenInvincible()
    {
        ArrayList<IGameObject> collisions = new ArrayList<>();
        Transform enemyTransform = new Transform(new Ponto(0, 0), 1, 0, 1);
        Retangulo enemyCollider = new Retangulo(new Ponto[]{}, enemyTransform);
        GameObject enemy = new GameObject("ENEMY", enemyTransform, enemyCollider, new Behavior(), new Shape());
        collisions.add(enemy);

        // Make player invincible
        inputEvent.keyPressed(new KeyEvent(new java.awt.Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_SPACE, ' '));
        playerBehavior.evasiveManeuver(inputEvent);

        int initialLife = playerBehavior.getLife();
        playerBehavior.onCollision(collisions);

        assertEquals(initialLife, playerBehavior.getLife());
    }

    @Test
    void noMovement_whenNoInput() {
        Ponto initialPosition = gameObject.transform().position();

        playerBehavior.onUpdate(inputEvent);

        Ponto newPosition = gameObject.transform().position();
        assertEquals(initialPosition.x(), newPosition.x());
        assertEquals(initialPosition.y(), newPosition.y());
    }
}