package gui;

import core.objectsInterface.IGameObject;
import java.util.concurrent.CopyOnWriteArrayList;

public interface IGuiBridge
{
    void draw(CopyOnWriteArrayList<IGameObject> objects);

    IInputEvent getInputState();
}