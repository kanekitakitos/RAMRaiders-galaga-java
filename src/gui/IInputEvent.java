package gui;
import javax.swing.JFrame;


public interface IInputEvent
{
    boolean isActionActive(String action);

    void registerInputHandlers(JFrame frame);
}
