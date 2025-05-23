package test;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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



        frame.setVisible(true);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() ->
        {
            if(inputEvent.isActionActive("ATTACK"))
            System.out.println("ATTACK "+ inputEvent.isActionActive("ATTACK"));
            if(inputEvent.isActionActive("RIGHT"))
            System.out.println("RIGHT "+ inputEvent.isActionActive("RIGHT"));
            if(inputEvent.isActionActive("LEFT"))
            System.out.println("LEFT "+ inputEvent.isActionActive("LEFT"));
            if(inputEvent.isActionActive("EVASIVE"))
            System.out.println("EVASIVE "+ inputEvent.isActionActive("EVASIVE"));

        }, 0, 100, TimeUnit.MILLISECONDS);

        // Add shutdown hook to clean up executor
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }
}