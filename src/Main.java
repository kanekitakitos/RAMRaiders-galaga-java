import java.util.Scanner;

import core.*;
import geometry.*;
import test.GameVisualizer;

public class Main
{
    public static void moonshak()
    {
        Scanner sc = new Scanner(System.in);

        int frame = Integer.parseInt(sc.nextLine());
        int numberOfGameObjects = Integer.parseInt(sc.nextLine());

        GameEngine ge = new GameEngine();
        for (int i = 0; i < numberOfGameObjects; i++)
            ge.add(geraGameObjects(sc));

        for (int i = 0; i < frame; i++) // atualiza X frames os gameObjects
            ge.onUpdate();


        //System.out.print(ge.checkCollision());

        sc.close();
        }

    public static GameObject geraGameObjects(Scanner sc)
    {
        try
        {

            String nome = sc.nextLine(); // Nome do objeto
            //--- Transform -------------------------------------------------

            String line = sc.nextLine(); // Transform

            String[] parts = line.split(" ");
            Ponto p = new Ponto(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
            int layer = Integer.parseInt(parts[2]);
            double angle = Double.parseDouble(parts[3]);
            double scale = Double.parseDouble(parts[4]);

            Transform t = new Transform(p, layer, angle, scale);
            //---------------------------------------------------------------

            line = sc.nextLine();

            parts = line.split(" ");
            Collider c = (parts.length <= 3) ? new Circulo(Double.parseDouble(parts[2]), t) : new Poligono(line, t);

            //---------------------------------------------------------------
            line = sc.nextLine();
            parts = line.split(" ");
            Ponto velocidade = new Ponto(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
            int moveLayer = Integer.parseInt(parts[2]);
            int rotateLayer = Integer.parseInt(parts[3]);
            int scaleLayer = Integer.parseInt(parts[4]);

           //return new GameObjects(nome, t, c, velocidade, moveLayer, rotateLayer, scaleLayer);
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }


    public static void main(String[] args)
    {
        GameVisualizer visualizer = new GameVisualizer(10, 10); // 60 e 80
        visualizer.showGameObjects(GameVisualizer.createSampleObjects());
    }

}