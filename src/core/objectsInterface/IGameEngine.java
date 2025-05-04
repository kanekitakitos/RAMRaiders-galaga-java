package core.objectsInterface;

public interface IGameEngine
{
    void addEnable(IGameObject go);
    void addDisable(IGameObject go);

    void enable(IGameObject go);
    void disable(IGameObject go);

    boolean isEnabled(IGameObject go);

    boolean isDisabled(IGameObject go);



    /**
     * Destroy IGameObject go whether it is enabled or disabled
     *  pre: go != null
     *  pos: go.onDestroy()
     * @param go
     */
    void destroy(IGameObject go);


    /**
     * Destroy all IGameObjects
     *  pos: calls onDestroy() for each IGameObject
     */
    void destroyAll();




    /**
     *  Generates a new frame:
     *  Get user input from UI
     *  update all the enabled GameObjects
     *  check for collisions and send info to the GameObjects
     *  update UI
     *  pos: UI.input() &&
     *       calls Behaviour.onUpdate() for all enabled Object &&
     *       Behaviour.onCollision() &&
     *       UI.draw()
     *
     */
    void run();



    /**
     * Checks for collisions for all the enabled objects
     * pos: calls Behavior.onCollision(go) for all enabled GameObjects
     * 			passing in the list of all the objects that collided with each IGameObject
     */
    void checkCollision();



}
