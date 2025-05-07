package gui;

import core.objectsInterface.IGameObject;
import java.util.concurrent.CopyOnWriteArrayList;

public interface IGuiBridge
{
    void draw(CopyOnWriteArrayList<IGameObject> objects);

    InputEvent getInputState(); // Pode ser expandido conforme necessidade
}