package pasture;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected String food;
    protected boolean scared;

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

    /**
     * Implementing the move method.
     */
    @Override
    public void move() {
        if (moveDelay <= 0 && pasture.getEntityPosition(this) != null) {
            // perform move
            if (this.evaluateDirection() != null) {
                pasture.moveEntity(this, evaluateDirection());
            }
            moveDelay = moveInterval;
        }
        moveDelay--;
    }

    private Point evaluateDirection() {
        // get all entities within viewDistance of the animal
        List<Entity> seen = pasture.getEntitiesWithinDistance(pasture.getPosition(this), this.viewDistance);

        // score all points surrounding our position, inclusive
        Map<Point, Double> scoredNeighbours = new HashMap<>();
        Point here = pasture.getPosition(this);

        pasture.getAllNeighbours(here).forEach((neighbour)
                -> {
            Double score = 0.0;
            for (Entity e : seen) {
                Double distance = neighbour.distance(pasture.getPosition(e));
                if (e instanceof Sheep) { // only eat sheep
                    score += 100 / (1 + distance);
                }
            }
            scoredNeighbours.put(neighbour, score);
        });

        // get optimal direction
        // from http://stackoverflow.com/questions/5911174/finding-key-associated-with-max-value-in-a-java-map
        Map.Entry<Point, Double> maxEntry = null;
        for (Map.Entry<Point, Double> entry : scoredNeighbours.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        Point preferredNeighbour = maxEntry.getKey();
        // if we can't go in the preferred direction, continue in direction from last turn.
        if (pasture.getFreeNeighbours(this).contains(preferredNeighbour) == false) {
            preferredNeighbour
                    = new Point((int) pasture.getPosition(this).getX() + lastX,
                            (int) pasture.getPosition(this).getY() + lastY);
        }
        // if we still can't go there, resort to random direction
        if (pasture.getFreeNeighbours(this).contains(preferredNeighbour) == false) {
            preferredNeighbour
                    = getRandomMember(pasture.getFreeNeighbours(this));
        }
        // update direction
        if (preferredNeighbour != null) {
            lastX = (int) preferredNeighbour.getX() - (int) pasture.getPosition(this).getX();
            lastY = (int) preferredNeighbour.getY() - (int) pasture.getPosition(this).getY();
        }
        return preferredNeighbour;
    }

    /**
     * A general method for grabbing a random element from a list. Does it
     * belong in this class?
     */
    private static <X> X getRandomMember(java.util.List<X> c) {
        if (c.isEmpty()) {
            return null;
        }
        int n = (int) (Math.random() * c.size());
        return c.get(n);
    }
}
