package test;

import core.*;
import core.behaviorItems.HomingShootAttack;
import core.objectsInterface.IGameObject;
import geometry.Poligono;
import geometry.Ponto;
import geometry.Retangulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestHomingShootAttack
{
    private final Ponto[] pts = {new Ponto(2.0, 4), new Ponto(2.0, 0), new Ponto(0.0, 0.0), new Ponto(0.0, 4.0)};
    private GameObject attacker;
    private GameObject target;
    private HomingShootAttack homingAttack;

    @BeforeEach
    void setUp()
    {
        Transform enemyTransform = new Transform(new Ponto(7.0, 7.0), 0, 270, 1);
        Transform playerTransform = new Transform(new Ponto(5.0, 0.0), 0, 90, 1);

        Poligono polygon0 = new Retangulo(pts, enemyTransform);
        Poligono polygon1 = new Retangulo(pts, playerTransform);

        Shape shape = new Shape();
        Behavior playerBehavior = new PlayerBehavior();
        Behavior enemyBehavior = new EnemyBehavior();

        attacker = new GameObject("ENEMY", enemyTransform, polygon0, enemyBehavior, shape);
        target = new GameObject("PLAYER", playerTransform, polygon1, playerBehavior, shape);

        homingAttack = new HomingShootAttack();

            attacker.behavior().subscribe(target);

            attacker.onInit();
        target.onInit();
    }

    @Test
    void testHomingBulletCreationAndCollision()
    {
        IGameObject bullet = homingAttack.execute(attacker, target);
        assertNotNull(bullet, "Bullet should not be null after attack execution");
        assertTrue(bullet.name().startsWith("HOMING_BULLET"), "Bullet name should start with HOMING_BULLET");
    }



    @Test
    void testBulletAngleAndCollisionFromDifferentPosition()
    {
        IGameObject bullet = homingAttack.execute(attacker, target);
        for (int i = 0; i < 4 ; i++)
            bullet.onUpdate();


        assertEquals(254,Math.round(bullet.transform().angle()));
        assertTrue(target.collider().colision(bullet.collider()));

            target.transform().move(new Ponto(20,0),0);
            target.collider().updatePosicao();

            attacker.transform().move(new Ponto(5,-6),0); // x + 13y = 25  -18.43
            attacker.transform().rotate(-270);
            attacker.collider().onUpdateCollider();

        bullet = homingAttack.execute(attacker, target);
            for (int i = 0; i < 9; i++)
                bullet.onUpdate();

        assertEquals(356,Math.round(bullet.transform().angle()));
        assertTrue(target.collider().colision(bullet.collider()));
    }

    @Test
    void testInvarianteNullChecks()
    {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        assertThrows(IllegalArgumentException.class, () -> homingAttack.execute(null, target),
                "Should throw exception for null attacker");
        assertEquals("HomingShootAttack:vi", outContent.toString().trim());

        assertThrows(IllegalArgumentException.class, () -> homingAttack.execute(attacker, null),
                "Should throw exception for null target");

        assertThrows(IllegalArgumentException.class, () -> homingAttack.execute(attacker, attacker),
                "Should throw exception for equals attacker and target");
    }

    @Test
    void testConsecutiveBullets()
    {
        IGameObject bullet1 = homingAttack.execute(attacker, target);
        IGameObject bullet2 = homingAttack.execute(attacker, target);

        assertNotEquals(bullet1.name(), bullet2.name(), "Consecutive bullets should have different names");
    }
}