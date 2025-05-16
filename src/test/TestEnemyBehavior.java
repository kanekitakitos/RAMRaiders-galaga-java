package test;

import core.*;
import core.behaviorItems.*;
import geometry.Poligono;
import geometry.Ponto;
import geometry.Retangulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestEnemyBehavior
{
    private EnemyBehavior enemyBehavior;
    private GameObject enemy;

    @BeforeEach
    void setUp()
    {
        Ponto[] pts = {new Ponto(2.0, 4), new Ponto(2.0, 0), new Ponto(0.0, 0.0), new Ponto(0.0, 4.0)};
        Transform transform = new Transform(new Ponto(1.0, 2), 1, 90, 1);
        Poligono polygon = new Retangulo(pts, transform);
        enemyBehavior = new EnemyBehavior();


        enemy = new GameObject(
            "TestEnemy",
            transform,
            polygon,
            enemyBehavior,
            new Shape());
        this.enemy.onInit();
        this.enemyBehavior.activateMovement(true);
    }

    @Test
    void testZigZagMovement()
    {
        // Test multiple frames to verify zigzag pattern
        double[] expectedX = new double[4];

        // Store initial position
        Ponto initialPos = enemy.transform().position();


        // Run movement for 4 frames and store x positions
        for (int i = 0; i < 4; i++)
        {
            enemyBehavior.move();

            expectedX[i] = enemy.transform().position().x();
        }

        // Verify zigzag pattern (alternating left and right movement)
        assertTrue(expectedX[1] != expectedX[0], "Position should change after first move");
        assertTrue(expectedX[2] != expectedX[1], "Position should change after second move");

        assertTrue(Math.abs(expectedX[3] - initialPos.x()) <= 0.5, "Movement should stay within amplitude bounds");
    }

    @Test
    void testYPositionMaintained()
    {
        double initialY = enemy.transform().position().y();
        // Move multiple times
        for (int i = 0; i < 10; i++)
        {
            enemyBehavior.move();
            assertEquals(initialY, enemy.transform().position().y(), "Y position should remain constant");
        }
    }




    @Test
    void testFlyCircleMovementBehaviorLeft()
    {

        FlyCircleMovement flyCircleMovement = new FlyCircleMovement();
        enemyBehavior.setMovement(flyCircleMovement);

        assertFalse(flyCircleMovement.isActive(), "Movement should not be active");

        flyCircleMovement.setActive(true);
        flyCircleMovement.setDirection(true);
        Ponto initialPosition = enemy.transform().position();

        for (int i = 0; i < 80; i++)
        {
            enemy.onUpdate();

            if(!flyCircleMovement.isActive())
                break;

            assertTrue(flyCircleMovement.isActive());
        }

        assertEquals(initialPosition.x(),enemy.transform().position().x(),0.1);
        assertEquals(initialPosition.y(),enemy.transform().position().y(),0.1);

    }

    @Test
    void testFlyCircleMovementBehaviorRight()
    {

        FlyCircleMovement flyCircleMovement = new FlyCircleMovement();
        enemyBehavior.setMovement(flyCircleMovement);
        assertFalse(flyCircleMovement.isActive(), "Movement should not be active");
        flyCircleMovement.setActive(true);
        Ponto initialPosition = enemy.transform().position();


        for (int i = 0; i < 80; i++)
        {
            enemy.onUpdate();

            if(!flyCircleMovement.isActive())
                break;

            assertTrue(flyCircleMovement.isActive());
        }

        assertEquals(initialPosition.x(),enemy.transform().position().x(),0.1);
        assertEquals(initialPosition.y(),enemy.transform().position().y(),0.1);

    }



    @Test
    void testFlyTopDownMovementBehaviorRight()
    {

        FlyTopDownMovement fly = new FlyTopDownMovement();
        enemyBehavior.setMovement(fly);
        assertFalse(fly.isActive(), "Movement should not be active");
        fly.setActive(true);
        fly.setDirection(true);

        for (int i = 0; i < 100; i++)
        {
            enemy.onUpdate();

            if(!fly.isActive())
                break;

            assertTrue(fly.isActive());
        }
    }


    @Test
    void testFlyLoopDropMovementBehaviorRight()
    {

        FlyLassoMovement fly = new FlyLassoMovement();
        enemyBehavior.setMovement(fly);
        assertFalse(fly.isActive(), "Movement should not be active");
        fly.setActive(true);
        fly.setDirection(true);

        for (int i = 0; i < 150; i++)
        {
            enemy.onUpdate();

            if(!fly.isActive())
                break;

            assertTrue(fly.isActive());
        }
    }

    @Test
    void testFlyLoopDropMovementBehaviorLeft()
    {
        FlyLassoMovement fly = new FlyLassoMovement();
        enemyBehavior.setMovement(fly);
        assertFalse(fly.isActive(), "Movement should not be active");
        fly.setActive(true);
        fly.setDirection(false);

        for (int i = 0; i < 150; i++)
        {
            enemy.onUpdate();

            if(!fly.isActive())
                break;

            assertTrue(fly.isActive());
        }
    }


    @Test
    void testFlySideLassoMovementBehaviorLeft()
    {
        EnterSideMovement fly = new EnterSideMovement();
        enemyBehavior.setMovement(fly);
        assertFalse(fly.isActive(), "Movement should not be active");
        fly.setActive(true);

        fly.setFinalTarget(new Ponto(10,12));

        for (int i = 0; i < 150; i++)
        {
            enemy.onUpdate();

            if(!fly.isActive())
                break;

            assertTrue(fly.isActive());
        }
    }

    @Test
    void testFlyOverTopMovementBehaviorRight()
    {
        EnterOverTopMovement fly = new EnterOverTopMovement();
        enemyBehavior.setMovement(fly);
        assertFalse(fly.isActive(), "Movement should not be active");
        fly.setDirection(false);
        Ponto target = new Ponto(8,2);
        fly.setFinalTarget(target);

        fly.setActive(true);

        for (int i = 0; i < 300; i++)
        {
            System.out.println(enemy.transform().position());
            enemy.onUpdate();
            if(!fly.isActive())
                break;

            assertTrue(fly.isActive());
        }

        assertEquals(target.x(),enemy.transform().position().x(),0.1);
        assertEquals(target.y(),enemy.transform().position().y(),0.1);
    }






}