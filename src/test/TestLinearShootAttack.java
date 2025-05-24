package test;

import core.*;
import core.behaviorItems.LinearShootAttack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import geometry.Ponto;
import geometry.Retangulo;
import gui.InputEvent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestLinearShootAttack {
    private GameObject attacker;
    private LinearShootAttack shootAttack;
    private InputEvent inputEvent;

    @BeforeEach
    void setUp() {
        Ponto[] points = { new Ponto(2.0, 4), new Ponto(2.0, 0), new Ponto(0.0, 0.0), new Ponto(0.0, 4.0) };
        Transform transform = new Transform(new Ponto(1.0, 2), 0, 0, 1);
        Retangulo rectangle = new Retangulo(points, transform);

        attacker = new GameObject(
                "TEST_ATTACKER",
                transform,
                rectangle,
                new Behavior(),
                new Shape());

        shootAttack = new LinearShootAttack();

    }

    @Test
    void testShootBullet() {
        GameObject bullet = (GameObject) shootAttack.execute(attacker, null);
        GameObject bullet2 = (GameObject) shootAttack.execute(attacker, null);

        assertNotNull(bullet, "Bullet should not be null after attack execution");
        assertEquals("LINEAR_BULLET 0", bullet.name());
        assertEquals("LINEAR_BULLET 1", bullet2.name());
        assertEquals(1.0, bullet.transform().position().x(), "Bullet x position should be 1.0");

        // Test bullet movement
        Ponto initialPos = bullet.transform().position();
        bullet.onUpdate();
        assertNotEquals(initialPos, bullet.transform().position(), "Bullet should move after update");
    }

    @Test
    void testInvalidAttacker() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        assertThrows(IllegalArgumentException.class, () -> shootAttack.execute(null, null));
        assertEquals("LinearShootAttack:vi", outContent.toString().trim());

        assertThrows(IllegalArgumentException.class, () -> shootAttack.execute(null, attacker));

    }
}