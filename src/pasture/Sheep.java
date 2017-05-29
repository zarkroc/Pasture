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
 * @version 2017-05-01
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
     */
    public Sheep(Pasture pasture, int moveInterval, int viewDistance, int reproductionDelay, int starvationDelay) {
        super(pasture, moveInterval, viewDistance, moveInterval, reproductionDelay, starvationDelay);
        this.image = new ImageIcon(getClass().getResource("sheep.gif"));
        this.reproductionCounter = reproductionDelay;
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
     * Overrides the breed function. If there is a space next to a sheep it will
     * create a new instance if the counter is 0 or less. If not the counter
     * will be decreased.
     */
    @Override
    public void breed() {
        if (reproductionCounter-- <= 0 && this.isAlive()) {
            //Is there a near free space?
            if (pasture.getFreeNeighbours(this).size() > 0 && hasFeed == true) {
                pasture.addEntity(new Sheep(pasture, moveInterval, viewDistance,
                        reproductionDelay, starvationDelay),
                        pasture.getFreeNeighbours(this).get((int) (Math.random()
                                * pasture.getFreeNeighbours(this).size())));
                this.reproductionCounter = reproductionDelay;
            }
        }
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
                    if (e instanceof Wolf) { // Run away
                        score -= 100 / (1 + distance);
                    } else if (e instanceof Grass) {
                        score += 90 / (1 + distance);
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
     * Feed on grass
     *
     * @param cohabitant Entity
     */
    @Override
    public void feed(Entity cohabitant) {
        if (starvationCounter <= 0 && this.isAlive()) {
            this.kill();
        } else {
            if (cohabitant instanceof Grass && this.isAlive()) {
                cohabitant.kill();
                this.hasFeed = true;
                starvationCounter = starvationDelay;
            } else {
                starvationCounter--;
            }
        }
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
