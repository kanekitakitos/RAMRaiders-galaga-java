package gui;

import core.objectsInterface.IGameObject;
import java.util.List;

public interface IGuiBridge
{
    void draw(List<IGameObject> objects);
    InputEvent getInputState(); // Pode ser expandido conforme necessidade
}