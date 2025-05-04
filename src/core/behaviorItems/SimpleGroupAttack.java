package core.behaviorItems;

import java.util.List;
import core.objectsInterface.IGameObject;
import geometry.Ponto;

public class SimpleGroupAttack implements IGroupAttackStrategy 
{
    final double SPACING = 0.5; // Smaller space between enemies

    @Override
    public void execute(List<IGameObject> enemies, IGameObject target)
    {


    }


    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target)
    {
        matrixFormation(enemies, target);
    }


    public void matrixFormation(List<IGameObject> enemies, IGameObject target)
    {
        int[][] pattern = {
            {0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        List<Ponto> positions = new java.util.ArrayList<>();
        Ponto startPoint = new Ponto(target.transform().position().x(), target.transform().position().y() + 5);
        int rows = pattern.length;
        int cols = pattern[0].length;
        double totalWidth = (cols - 1) * SPACING;
        double totalHeight = (rows - 1) * SPACING;
        double startX = startPoint.x() - totalWidth / 2;
        double startY = startPoint.y() - totalHeight / 2;
        for (int row = rows - 1; row >= 0; row--) {
            for (int col = 0; col < cols; col++) {
                if (pattern[row][col] == 1) {
                    double x = startX + col * SPACING;
                    double y = startY + (rows - 1 - row) * SPACING;
                    positions.add(new Ponto(x, y));
                }
            }
        }
        return positions;
    }

    public int getNumberOfEnemies()
    {
        return 40;
    }
}