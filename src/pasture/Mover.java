package pasture;

/**
 * Implemented by all moving entities
 *
 * @author Tomas Perers
 * @version 2017-04-01
 */
public interface Mover {

    /**
     * Called from Entity::tick() for all Movable entities.
     *
     */
    public void move();

}
