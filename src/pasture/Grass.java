package pasture;

import javax.swing.*;

/**
 * Representation of a plant
 *
 * @author Tomas Perers
 * @version 2017-05-01
 */
public class Grass extends Entity implements Breeder {

    static final int MULTIPLY_INTERVAL = 10;
    private int multiplyDelay = 10;

    public Grass(Pasture pasture) {
        super(pasture);
        this.image = new ImageIcon(getClass().getResource("plant.gif"));

    }

    /**
     * overrides the breed method. If there is an empty space and if the delay
     * is 0 or less. Create a new grass. Reset the delay.
     */
    @Override
    public void breed() {
        if (multiplyDelay-- <= 0) {
            // free space adjacent?
            if (pasture.getFreeNeighbours(this).size() > 0) {
                pasture.addEntity(new Grass(pasture),
                        pasture.getFreeNeighbours(this).get(
                                (int) (Math.random() * pasture.getFreeNeighbours(this).size())
                        )
                );
                this.multiplyDelay = MULTIPLY_INTERVAL;
            }
        }
    }

    /**
     * Test if another entity can be in the same spot Animals can but not Plants
     * (Grass)
     *
     * @param otherEntity Entity
     * @return boolean true or false
     */
    @Override
    public boolean isCompatible(Entity otherEntity) {
        return (otherEntity instanceof Animal);
    }
}
