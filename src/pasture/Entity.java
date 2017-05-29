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
     * Is the Entity alive or not?
     */
    private boolean alive;

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
        this.alive = true;
    }

    /**
     * tick() calls methods in entities that are implementing different
     * interfaces move() is executed to perform a move.
     */
    public void tick() {
        //Does the object implement a breeder
        if (this instanceof Breeder) {
            ((Breeder) this).breed();
        }

        //Does the object implements a mover?
        if (this instanceof Mover) {
            ((Mover) this).move();
        }

        //Does it implement a feeder?
        if (this instanceof Feeder) {
            pasture.getEntitiesAt(pasture.getPosition(this)).forEach((cohabitant)
                    -> {
                ((Feeder) this).feed(cohabitant);
            });
        }

    }

    /**
     * Kills this entity
     */
    public void kill() {
        this.alive = false;
    }

    /**
     * Returns true or false depending on alive state
     *
     * @return boolean alive
     */
    public boolean isAlive() {
        return this.alive;
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
