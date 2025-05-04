package core.behaviorItems;

import core.GameObject;
import geometry.Ponto;

public class ZigzagMovement implements IEnemyMovement
{
    private boolean isActive = false;
    private double time = 0;

    @Override
    public void move(GameObject enemy)
    {
        if (!isActive) return;

        time += 45;
        double amplitude = 0.5;
        double dx = (time <= 90 ? amplitude : amplitude * -1);
        time = time % 180;
        enemy.velocity(new Ponto(dx, 0));
    }

    @Override
    public void setActive(boolean active)
    {
        this.isActive = active;
        if (active)
            time = 0;
    }

    @Override
    public boolean isActive()
    {
        return isActive;
    }
}
