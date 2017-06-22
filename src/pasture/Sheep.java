package pasture;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

/**
 * A representation of a sheep
 *
 * @author Tomas Perers
 * @version 2017-06-05
 */
public class Sheep extends Animal {

    /**
     * Creates a new instance of a sheep class. Sheep will live in the pasture
     * Sends the pasture and move interval and view distance to the super class.
     *
     * @param pasture Pasture to live in
     * @param moveInterval int with move delay
     * @param viewDistance int with view distance
     * @param reproductionDelay int with reproduction delay.
     * @param starvationDelay int days until starving.
     */
    public Sheep(Pasture pasture, int moveInterval, int viewDistance, int reproductionDelay, int starvationDelay) {
        super(pasture, moveInterval, viewDistance, moveInterval, reproductionDelay, starvationDelay);
        this.image = new ImageIcon(getClass().getResource("sheep.gif"));
        this.reproductionCounter = reproductionDelay;
        this.food = "Grass";
        this.scared = "Wolf";
    }

    @Override
    public boolean isCompatible(Entity otherEntity) {
        if (otherEntity instanceof Grass) {
            return true;
        } else if (otherEntity instanceof Wolf) {
            return true;
        }
        return false;
    }
    
    /**
     * tick() calls methods to perform breeding, moving and feeding.
     */
    @Override
    public void tick() {
        move();
        pasture.getEntitiesAt(pasture.getPosition(this)).forEach((cohabitant)
                -> {
            this.feed(cohabitant);
        });
        breed();
    }

    /**
     * Overrides the breed function. If there is a space next to a sheep it will
     * create a new instance if the counter is 0 or less. If not the counter
     * will be decreased.
     */
    @Override
    public void breed() {
        if (reproductionCounter <= 0 && pasture.getEntityPosition(this) != null) {
            //Is there a near free space?
            if (pasture.getFreeNeighbours(this).size() > 0 && hasFeed == true) {
                pasture.addEntity(new Sheep(pasture, moveInterval, viewDistance,
                        reproductionDelay, starvationDelay),
                        pasture.getFreeNeighbours(this).get((int) (Math.random()
                                * pasture.getFreeNeighbours(this).size())));
                this.reproductionCounter = reproductionDelay;
            }
        }
        reproductionCounter--;
    }          

    /**
     * Feed on grass
     *
     * @param cohabitant Entity
     */
    @Override
    public void feed(Entity cohabitant) {
        if (starvationCounter <= 0 && pasture.getEntityPosition(this) != null) {
            this.kill();
        } else {
            if (cohabitant instanceof Grass && pasture.getEntityPosition(this) != null) {
                cohabitant.kill();
                this.hasFeed = true;
                starvationCounter = starvationDelay;
            }
        }
        starvationCounter--;
    }
}
