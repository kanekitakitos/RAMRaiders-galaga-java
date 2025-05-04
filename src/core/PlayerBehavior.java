package core;
import core.behaviorItems.IAttackStrategy;
import core.behaviorItems.LinearShootAttack;
import core.objectsInterface.IGameObject;
import core.objectsInterface.IInputEvent;
import geometry.Ponto;
import gui.InputEvent;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
/**
 * Represents the behavior of a player in the game.
 *
 * @Pre-Conditions:
 *   - The required dependencies such as the game object, transform, and collider must be properly initialized.
 *   - The attack strategy must be set or a default strategy is provided.
 *
 * @Post-Conditions:
 *   - The player's input events lead to evasive maneuvers, attacks, and accurate collision handling.
 *   - The game object's state is updated accordingly based on player actions.
 *
 * @see <a href="https://www.youtube.com/watch?v=PJxcxHgmK4w"> ScheduledExecutorService </a>
 * @see <a href="https://stackoverflow.com/questions/71371997/single-scheduledexecutorservice-instance-vs-multiple-scheduledexecutorservice-in"> Single ScheduledExecutorService instance vs Multiple ScheduledExecutorService instances </a>
 * @see <a href="https://www.blackbox.ai/share/211e5a8d-74e2-4daf-aca4-469f1fa1c7e9"> is useful the ScheduledExecutorService ? - BlackBox.ai </a>
 *
 * @see core.Behavior
 *
 * @author Brandon Mejia
 * @author Gabriel Pedroso
 * @author Miguel Correia
 *
 * @version 2025-04-18
 */
public class PlayerBehavior extends Behavior
{
    private IAttackStrategy attackStrategy;
    private boolean isAttacking = false;

    private int life = 3;
    private boolean isInvincible = false;
    private final long invincibilityDuration = 1000; // milliseconds

    private final ScheduledExecutorService localScheduler;

    public PlayerBehavior()
    {
        super();
        this.attackStrategy = new LinearShootAttack();
        this.localScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void setAttackStrategy(IAttackStrategy attackStrategy)
    {
        this.attackStrategy = attackStrategy;
    }

    public boolean isInvincible()
    {
        return this.isInvincible;
    }

    public int life()
    {
        return this.life;
    }

    /**
     * Updates the behavior logic.
     *
     * @param dT The time delta since the last update.
     * @param ie The input event to process.
     */
    @Override
    public void onUpdate(double dT, IInputEvent ie)
    {
        this.go.onUpdate();
        this.evasiveManeuver(ie);
    }

    public IGameObject attack(InputEvent ie)
    {
        if(this.attackStrategy == null )
            return null;

        if(!this.isAttacking && this.isEnabled() && ie.isAttack())
        {

            this.isAttacking = true;

            // Schedule to reset the attack flag after 500ms delay
            localScheduler.schedule(() ->
            {
                isAttacking = false;
            }, invincibilityDuration, TimeUnit.MILLISECONDS);

            return this.attackStrategy.execute(this.go, this.observedObject);
        }
        else
        {
            return null;
        }
    }

    /**
     * Handles collisions with other GameObjects.
     *
     * @param collisions A list of GameObjects that this behavior collided with.
     */
    @Override
    public void onCollision(ArrayList<IGameObject> collisions)
    {
        for (IGameObject go : collisions)
            go.behavior().onDestroy();

        if(this.life > 0 && !this.isInvincible && this.isEnabled())
        {
            this.life--;

            this.isInvincible = true;

            // Schedule to reset the Invincible flag after 500ms delay
            localScheduler.schedule(() ->
            {
                this.isInvincible = false;
            }, invincibilityDuration, TimeUnit.MILLISECONDS);

            System.out.println("Player hit! Remaining life: " + this.life);
        }


        if(this.life <=  0 && this.isEnabled())
            this.onDestroy();
    }

    /**
     * Executes an evasive maneuver for 1 second.
     * Uses a separate thread to perform incremental moves so the game loop is not blocked.
     * The evasion direction is determined by the current input event (IE).
     *
     * @param Iie The input event triggering the evasive maneuver.
     */
    public void evasiveManeuver(IInputEvent Iie)
    {
        if(Iie == null)
            return;

        InputEvent ie = (InputEvent) Iie;
        // Determine direction based on the input event.
        double deltaX = 0;
        double deltaY = 0;

        // Define move step.
        double moveStep = 1.0;

        if (ie.isRight())
            deltaX += moveStep;
        if (ie.isLeft())
            deltaX -= moveStep;

        final Ponto evasionVector = new Ponto(deltaX, deltaY);

        this.isInvincible = true;
        // Apply an evasive move based on calculated direction.
        this.go.transform().move(evasionVector, 0);
        // Also update the collider position.
        this.go.collider().updatePosicao();

        localScheduler.schedule(() ->
        {
            this.isInvincible = false;
        }, invincibilityDuration, TimeUnit.MILLISECONDS);
    }
}