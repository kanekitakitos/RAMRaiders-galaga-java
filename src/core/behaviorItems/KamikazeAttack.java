package core.behaviorItems;

    import core.GameObject;
    import core.objectsInterface.IGameObject;
    import geometry.Ponto;

    public class KamikazeAttack implements IAttackStrategy
    {
        private boolean hadPath = false;

        private void invariante(IGameObject attacker, IGameObject target)
        {
            if (attacker == null || target == null || attacker.equals(target))
            {
                System.out.println("KamikazeAttack:vi");
                throw new IllegalArgumentException("attacker or target is null or they are the same object");
            }
        }

        @Override
        public IGameObject execute(IGameObject attacker, IGameObject target)
        {
            invariante(attacker, target);

            if (hadPath)

                calculateTargetPosition(attacker, target);

            else
            {
                // Initial circular movement setup
                double radius = 1; // Radius of the circular path

                // Calculate center point for the circle relative to attacker's position
                Ponto currentPos = attacker.transform().position();
                double centerX = currentPos.x() + radius;
                double centerY = currentPos.y();

                // Calculate circular movement
                double angle = attacker.transform().angle() * Math.PI / 180;
                double newX = centerX + radius * Math.cos(angle);
                double newY = centerY + radius * Math.sin(angle);

                // Create velocity vector for circular movement
                Ponto velocity = new Ponto(
                    (newX - currentPos.x()) * 0.5,
                    (newY - currentPos.y()) * 0.5
                );

                ((GameObject)attacker).velocity(velocity);
                attacker.transform().rotate(15); // Rotate gradually

                // After completing approximately one circle, switch to direct path
                if (attacker.transform().angle() >= 360)
                    hadPath = true;
            }

            return attacker;
        }


        private double calculateAngleToTarget(IGameObject attacker, IGameObject target)
        {
            Ponto attackerPosition = attacker.transform().position();
            Ponto targetPosition = target.transform().position();
            double dx = targetPosition.x() - attackerPosition.x();
            double dy = targetPosition.y() - attackerPosition.y();
            return Math.toRadians(new Ponto(dx, dy).theta());
        }


        private void calculateTargetPosition(IGameObject attacker, IGameObject target)
        {
            double SPEED = 1.0;
            // Calculate angle to target
            double theta = calculateAngleToTarget(attacker, target);

            // Update attacker's velocity to move towards target
            Ponto velocity = new Ponto(
                    Math.cos(theta) * SPEED,
                    Math.sin(theta) * SPEED
            );

            //Update attacker's properties
            ((GameObject) attacker).velocity(velocity);

            double nextAngle = (Math.toDegrees(theta) + 360) - attacker.transform().angle();
            double angle = attacker.transform().angle() + nextAngle;
            attacker.transform().rotate(nextAngle == 0.0 ? 0.0 : angle);
            // you don't need to update the collider here, as it is done in the move method
        }
    }