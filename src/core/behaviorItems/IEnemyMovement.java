package core.behaviorItems;

import core.GameObject;

public interface IEnemyMovement
{

    void move(GameObject enemy);

    void setActive(boolean active);

    boolean isActive();

}
