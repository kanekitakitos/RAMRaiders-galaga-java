package core.behaviorItems;

import core.objectsInterface.IGameObject;

/**
 * Interface for defining different attack strategies
 * Following the Strategy design pattern
 */
public interface IAttackStrategy
{
    /**
     * Execute the attack strategy
     *
     * @param attacker The GameObject performing the attack
     * @param target The target GameObject (usually the player)
     */
    IGameObject execute(IGameObject attacker, IGameObject target);

}