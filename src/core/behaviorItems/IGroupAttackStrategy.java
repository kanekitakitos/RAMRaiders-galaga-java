package core.behaviorItems;

import java.util.List;
import core.objectsInterface.IGameObject;

public interface IGroupAttackStrategy
{
    void onInit(List<IGameObject> enemies, IGameObject target);

    void execute(List<IGameObject> enemies, IGameObject target);

    int getNumberOfEnemies();

}