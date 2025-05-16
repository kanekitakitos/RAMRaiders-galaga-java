package core.EnemyGroupAttack;

import java.util.ArrayList;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import java.util.List;

/**
 * The `EnemyGridMapper` class manages a 2D grid of enemy game objects,
 * allowing association, retrieval, and organization of enemies based on a
 * pattern.
 * It provides methods to calculate enemy positions, check grid state, and
 * display grid details.
 *
 * <p>
 * Responsibilities:
 * </p>
 * - Associate enemies to specific grid positions.
 * - Calculate enemy positions based on a pattern and player position.
 * - Retrieve enemies from the grid.
 * - Display and query the state of the enemy grid.
 *
 * @see IGameObject
 * @see Ponto
 *
 * @author Brandon Mejia
 * @version 2025-05-16
 */
public class EnemyGridMapper {

    /** The pattern defining the grid layout and priorities. */
    private int[][] pattern;
    /** The spacing between grid cells. */
    private double spacing = 70;
    /** 2D array representing the enemy grid. */
    static private IGameObject[][] enemyGrid = new IGameObject[5][10];

    /**
     * Constructs an `EnemyGridMapper` with specified rows, columns, and pattern.
     *
     * @param row     Number of rows in the grid.
     * @param col     Number of columns in the grid.
     * @param pattern The pattern defining the grid layout.
     */
    public EnemyGridMapper(int row, int col, int[][] pattern) {
        this.pattern = new int[row][col];
    }

    /**
     * Constructs an `EnemyGridMapper` with a predefined pattern.
     *
     * @param pattern The pattern defining the grid layout.
     */
    public EnemyGridMapper(int[][] pattern) {
        this.pattern = pattern;
    }

    /**
     * Associates an enemy with a specific position in the grid.
     *
     * @param row   The row index in the grid.
     * @param col   The column index in the grid.
     * @param enemy The enemy to associate with the position.
     */
    public void associateEnemyAt(int row, int col, IGameObject enemy) {
        if (EnemyGridMapper.enemyGrid != null && row >= 0 && row < EnemyGridMapper.enemyGrid.length && col >= 0
                && col < EnemyGridMapper.enemyGrid[row].length) {
            EnemyGridMapper.enemyGrid[row][col] = enemy;
        }
        // Otherwise, out of bounds.
    }

    /**
     * Retrieves the enemy at a specific position in the grid.
     *
     * @param row The row index in the grid.
     * @param col The column index in the grid.
     * @return The enemy at the specified position, or null if none exists.
     */
    public IGameObject getEnemyAt(int row, int col) {
        if (EnemyGridMapper.enemyGrid != null && row >= 0 && row < EnemyGridMapper.enemyGrid.length && col >= 0
                && col < EnemyGridMapper.enemyGrid[row].length) {
            return EnemyGridMapper.enemyGrid[row][col];
        }
        return null;
    }

    /**
     * Calculates the positions of enemies based on the player's position and the
     * grid pattern.
     * Associates each enemy to its calculated grid position.
     *
     * @param playerPosition The position of the player.
     * @param enemies        The list of enemies to position.
     * @return A list of calculated positions for the enemies.
     */
    public ArrayList<Ponto> calculateEnemyPositions(Ponto playerPosition, List<IGameObject> enemies) {
        ArrayList<Ponto> calculatedPositions = new ArrayList<>();
        double startX = playerPosition.x() - ((pattern[0].length - 1) * spacing) / 2.0;
        double startY = playerPosition.y() + 660;

        int enemyIndex = 0;
        // Iterate by priority (1 to 5)
        for (int priority = 1; priority <= 5 && enemyIndex < enemies.size(); priority++) {
            for (int row = 0; row < pattern.length && enemyIndex < enemies.size(); row++) {
                for (int col = 0; col < pattern[row].length && enemyIndex < enemies.size(); col++) {
                    if (pattern[row][col] == priority) {
                        double x = startX + col * spacing;
                        double y = startY - row * spacing;
                        calculatedPositions.add(new Ponto(x, y));
                        associateEnemyAt(row, col, enemies.get(enemyIndex));
                        enemyIndex++;
                    }
                }
            }
        }
        return calculatedPositions;
    }

