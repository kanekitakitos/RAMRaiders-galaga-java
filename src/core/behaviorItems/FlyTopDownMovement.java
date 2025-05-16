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
    private final double D1 = 150.0 * scale;
    private final double t1 = 3 * Math.PI / C1;

    private final double maxT2 = 3.5;
    private final double maxT3a = 0.1; // to waypoint1
    private final double maxT3b = 0.1; // to waypoint2
    private final double maxT3c = 0.1; 
    private final double maxT3d = 2; 
    private final double t0Max = 0.5;
    private double DescendStraight = 2;

    private Ponto waypoint1 = null;
    private Ponto waypoint2 = null;
    private Ponto waypoint3 = null;

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
            waypoint1 = null;
            waypoint2 = null;
            waypoint3 = null;
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

        double totalT = t0Max + t1 + maxT2 + maxT3a + maxT3b + maxT3c + maxT3d;
        if (t > totalT) {
            enemy.velocity(new Ponto(0, 0));
            enemy.rotateSpeed(0);
            DescendStraight = 100;
            setActive(false);
        }
    }

    private Ponto computePosition(double localT) {
        if (localT <= t0Max) {
            return handlePhase0Approach(localT);
        } else if (localT <= t0Max + t1) {
            return handlePhase1Senoidal(localT - t0Max);
        } else if (localT <= t0Max + t1 + maxT2) {
            return handlePhase2Sinusoidal(localT - t0Max - t1);
        } else {
            return handlePhase3ToTarget(localT - t0Max - t1 - maxT2);
        }
    }

    private Ponto handlePhase0Approach(double t) {
        double alpha = t / t0Max;
        double x = initialPosition.x();
        double y = initialPosition.y() + (-DescendStraight - initialPosition.y()) * alpha;
        return new Ponto(x, y);
    }

    private Ponto handlePhase1Senoidal(double t) {
        int dir = goLeftToRight ? 1 : -1;
        double x = initialPosition.x() + dir * A1 * t;
        double y = -DescendStraight - B1 * Math.sin(C1 * t) - D1 * t;
        return new Ponto(x, y);
    }

    private Ponto handlePhase2Sinusoidal(double t2) {
        int dir = goLeftToRight ? 1 : -1;

        double baseX = initialPosition.x() + dir * A1 * t1;
        double baseY = -DescendStraight - B1 * Math.sin(C1 * t1) - D1 * t1;

        double a2 = 45.0 * scale;
        double x = baseX + dir * a2 * t2;
        double b2 = 1.5 * scale;
        double c2 = 5 * Math.PI;
        double d2 = 110 * scale;
        double y = baseY - b2 * Math.sin(c2 * t2) - d2 * t2;

        return new Ponto(x, y);
    }

    private Ponto handlePhase3ToTarget(double t3) {
        if (initialPosition == null || target == null)
            return handlePhase2Sinusoidal(maxT2);

        Ponto start = handlePhase2Sinusoidal(maxT2);

        // Lazy initialization of waypoints
        if (waypoint1 == null)
        {
            double deltaY = Math.abs(start.y() - target.y());
            double signX = (target.x() - start.x()) >= 0 ? -1 : 1;

            // First move diagonally (Δx = Δy)
            waypoint1 = new Ponto(start.x() + signX * deltaY, start.y() + deltaY);

            // Then go vertically above the target
            waypoint2 = new Ponto(start.x(), target.y() - 100);

            // Then go horizontally to align with target x
            waypoint3 = new Ponto(target.x(), target.y() - 50);
        }

        if (t3 <= maxT3a) {
            return interpolate(start, waypoint1, t3 / maxT3a);
        } else if (t3 <= maxT3a + maxT3b) {
            double localT = t3 - maxT3a;
            return interpolate(waypoint1, waypoint2, localT / maxT3b);
        } else if (t3 <= maxT3a + maxT3b + maxT3c) {
            double localT = t3 - maxT3a - maxT3b;
            return interpolate(waypoint2, waypoint3, localT / maxT3c);
        } else {
            double localT = t3 - maxT3a - maxT3b - maxT3c;
            return interpolate(waypoint3, target, localT / maxT3d);
        }
    }

    private Ponto interpolate(Ponto a, Ponto b, double alpha) {
        alpha = Math.max(0.0, Math.min(1.0, alpha));
        double x = a.x() + (b.x() - a.x()) * alpha;
        double y = a.y() + (b.y() - a.y()) * alpha;
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
