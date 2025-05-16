package core.behaviorItems;

import core.GameObject;
import core.objectsInterface.IGameObject;
import geometry.Ponto;

public class FlyTopDownMovement implements IEnemyMovement {
    private boolean active = false;
    private double t = 0.0;
    private final double scale = 2;
    private boolean goLeftToRight = true;
    private Ponto initialPosition;
    private Ponto target = null;

    private final double tIncrement = 0.0155;

    private final double A1 = 40.0 * scale;
    private final double B1 = 9.0 * scale;
    private final double C1 = 2 * Math.PI;
    private final double D1 = 60.0 * scale;
    private final double t1 = 3 * Math.PI / C1;

    private final double maxT2 = 4.0; // vertical movement duration
    private final double maxT3 = 1.0; // return duration

    public void setTarget(Ponto target) {
        this.target = target;
    }

    private void invariante(IGameObject go) {
        if (go != null)
            return;

        System.out.println("FlyTopDownMovement:iv");
        System.exit(0);
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            t = 0.0;
            initialPosition = null;
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setDirection(boolean goLeftToRight) {
        this.goLeftToRight = goLeftToRight;
    }

    @Override
    public void move(GameObject enemy) {
        invariante(enemy);

        if (!active)
            return;

        if (initialPosition == null)
            initialPosition = enemy.transform().position();

        double angle = enemy.transform().angle();
        if (Math.abs(angle - 270) > 2.0) {
            rotateToVertical(enemy);
            return;
        }

        Ponto current = computePosition(t);
        Ponto next = computePosition(t + tIncrement);
        Ponto velocity = new Ponto(next.x() - current.x(), next.y() - current.y());

        enemy.velocity(velocity);

        t += tIncrement;

        if (t > (t1 + maxT2 + maxT3))
            setActive(false);
    }

    private Ponto computePosition(double localT) {
        if (localT <= t1) {
            return handlePhase1Senoidal(localT);
        } else if (localT <= t1 + maxT2) {
            return handlePhase2Sinusoidal(localT - t1);
        } else {
            return handlePhase3ToTarget(localT - t1 - maxT2);
        }
    }

    private Ponto handlePhase1Senoidal(double t)
    {
        int dir = goLeftToRight ? 1 : -1;
        double x = initialPosition.x() + dir * A1 * t;
        double y = initialPosition.y() - B1 * Math.sin(C1 * t) - D1 * t;
        return new Ponto(x, y);
    }

    private Ponto handlePhase2Sinusoidal(double t2)
    {
        int dir = goLeftToRight ? 1 : -1;

        double baseX = initialPosition.x() + dir * A1 * t1;
        double baseY = initialPosition.y() - B1 * Math.sin(C1 * t1) - D1 * t1;

        double a2 = 45.0 * scale;
        double x = baseX + dir * a2 * t2;
        double b2 = 1.5 * scale;
        double c2 = 5 * Math.PI;
        double d2 = 110 * scale;
        double y = baseY - b2 * Math.sin(c2 * t2) - d2 * t2;

        return new Ponto(x, y);
    }

    private Ponto handlePhase3ToTarget(double t3) {
        if (target == null)
            return handlePhase2Sinusoidal(maxT2); // fallback to last position

        // Start point is end of phase 2
        Ponto start = handlePhase2Sinusoidal(maxT2);

        // Linear interpolation
        double alpha = Math.min(t3 / maxT3, 1.0);
        double x = start.x() + (target.x() - start.x()) * alpha;
        double y = start.y() + (target.y() - start.y()) * alpha;

        return new Ponto(x, y);
    }

    private void rotateToVertical(GameObject enemy) {
        double currentAngle = enemy.transform().angle();
        double targetAngle = 270.0;

        double angleDiff = (targetAngle + 360) - currentAngle;
        while (angleDiff > 180) angleDiff -= 360;
        while (angleDiff < -180) angleDiff += 360;

        double rotationSpeed = angleDiff * 0.09;
        if (Math.abs(rotationSpeed) > 5.0) {
            rotationSpeed = Math.signum(rotationSpeed) * 5.0;
        }

        enemy.rotateSpeed(rotationSpeed);
    }
}