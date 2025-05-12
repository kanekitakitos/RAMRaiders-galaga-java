package core.behaviorItems;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import core.objectsInterface.IGameObject;
import core.EnemyBehavior;
import geometry.Ponto;
import java.util.HashMap;
import java.util.Map;

public class ZigzagGroup implements IGroupAttackStrategy
{
    private int[][] pattern = {
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 2, 2, 2, 2, 1, 1, 1, 1, 0 },
        { 0, 2, 2, 2, 2, 1, 1, 1, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    private boolean isGroupAttackComplete = false;
    private ArrayList<IEnemyMovement> previousMovements = new ArrayList<>();
    private ScheduledExecutorService scheduler;

    private void invariante(List<IGameObject> enemies, IGameObject target)
    {
        if(enemies != null && !enemies.isEmpty() && target != null)
            return;

        System.out.println("ZigzagGroup:iv");
        System.exit(0);
    }

    private Map<Ponto, IGameObject> gridEnemyMap = new HashMap<>();
    private final double SPACING = 70;

    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target)
    {
        invariante(enemies, target);
        gridEnemyMap.clear();

        ArrayList<Ponto> positions = calculateEnemyPositions(target.transform().position(), pattern, SPACING);

        // Associa cada posição da grid ao inimigo correspondente
        int enemyIndex = 0;
        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[row].length; col++) {
                if (pattern[row][col] > 0 && enemyIndex < enemies.size()) {
                    Ponto pos = positions.get(enemyIndex);
                    gridEnemyMap.put(new Ponto(row, col), enemies.get(enemyIndex));
                    enemyIndex++;
                }
            }
        }
    }

    // Agora, para aplicar um movimento ao inimigo da posição (row, col):
    private void aplicarMovimentoNaGrid(int row, int col, IEnemyMovement movimento) 
    {
        IGameObject enemy = gridEnemyMap.get(new Ponto(row, col));
        if (enemy != null) {
            EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
            behavior.setMovement(movimento);
            movimento.setActive(true);
        }
    }
    @Override
    public void execute(List<IGameObject> enemies, IGameObject target)
    {
        for (Map.Entry<Ponto, IGameObject> entry : gridEnemyMap.entrySet()) {
            Ponto gridPos = entry.getKey();
            IGameObject enemy = entry.getValue();

            int row = (int) gridPos.x();
            int col = (int) gridPos.y();
            int patternValue = pattern[row][col];

            if (patternValue > 0) {
                EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
                ZigzagMovement movement = new ZigzagMovement();
                // 1 = esquerda, 2 = direita
                movement.setDirection(patternValue == 1);
                behavior.setMovement(movement);
                movement.setActive(true);
            }
        }
        isGroupAttackComplete = true;
    }


    @Override
    public int getNumberOfEnemies()
    {
        return 40;
    }

    @Override
    public boolean isGroupAttackComplete() {
        return this.isGroupAttackComplete;
    }

    @Override
    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }


    
    /**
     * Calculates the positions for enemies based on the target's position and the
     * defined pattern.
     *
     * @param playerPosition The position of the target object.
     * @param pattern        The pattern defining enemy priorities.
     * @param spacing        The spacing between enemy positions.
     * @return A list of calculated positions for enemies.
     */
    private ArrayList<Ponto> calculateEnemyPositions(Ponto playerPosition, int[][] pattern, double spacing) {
        @SuppressWarnings("unchecked")
        ArrayList<Ponto>[] priorityGroups = new ArrayList[6];
        for (int i = 1; i <= 5; i++)
            priorityGroups[i] = new ArrayList<>();

        double startX = playerPosition.x() - ((pattern[0].length - 1) * spacing) / 2.0;
        double startY = playerPosition.y() + 660;

        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[0].length; col++)
            {
                int priority = pattern[row][col];
                if (priority > 0)
                {
                    double x = startX + col * spacing;
                    double y = startY - row * spacing;
                    priorityGroups[priority].add(new Ponto(x, y));
                }
            }
        }

        ArrayList<Ponto> positions = new ArrayList<>();
        for (int priority = 1; priority <= 5; priority++)
        {
            positions.addAll(priorityGroups[priority]);
        }

        return positions;
    }

    private Map<Double, List<IGameObject>> agruparPorLinha(ArrayList<Ponto> positions, List<IGameObject> enemies) {
        Map<Double, List<IGameObject>> linhas = new HashMap<>();
        double tolerancia = 1.0; // tolerância para considerar valores de Y iguais
    
        for (int i = 0; i < positions.size(); i++) {
            double y = positions.get(i).y();
            boolean added = false;
            for (Double key : linhas.keySet()) {
                if (Math.abs(key - y) < tolerancia) {
                    linhas.get(key).add(enemies.get(i));
                    added = true;
                    break;
                }
            }
            if (!added) {
                List<IGameObject> novaLinha = new ArrayList<>();
                novaLinha.add(enemies.get(i));
                linhas.put(y, novaLinha);
            }
        }
        return linhas;
    }
}
