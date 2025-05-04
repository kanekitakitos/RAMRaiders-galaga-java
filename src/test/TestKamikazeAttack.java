package test;

import core.*;
import core.behaviorItems.KamikazeAttack;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import geometry.Retangulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestKamikazeAttack
{
    private GameObject attacker;
    private GameObject target;
    private KamikazeAttack kamikazeAttack;
    private Ponto[] points = {
            new Ponto(2.0, 4.0),
            new Ponto(2.0, 0.0),
            new Ponto(0.0, 0.0),
            new Ponto(0.0, 4.0)
    };

    @BeforeEach
    void setUp()
    {
        // Setup attacker
        Transform attackerTransform = new Transform(new Ponto(10.0, 10.0), 0, 0, 1);
        Retangulo attackerRect = new Retangulo(points, attackerTransform);
        attacker = new GameObject(
                "TEST_ATTACKER",
                attackerTransform,
                attackerRect,
                new EnemyBehavior(),
                new Shape()
        );

        // Setup target
        Transform targetTransform = new Transform(new Ponto(0.0, 0.0), 0, 0, 1);
        Retangulo targetRect = new Retangulo(points, targetTransform);
        target = new GameObject(
                "TEST_TARGET",
                targetTransform,
                targetRect,
                new PlayerBehavior(),
                new Shape()
        );

        kamikazeAttack = new KamikazeAttack();
        target.onInit();
        attacker.onInit();
    }

    @Test
    void testValidKamikazeAttack()
    {
        IGameObject result = kamikazeAttack.execute(attacker, target);
        assertNotNull(result, "Attack result should not be null");
        assertEquals(attacker, result, "Should return the modified attacker");

        Ponto velocity = ((GameObject)result).velocity();
        assertNotNull(velocity, "Velocity should be set");
        assertEquals(1.0, Math.sqrt(velocity.x() * velocity.x() + velocity.y() * velocity.y()), 0.001,
                "Velocity magnitude should be 1.0");

        assertEquals(225,attacker.transform().angle());
    }

    @Test
    void testInvalidAttackerAndTarget()
    {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Test null attacker
        assertThrows(IllegalArgumentException.class,
                () -> kamikazeAttack.execute(null, target));
        assertEquals("KamikazeAttack:vi", outContent.toString().trim());

        // Test null target
        assertThrows(IllegalArgumentException.class,
                () -> kamikazeAttack.execute(attacker, null));

        // Test same object for attacker and target
        assertThrows(IllegalArgumentException.class,
                () -> kamikazeAttack.execute(attacker, attacker));
    }

    @Test
    void testMovementDirection()
    {
        IGameObject result = kamikazeAttack.execute(attacker, target);
        Ponto velocity = ((GameObject)result).velocity();

        // Calculate expected angle to target
        double dx = target.transform().position().x() - attacker.transform().position().x();
        double dy = target.transform().position().y() - attacker.transform().position().y();
        double expectedAngle = Math.atan2(dy, dx);

        // Calculate actual angle from velocity
        double actualAngle = Math.atan2(velocity.y(), velocity.x());

        assertEquals(expectedAngle, actualAngle, 0.001,
                "Velocity direction should match angle to target");
    }


    @Test
    void testAttackAngleAndCollisionFromDifferentPosition()
    {
        Transform attackerTransform = new Transform(new Ponto(4.0, 1.0), 0,180 , 1);
        Retangulo attackerRect = new Retangulo(new Ponto[] {
                new Ponto(4.0, 2.0),
                new Ponto(4.0, 0.0),
                new Ponto(0.0, 0.0),
                new Ponto(0.0, 2.0)
        }, attackerTransform);
            attacker = new GameObject("TEST_ATTACKER", attackerTransform, attackerRect, new EnemyBehavior(), new Shape());
            attacker.onInit();

        Transform targetTransform = new Transform(new Ponto(20.0, 2.0), 0, 0, 1);
        Retangulo targetRect = new Retangulo(points, targetTransform);
            target = new GameObject("TEST_TARGET", targetTransform, targetRect, new PlayerBehavior(), new Shape());
            target.onInit();

            double angle = attacker.transform().angle();
            kamikazeAttack.execute(attacker, target);

        assertNotEquals(angle, target.transform().angle(), 0.0, "Angle should be different");
        for (int i = 0; i < 14; i++)
            attacker.onUpdate();
        assertTrue(attacker.collider().colision(target.collider()));
    }


    @Test
    void testAttackFromSameAngle()
    {
        Transform attackerTransform = new Transform(new Ponto(0.0, 6.0), 0,270 , 1);
        Retangulo attackerRect = new Retangulo(new Ponto[] {
                new Ponto(4.0, 2.0),
                new Ponto(4.0, 0.0),
                new Ponto(0.0, 0.0),
                new Ponto(0.0, 2.0)
        }, attackerTransform);
        attacker = new GameObject("TEST_ATTACKER", attackerTransform, attackerRect, new EnemyBehavior(), new Shape());
        attacker.onInit();

        Transform targetTransform = new Transform(new Ponto(0.0, 0.0), 0, 90, 1);
        Retangulo targetRect = new Retangulo(points, targetTransform);
        target = new GameObject("TEST_TARGET", targetTransform, targetRect, new PlayerBehavior(), new Shape());
        target.onInit();

        kamikazeAttack.execute(attacker, target);
        for (int i = 0; i < 3; i++)
            attacker.onUpdate();

        assertTrue(attacker.collider().colision(target.collider()));
        assertEquals(270, attacker.transform().angle());





    }

}