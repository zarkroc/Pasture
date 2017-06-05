package pasture;

/**
 * A representation of an animal
 *
 * @author Tomas Perers
 * @version 2017-06-05
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
}
