package pasture;

import javax.swing.*;

/**
 * This is the superclass of all entities in the pasture simulation system. This
 * interface <b>must</b> be implemented by all entities that exist in the
 * simulation of the pasture.
 */
public abstract class Entity {

    /**
     * The icon of the entity
     */
    protected ImageIcon image;

    /**
     * Entity position
     */
    protected final Pasture pasture;

    /**
     * Counter for reproduction ready
     */
    private int reproductionCounter;

    /**
     * Create an entity object
     *
     * @param pasture Pasture input parameter
     */
    public Entity(Pasture pasture) {
        this.pasture = pasture;
        this.image = new ImageIcon("unkown.gif");
    }

    /**
     * tick() calls methods in entities that are implementing different
     * interfaces move() is executed to perform a move.
     */
    abstract public void tick();

    /**
     * Kills this entity
     */
    public void kill() {
        pasture.removeEntity(this);
    }

    /**
     * ImageIcon returns the icon of this entity, to be displayed by the pasture
     * gui.
     *
     * @return
     */
    public ImageIcon getImage() {
        return this.image;
    }

    /**
     * Tests if this entity can be in the same position as another given entity
     *
     * @param otherEntity Entity object
     * @return false
     */
    public boolean isCompatible(Entity otherEntity) {
        return false;
    }

}
