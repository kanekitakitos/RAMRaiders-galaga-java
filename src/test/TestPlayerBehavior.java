package test;
import core.*;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import geometry.Poligono;
import geometry.Retangulo;
import gui.InputEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayerBehavior
{
    private PlayerBehavior playerBehavior0;
    private GameObject gameObject0;

    private PlayerBehavior playerBehavior1;
    private GameObject gameObject1;

    private InputEvent inputEvent;

    @BeforeEach
    void setUp()
    {
        playerBehavior0 = new PlayerBehavior();

        Ponto[] pts = {new Ponto(2.0, 4), new Ponto(2.0, 0), new Ponto(0.0, 0.0), new Ponto(0.0, 4.0)};
        Transform transform = new Transform(new Ponto(1.0, 2), 0, 0, 1);
        Poligono polygon = new Retangulo(pts, transform);

        gameObject0 = new GameObject("PLAYER 0", transform, polygon, playerBehavior0, new Shape());
        gameObject0.onInit();


        playerBehavior1 = new PlayerBehavior();
        gameObject1 = new GameObject("PLAYER 1", transform, polygon, playerBehavior1, new Shape());
        gameObject1.onInit();

        inputEvent = new InputEvent();
    }

    @Test
    void attackNotCreatesProjectileWhenNotAttacking()
    {
        IGameObject result = playerBehavior0.attack(inputEvent);
        assertNull(result);
    }

    @Test
    void attackReturnsNullWhenAlreadyAttacking()
    {
        inputEvent.setPlayerMove("MOUSE_LEFT", true);
        IGameObject firstAttack = playerBehavior0.attack(inputEvent);
        assertNotNull(firstAttack);

        IGameObject secondAttack = playerBehavior0.attack(inputEvent);
        assertNull(secondAttack);
    }

    @Test
    void onCollisionReducesLifeByOneWhenHit()
    {
        ArrayList<IGameObject> collisions = new ArrayList<>();
        collisions.add(gameObject1);

        playerBehavior0.onCollision(collisions);
        assertEquals(2, playerBehavior0.life());
    }

    @Test
    void onCollisionMakesPlayerInvincibleTemporarily() throws InterruptedException
    {
        ArrayList<IGameObject> collisions = new ArrayList<>();
        collisions.add(gameObject1);

        playerBehavior0.onCollision(collisions);
        assertTrue(playerBehavior0.isInvincible());

        Thread.sleep(1500);
        assertFalse(playerBehavior0.isInvincible());
    }

    @Test
    void playerRemainsAliveWhenHitWithInvincibility() throws InterruptedException
    {
        ArrayList<IGameObject> collisions = new ArrayList<>();
        collisions.add(gameObject1);


        playerBehavior0.onCollision(collisions); // hit
        playerBehavior0.onCollision(collisions);

        Thread.sleep(1300);
        assertEquals(2, playerBehavior0.life());
        playerBehavior0.onCollision(collisions); // hit
        assertEquals(1, playerBehavior0.life());
        assertTrue(playerBehavior0.isEnabled());
    }

    @Test
    void playerIsDestroyedWhenLifeReachesZero() throws InterruptedException
    {
        ArrayList<IGameObject> collisions = new ArrayList<>();
        collisions.add(gameObject1);


        assertEquals(3, playerBehavior0.life());
        playerBehavior0.onCollision(collisions); // hit
        Thread.sleep(1300);
        assertEquals(2, playerBehavior0.life());
        playerBehavior0.onCollision(collisions);// hit
        Thread.sleep(1300);
        assertEquals(1, playerBehavior0.life());
        playerBehavior0.onCollision(collisions); // hit

        assertEquals(0, playerBehavior0.life());
        assertFalse(playerBehavior0.isEnabled());


    }

    @Test
    void evasiveManeuverMovesPlayerRight()
    {
        inputEvent.setPlayerMove("RIGHT", true);
        Ponto initialPosition = gameObject0.transform().position();

        playerBehavior0.moveAndEvasiveManeuver(inputEvent);

        Ponto newPosition = gameObject0.transform().position();
        assertTrue(newPosition.x() > initialPosition.x());
        assertEquals(initialPosition.y(), newPosition.y());
    }

    @Test
    void evasiveManeuverMovesPlayerLeft() throws InterruptedException
    {
        inputEvent.setPlayerMove("LEFT", true);
        Ponto initialPosition = gameObject0.transform().position();

        playerBehavior0.moveAndEvasiveManeuver(inputEvent);
        assertTrue(playerBehavior0.isInvincible());

        Ponto newPosition = gameObject0.transform().position();
        assertTrue(newPosition.x() < initialPosition.x());
        assertEquals(initialPosition.y(), newPosition.y());

        Thread.sleep(1200);
        assertFalse(playerBehavior0.isInvincible());
    }

}