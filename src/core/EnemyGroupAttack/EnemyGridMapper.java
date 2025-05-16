package core.EnemyGroupAttack;

import java.util.ArrayList;
// import java.util.HashMap; // Não mais necessário
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import java.util.List;

/**
 * Classe auxiliar para mapear inimigos em uma grade 2D.
 * Facilita o cálculo de posições e a associação de prioridades.
 */
public class EnemyGridMapper 
{

    private int[][] pattern;
    private double spacing = 70; // Removido static, pois pode ser configurável por instância se necessário
    static private IGameObject[][] enemyGrid =new IGameObject[5][10]; // Substituindo o HashMap por uma matriz 2D

    public EnemyGridMapper(int[][] pattern)
    {
        this.pattern = pattern;
    }

    // Método para associar um inimigo a uma posição (row, col) específica na grade
    public void associateEnemyAt(int row, int col, IGameObject enemy)
    {
        if (this.enemyGrid != null && row >= 0 && row < this.enemyGrid.length && col >= 0 && col < this.enemyGrid[row].length) {
            this.enemyGrid[row][col] = enemy;
        }
        // Caso contrário, está fora dos limites. Pode-se logar um erro ou lançar exceção.
    }

    // Método para obter um inimigo de uma posição (row, col) específica na grade
    public IGameObject getEnemyAt(int row, int col)
    {
        if (this.enemyGrid != null && row >= 0 && row < this.enemyGrid.length && col >= 0 && col < this.enemyGrid[row].length) {
            return this.enemyGrid[row][col];
        }
        return null; // Ou lançar exceção por estar fora dos limites
    }

    /**
     * Calcula as posições dos inimigos com base na posição do alvo e no padrão definido,
     * e associa os inimigos fornecidos às suas posições na grade.
     * A lista de inimigos fornecida DEVE ESTAR ORDENADA de acordo com a ordem de varredura
     * das células ativas no 'pattern' (linha por linha, coluna por coluna).
     *
     * @param playerPosition A posição do objeto alvo.
     * @param enemies A lista de inimigos a serem associados.
     * @return Uma lista de Ponto (posições) calculadas para os inimigos, ORDENADA POR PRIORIDADE.
     */
    public ArrayList<Ponto> calculateEnemyPositions(Ponto playerPosition, List<IGameObject> enemies)
    {
        ArrayList<Ponto> calculatedPositions = new ArrayList<>();
        double startX = playerPosition.x() - ((pattern[0].length - 1) * spacing) / 2.0;
        double startY = playerPosition.y() + 660;
        
        int enemyIndex = 0;
        // Primeiro percorre por prioridade (1 a 5)
        for (int priority = 1; priority <= 5 && enemyIndex < enemies.size(); priority++)
        {
            // Depois percorre a matriz uma única vez para esta prioridade
            for (int row = 0; row < pattern.length && enemyIndex < enemies.size(); row++)
            {
                for (int col = 0; col < pattern[row].length && enemyIndex < enemies.size(); col++)
                {
                    if (pattern[row][col] == priority)
                    {
                        // Calcula a posição física
                        double x = startX + col * spacing;
                        double y = startY - row * spacing;
                        
                        // Adiciona à lista de posições e associa o inimigo
                        calculatedPositions.add(new Ponto(x, y));
                        associateEnemyAt(row, col, enemies.get(enemyIndex));
                        enemyIndex++;
                    }
                }
            }
        }
        
        return calculatedPositions;
    }

