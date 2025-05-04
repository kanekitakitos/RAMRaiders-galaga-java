package test;

import javax.swing.*;

import gui.InputEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestInputPlayer extends JFrame
{
        public static void main(String[] args)
        {
            JFrame frame = new JFrame("Keyboard Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);

            InputEvent inputEvent = new InputEvent();
            inputEvent.registerInputHandlers(frame);
            frame.setVisible(true);

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() ->
            {
                if (inputEvent.isRight()) System.out.println("Right pressed");
                if (inputEvent.isLeft()) System.out.println("Left pressed");
                if (inputEvent.isAttack()) System.out.println("Attack Button pressed");
                if (inputEvent.isEvasiveManeuver()) System.out.println("Evasive Mouse Button pressed");
            }, 0, 100, TimeUnit.MILLISECONDS);

            // Add shutdown hook to clean up executor
            Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
        }
}