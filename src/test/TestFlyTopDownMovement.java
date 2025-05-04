package test;

import core.*;
import core.behaviorItems.FlyTopDownMovement;
import geometry.Poligono;
import geometry.Ponto;
import geometry.Retangulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestFlyTopDownMovement
{
    private FlyTopDownMovement movement;
    private GameObject enemy;

    @BeforeEach
    void setUp() {
        Ponto[] pts = {new Ponto(2.0, 4), new Ponto(2.0, 0),
                new Ponto(0.0, 0.0), new Ponto(0.0, 4.0)};
        Transform transform = new Transform(new Ponto(1.0, 2), 1, 90, 1);
        Poligono polygon = new Retangulo(pts, transform);
        movement = new FlyTopDownMovement();

        enemy = new GameObject(
                "TestEnemy",
                transform,
                polygon,
                new EnemyBehavior(),
                new Shape()
        );

        enemy.onInit();
    }

    @Test
    void testInitialState()
    {
        assertFalse(movement.isActive(), "Movement should be inactive initially");
    }

    @Test
    void testActivation() {
        movement.setActive(true);
        assertTrue(movement.isActive(), "Movement should be active after activation");

        movement.setActive(false);
        assertFalse(movement.isActive(), "Movement should be inactive after deactivation");
    }

    @Test
    void testMovementLeftToRight()
    {
        movement.setActive(true);
        movement.setDirection(true);
        Ponto initialPos = enemy.transform().position();

        movement.move(enemy);
        enemy.onUpdate();

        Ponto newPos = enemy.transform().position();
        assertTrue(newPos.x() > initialPos.x(), "Should move right");
        assertTrue(newPos.y() < initialPos.y(), "Should move down");
    }

    @Test
    void testMovementRightToLeft()
    {
        movement.setActive(true);
        movement.setDirection(false);
        Ponto initialPos = enemy.transform().position();

        movement.move(enemy);
        enemy.onUpdate();

        Ponto newPos = enemy.transform().position();
        assertTrue(newPos.x() < initialPos.x(), "Should move left");
        assertTrue(newPos.y() < initialPos.y(), "Should move down");
    }

    @Test
    void testDeactivationAfterCompletion()
    {
        movement.setActive(true);

        // Run movement for more than 2 time units
        for(int i = 0; i < 150; i++)
        {
            movement.move(enemy);
            enemy.onUpdate();
        }

        assertFalse(movement.isActive(), "Movement should deactivate after completion");
    }
}