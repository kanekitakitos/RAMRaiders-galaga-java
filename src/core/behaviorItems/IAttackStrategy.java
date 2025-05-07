package core.behaviorItems;

      import core.objectsInterface.IGameObject;

      /**
       * Interface for defining different attack strategies.
       * This interface follows the Strategy design pattern, allowing for the implementation
       * of various attack behaviors that can be dynamically assigned to game objects.
       *
       * <p>Key Features:</p>
       * <ul>
       *   <li>Encapsulates attack behavior to allow dynamic assignment to game objects.</li>
       *   <li>Supports the creation of diverse attack strategies (e.g., melee, ranged, homing).</li>
       *   <li>Ensures flexibility and reusability in game object behavior design.</li>
       * </ul>
       *
       * @preConditions:
       * - The `attacker` must be a valid `IGameObject` instance with the necessary attributes for performing an attack.
       * - The `target` must be a valid `IGameObject` instance, typically representing the player or another game entity.
       *
       * @postConditions:
       * - The attack strategy will be executed, potentially modifying the state of the `attacker` and/or `target`.
       * - A new `IGameObject` may be returned, representing the result of the attack (e.g., a projectile or effect).
       *
       * @see IGameObject
       */
      public interface IAttackStrategy {

          /**
           * Executes the attack strategy.
           * This method defines the behavior of the attack, which may involve creating a new game object
           * (e.g., a projectile) or directly modifying the state of the attacker and/or target.
           *
           * @param attacker The `IGameObject` performing the attack.
           * @param target The target `IGameObject` (usually the player or another entity).
           * @return An `IGameObject` representing the result of the attack (e.g., a projectile or effect),
           *         or `null` if no object is created as a result of the attack.
           * @throws IllegalArgumentException if the `attacker` or `target` is null, or if they are the same object.
           */
          IGameObject execute(IGameObject attacker, IGameObject target);
      }