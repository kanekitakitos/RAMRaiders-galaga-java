package core;

import core.behaviorItems.IEnemyMovement;
import core.behaviorItems.ZigzagMovement;
import core.objectsInterface.IGameObject;
import gui.InputEvent;
import core.behaviorItems.IAttackStrategy;
import core.behaviorItems.IGroupAttackStrategy;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * EnemyBehavior class that defines the behavior of an enemy in the game.
 *
 * This class uses the Strategy pattern to allow dynamic selection of attack strategies
 * and the Observer pattern (inherited from the Behavior class) to monitor changes in observed objects.
 * It supports both individual and group attack strategies.
 *
 * @Pre-Conditions:
 *   - The enemy's game object, transform, and collider must be properly initialized.
 *   - The enemy should be subscribed to a valid observed GameObject when required for executing attacks.
 *
 * @Post-Conditions:
 *   - Attack execution returns a valid game object if an attack strategy is set, or null otherwise.
 *   - Group attack execution properly coordinates the selected group strategy on the target.
 *
 * @see core.Behavior
 * @see core.behaviorItems.IAttackStrategy
 * @see core.behaviorItems.IGroupAttackStrategy
 *
 * @author Brandon Mejia
 * @author Gabriel Pedroso
 * @author Miguel Correia
 *
 * @version 2025-04-18
 */
public class EnemyBehavior extends Behavior
{

    private IAttackStrategy attackStrategy = null;
    static private IGroupAttackStrategy groupAttack = null;
    private IEnemyMovement movement;
    private static final ScheduledExecutorService localScheduler = Executors.newScheduledThreadPool(1);


    private boolean isAttacking = false;
    private long attackDuration = 500; // Attack duration in milliseconds

    /**
     * Default constructor for EnemyBehavior.
     */
    public EnemyBehavior()
    {
        super();
        this.movement = new ZigzagMovement();
        // 30% chance of having 2 lives, 80% chance of having 1 life
        //this.life = new Random().nextDouble() < 0.3 ? 2 : 1;
        //TODO: Ainda falta implementar a vida do inimigo
    }

    /**
     * Sets the attack strategy for the enemy.
     *
     * @param strategy The attack strategy to use.
     */
    public void setAttack(IAttackStrategy strategy)
    {
        this.attackStrategy = strategy;
    }

    /**
     * Executes an attack using the currently set attack strategy.
     *
     * @param ie The input event triggering the attack.
     * @return The game object resulting from the attack, or null if no strategy is set.
     */
    public IGameObject attack(InputEvent ie)
    {
        if (groupAttack == null)
            return null;


        if(!this.isAttacking && this.isEnabled())
        {
            // Schedule to reset the attack flag after 500ms delay
            localScheduler.schedule(this::stopAttack, this.attackDuration, TimeUnit.MILLISECONDS);
            return this.attackStrategy.execute(this.go, this.observedObject);
        }
        else
        {
            return null;
        }
    }

    /**
     * Stops the current attack by setting the attacking flag to false.
     */
    public void stopAttack()
    {
        this.isAttacking = false;
    }

    public void startAttack()
    {
        this.isAttacking = true;
    }

    public void activateMovement(boolean value)
    {
        if (this.movement != null)
            this.movement.setActive(value);
    }

    /**
     * Checks if the enemy is currently attacking.
     *
     * @return true if the enemy is attacking, false otherwise.
     */
    public boolean isAttacking()
    {
        return isAttacking;
    }



    public void setMovement(IEnemyMovement movement)
    {
        if (movement == null)
            return;

        this.movement = movement;
    }

    /**
     * Executes a group attack using the specified group attack strategy.
     *
     * @param strategy The group attack strategy to use.
     * @param group The list of game objects participating in the attack.
     * @param target The target of the group attack.
     */
    static public void groupAttack(IGroupAttackStrategy strategy, List<IGameObject> group, IGameObject target)
    {
        strategy.onInit(group, target);
        strategy.execute(group, target);
    }

    /**
     * Executes a group attack using the instance's group attack strategy.
     *
     * @param group The list of game objects participating in the attack.
     * @param target The target of the group attack.
     */
    public void groupAttack(List<IGameObject> group, IGameObject target)
    {
        if (groupAttack == null)
            return;

        if(this.isEnabled())
            groupAttack(groupAttack, group, target);
    }

    @Override
    public void move()
    {

        if (movement.isActive())
            movement.move(this.go);

        super.move();
    }




}