    /**
     * Checks if the enemy grid is empty.
     *
     * @return true if the grid is empty, false otherwise.
     */
    public boolean isEmpty() {
        if (enemyGrid == null)
            return true;
        for (int r = 0; r < enemyGrid.length; r++) {
            for (int c = 0; c < enemyGrid[r].length; c++) {
                if (enemyGrid[r][c] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Draws a visual representation of the enemy grid in the console.
     * Shows enemy names, empty slots, and out-of-bounds cells.
     */
    public void drawGrid() {
        if (enemyGrid == null || pattern == null) {
            System.out.println("Grade ou padrão não inicializado.");
            return;
        }
        for (int r = 0; r < pattern.length; r++) {
            for (int c = 0; c < pattern[r].length; c++) {
                if (r < enemyGrid.length && c < enemyGrid[r].length) {
                    IGameObject enemy = enemyGrid[r][c];
                    if (pattern[r][c] > 0) {
                        if (enemy != null) {
                            System.out.print(String.format("%-10s ||| ", enemy.name()));
                        } else {
                            System.out.print(String.format("%-10s ||| ", "."));
                        }
                    } else {
                        System.out.print(String.format("%-10s ||| ", " "));
                    }
                } else {
                    System.out.print(String.format("%-10s ||| ", "OOB"));
                }
            }
            System.out.println();
        }
    }

    /**
     * Retrieves all enemies currently in the grid.
     *
     * @return A list of all enemies in the grid.
     */
    public ArrayList<IGameObject> getAllEnemies() {
        ArrayList<IGameObject> objects = new ArrayList<>();
        if (enemyGrid == null)
            return objects;

        for (int r = 0; r < enemyGrid.length; r++) {
            for (int c = 0; c < enemyGrid[r].length; c++) {
                if (enemyGrid[r][c] != null) {
                    objects.add(enemyGrid[r][c]);
                }
            }
        }
        return objects;
    }

    /**
     * Retrieves enemies from the grid that match a specific filter pattern.
     *
     * @param filterPattern The pattern to filter enemies.
     * @return A list of enemies matching the filter pattern.
     */
    public ArrayList<IGameObject> getEnemiesFromPattern(int[][] filterPattern) {
        ArrayList<IGameObject> relevantEnemies = new ArrayList<>();
        if (enemyGrid == null || pattern == null || filterPattern == null)
            return relevantEnemies;

        for (int r = 0; r < pattern.length; r++) {
            if (r >= filterPattern.length)
                continue;
            for (int c = 0; c < pattern[r].length; c++) {
                if (c >= filterPattern[r].length)
                    continue;

                if (pattern[r][c] > 0 && filterPattern[r][c] > 0 &&
                        r < enemyGrid.length && c < enemyGrid[r].length) {
                    IGameObject enemy = enemyGrid[r][c];
                    if (enemy != null) {
                        relevantEnemies.add(enemy);
                    }
                }
            }
        }
        return relevantEnemies;
    }

    /**
     * Displays detailed information about the enemy grid in the console,
     * including enemy names and positions for each active cell.
     */
    public void displayEnemyGridDetails() {
        if (enemyGrid == null || pattern == null) {
            System.out.println("Grade de inimigos (enemyGrid) ou padrão (pattern) não inicializado.");
            return;
        }

        System.out.println("\n=== Detalhes da Grade de Inimigos ===");
        for (int r = 0; r < pattern.length; r++) {
            for (int c = 0; c < pattern[r].length; c++) {
                if (r < enemyGrid.length && c < enemyGrid[r].length) {
                    if (pattern[r][c] > 0) {
                        IGameObject enemy = enemyGrid[r][c];
                        System.out.print("Grid[" + r + "][" + c + "]: ");
                        if (enemy != null) {
                            System.out.println(enemy.name() +
                                    " - Posição no Jogo: " + enemy.transform().position());
                        } else {
                            System.out.println("Vazio (ativo no padrão)");
                        }
                    }
                } else {
                    System.out.println("Grid[" + r + "][" + c + "]: Coordenada fora dos limites da enemyGrid!");
                }
            }
        }
        System.out.println("=====================================\n");
    }
}