    public boolean isEmpty()
    {
        if (enemyGrid == null) return true;
        for (int r = 0; r < enemyGrid.length; r++)
        {
            for (int c = 0; c < enemyGrid[r].length; c++) {
                if (enemyGrid[r][c] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void drawGrid()
    {
        if (enemyGrid == null || pattern == null) {
            System.out.println("Grade ou padrão não inicializado.");
            return;
        }
        for (int r = 0; r < pattern.length; r++)
        {
            for (int c = 0; c < pattern[r].length; c++)
            {
                // Verifica se a posição (r,c) está dentro dos limites da enemyGrid
                if (r < enemyGrid.length && c < enemyGrid[r].length)
                {
                    IGameObject enemy = enemyGrid[r][c];
                    if (pattern[r][c] > 0) { // Se é uma posição ativa no pattern
                        if (enemy != null) {
                            System.out.print(String.format("%-10s ||| ", enemy.name())); // Formata para melhor alinhamento
                        } else {
                            System.out.print(String.format("%-10s ||| ", ".")); // Posição ativa vazia
                        }
                    } else {
                        System.out.print(String.format("%-10s ||| ", " ")); // Posição inativa no pattern
                    }
                } else {
                     System.out.print(String.format("%-10s ||| ", "OOB")); // Fora dos limites da grade (erro de lógica)
                }
            }
            System.out.println();
        }
    }

    // Renomeado para getAllEnemies para clareza
    public ArrayList<IGameObject> getAllEnemies()
    {
        ArrayList<IGameObject> objects = new ArrayList<>();
        if (enemyGrid == null) return objects;

        for (int r = 0; r < enemyGrid.length; r++)
        {
            for (int c = 0; c < enemyGrid[r].length; c++)
            {
                if (enemyGrid[r][c] != null) { // Adiciona apenas se houver um inimigo
                    objects.add(enemyGrid[r][c]);
                }
            }
        }
        return objects;
    }

    public ArrayList<IGameObject> getEnemiesFromPattern(int[][] filterPattern)
    {
        ArrayList<IGameObject> relevantEnemies = new ArrayList<>();
        if (enemyGrid == null || pattern == null || filterPattern == null) return relevantEnemies;

        for (int r = 0; r < pattern.length; r++)
        {
            // Garante que filterPattern tenha esta linha
            if (r >= filterPattern.length) continue;
            for (int c = 0; c < pattern[r].length; c++)
            {
                // Garante que filterPattern[r] tenha esta coluna
                if (c >= filterPattern[r].length) continue;

                // Verifica se a posição é ativa em ambos os patterns e se está dentro dos limites da grade
                if (pattern[r][c] > 0 && filterPattern[r][c] > 0 &&
                    r < enemyGrid.length && c < enemyGrid[r].length) 
                {
                    IGameObject enemy = enemyGrid[r][c]; // Ponto chave da obtenção
                    if (enemy != null)
                    {
                        relevantEnemies.add(enemy);
                    }
                }
            }
        }
        return relevantEnemies;
    }

    /**
     * Exibe os detalhes da grade de inimigos no console, mostrando o nome do inimigo,
     * suas coordenadas na grade e sua posição atual no jogo para cada célula ativa.
     */
    public void displayEnemyGridDetails() {
        if (enemyGrid == null || pattern == null) {
            System.out.println("Grade de inimigos (enemyGrid) ou padrão (pattern) não inicializado.");
            return;
        }

        System.out.println("\n=== Detalhes da Grade de Inimigos ===");
        for (int r = 0; r < pattern.length; r++) {
            for (int c = 0; c < pattern[r].length; c++) {
                // Verifica se a coordenada (r,c) está dentro dos limites da enemyGrid
                if (r < enemyGrid.length && c < enemyGrid[r].length) {
                    if (pattern[r][c] > 0) { // Considera apenas posições ativas no pattern
                        IGameObject enemy = enemyGrid[r][c];
                        System.out.print("Grid[" + r + "][" + c + "]: ");
                        if (enemy != null) {
                            System.out.println(enemy.name() + 
                                               " - Posição no Jogo: " + enemy.transform().position());
                        } else {
                            System.out.println("Vazio (ativo no padrão)");
                        }
                    } else {
                        // Opcional: Mostrar células inativas do pattern para uma visualização completa da grade
                        // System.out.println("Grid[" + r + "][" + c + "]: Inativo no padrão");
                    }
                } else {
                    // Esta condição não deveria ser atingida se pattern e enemyGrid tiverem dimensões consistentes
                    System.out.println("Grid[" + r + "][" + c + "]: Coordenada fora dos limites da enemyGrid!");
                }
            }
        }
        System.out.println("=====================================\n");
    }
}