package core.behaviorItems;

import java.util.List;
import core.objectsInterface.IGameObject;

public interface IGroupAttackStrategy
{
    double SPACING = 0.5; // Smaller space between enemies

    void onInit(List<IGameObject> enemies, IGameObject target);

    void execute(List<IGameObject> enemies, IGameObject target);


}