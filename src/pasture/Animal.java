package pasture;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation of an animal
 *
 * @author Tomas Perers
 * @version 2017-05-01
 */
public abstract class Animal extends Entity implements Mover, Feeder, Breeder {

    protected int moveInterval, moveDelay, viewDistance;
    protected int starvationCounter;
    protected int starvationDelay;
    protected int reproductionCounter;
    protected int reproductionDelay;
    protected int lastX, lastY;
    protected boolean hasFeed;

    /**
     * Creates a new animal in a pasture and sets moveInterval and viewDistance.
     *
     * @param pasture
     * @param moveInterval
     * @param viewDistance
     * @param moveDelay
     * @param reproductionDelay
     * @param starvationCounter
     */
    public Animal(Pasture pasture, int moveInterval, int viewDistance, int moveDelay, int reproductionDelay, int starvationCounter) {
        super(pasture);
        this.starvationDelay = starvationCounter;
        this.starvationCounter = starvationCounter;
        this.moveInterval = moveInterval;
        this.viewDistance = viewDistance;
        this.moveDelay = moveDelay;
        this.lastX = 1;
        this.lastY = 1;
        this.reproductionDelay = reproductionDelay;
        this.hasFeed = false;
    }

//    /**
//     * A general method for grabbing a random element from a list. Does it
//     * belong in this class?
//     */
//    private static <X> X getRandomMember(java.util.List<X> c) {
//        if (c.isEmpty()) {
//            return null;
//        }
//        int n = (int) (Math.random() * c.size());
//        return c.get(n);
//    }
////
//    /**
//     * Implementing the move method.
//     */
//    @Override
//    public void move() {
//    }

//    /**
//     * Feed on sheep or Feed on grass
//     *
//     * @param cohabitant Entity
//     */
//    @Override
//    public void feed(Entity cohabitant) {
//        if (starvationCounter <= 0 && this.isAlive()) {
//            this.kill();
//        } else {
//            if (this instanceof Wolf) {
//                if (cohabitant instanceof Sheep && this.isAlive()) {
//                    cohabitant.kill();
//                    this.hasFeed = true;
//                    starvationCounter = starvationDelay;
//                } else {
//                    starvationCounter--;
//                }
//            } else if (this instanceof Sheep) {
//                if (cohabitant instanceof Grass && this.isAlive()) {
//                    cohabitant.kill();
//                    this.hasFeed = true;
//                    starvationCounter = starvationDelay;
//                } else {
//                    starvationCounter--;
//                }
//            }
//        }
//    }
}
