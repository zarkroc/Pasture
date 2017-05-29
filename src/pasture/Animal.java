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

    /**
     * Implementing the move method.
     */
    @Override
    public void move() {
        if (this.moveDelay-- <= 0 && this.isAlive()) {
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
                    if (this instanceof Sheep) {
                        if (e instanceof Wolf) { // Run away
                            score -= 100 / (1 + distance);
                        } else if (e instanceof Grass) {
                            score += 90 / (1 + distance);
                        }
                    } else if (this instanceof Wolf) {
                        if (e instanceof Sheep) { // only eat sheep
                            score += 100 / (1 + distance);
                        }
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
            lastX = (int) preferredNeighbour.getX() - (int) pasture.getPosition(this).getX();
            lastY = (int) preferredNeighbour.getY() - (int) pasture.getPosition(this).getY();
            // perform move
            pasture.moveEntity(this, preferredNeighbour);
            this.moveDelay = this.moveInterval;
        }
    }

    /**
     * Feed on sheep or Feed on grass
     *
     * @param cohabitant Entity
     */
    @Override
    public void feed(Entity cohabitant) {
        if (starvationCounter <= 0 && this.isAlive()) {
            this.kill();
        } else {
            if (this instanceof Wolf) {
                if (cohabitant instanceof Sheep && this.isAlive()) {
                    cohabitant.kill();
                    this.hasFeed = true;
                    starvationCounter = starvationDelay;
                } else {
                    starvationCounter--;
                }
            } else if (this instanceof Sheep) {
                if (cohabitant instanceof Grass && this.isAlive()) {
                    cohabitant.kill();
                    this.hasFeed = true;
                    starvationCounter = starvationDelay;
                } else {
                    starvationCounter--;
                }
            }
        }
    }
}
