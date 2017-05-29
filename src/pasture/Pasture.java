package pasture;

import java.util.*;
import java.awt.Point;

/**
 * A pasture contains sheep, wolves, fences, plants, and possibly other
 * entities. These entities move around in the pasture and try to find food,
 * other entities of the same kind and run away from possible enimies.
 */
public class Pasture {

    private int sheepMoveInterval;
    private int sheepViewDistance;
    private int wolfMoveInterval;
    private int wolfViewDistance;
    private final int SHEEP_REPRODUCTION_DELAY = 101;
    private final int WOLF_REPRODUCTION_DELAY = 201;
    private final int WOLF_STARVATION_DELAY = 200;
    private final int SHEEP_STARVATION_DELAY = 100;

    private final int width = 20;
    private final int height = 20;

    private final int fence = 40;
    private final int sheep = 20;
    private final int wolves = 10;
    private final int plants = 40;

    private final Set<Entity> world =
       new HashSet<Entity>();

    private final Map<Point, List<Entity>> grid =
       new HashMap<Point, List<Entity>>();

    private final Map<Entity, Point> point
       = new HashMap<Entity, Point>();



    private final PastureGUI gui;

    /**
     * Creates a new instance of this class and places the entities in it on
     * random positions.
     *
     * @param sheepMoveInterval Integer moving interval of sheep
     * @param sheepViewDistance Integer view distance of sheep
     * @param wolfMoveInterval Integer moving interval of wolf
     * @param wolfViewDistance Integer view distance of wolf
     */
    public Pasture(int sheepMoveInterval, int sheepViewDistance, int wolfMoveInterval, int wolfViewDistance) {

        this.sheepMoveInterval = sheepMoveInterval;
        this.sheepViewDistance = sheepViewDistance;
        this.wolfMoveInterval = wolfMoveInterval;
        this.wolfViewDistance = wolfViewDistance;
        Engine engine = new Engine(this);
        gui = new PastureGUI(width, height, engine);

        /* The pasture is surrounded by a fence. Replace Dummy for
         * Fence when you have created that class */
        for (int i = 0; i < width; i++) {
            addEntity(new Fence(this), new Point(i, 0));
            addEntity(new Fence(this), new Point(i, height - 1));
        }
        for (int i = 1; i < height - 1; i++) {
            addEntity(new Fence(this), new Point(0, i));
            addEntity(new Fence(this), new Point(width - 1, i));
        }

        /* 
         * Now insert the right number of different entities in the
         * pasture.
         */
        for (int i = 0; i < plants; i++) {
            Entity grass = new Grass(this);
            addEntity(grass, getFreePosition(grass));
        }

        for (int i = 0; i < sheep; i++) {
            Entity sheep = new Sheep(this, sheepMoveInterval, sheepViewDistance,
                    SHEEP_REPRODUCTION_DELAY, SHEEP_STARVATION_DELAY);
            addEntity(sheep, getFreePosition(sheep));
        }

        for (int i = 0; i < wolves; i++) {
            Entity wolf = new Wolf(this, wolfMoveInterval, wolfViewDistance,
                    WOLF_REPRODUCTION_DELAY, WOLF_STARVATION_DELAY);
            addEntity(wolf, getFreePosition(wolf));
        }

        for (int i = 0; i < fence; i++) {
            Entity fence = new Fence(this);
            addEntity(fence, getFreePosition(fence));
        }

        gui.update();
    }

    public void refresh() {
        gui.update();
    }

    /**
     * Return a free position at random if there is a free position in the
     * pasture.
     */
    private Point getFreePosition(Entity entityToPlace)
            throws MissingResourceException {
        Point position = new Point((int) (Math.random() * width),
                (int) (Math.random() * height));
        int p = position.x + position.y * width;
        int m = height * width;
        int q = 97;

        for (int i = 0; i < m; i++) {
            int j = (p + i + q) % m;
            int x = j % width;
            int y = j / width;
            position = new Point(x, y);
            boolean free = true;
            Collection<Entity> c = getEntitiesAt(position);
            if (c != null) {
                for (Entity thisThing : c) {
                    if (!entityToPlace.isCompatible(thisThing)) {
                        free = false;
                        break;
                    }
                }
            }
            if (free) {
                return position;
            }
        }
        throw new MissingResourceException(
                "There is no free space" + " left in the pasture",
                "Pasture", "");
    }

    /**
     * Returns the Point of a Entity
     *
     * @param e Entity
     * @return point.get(e) Point
     */
    public Point getPosition(Entity e) {
        return point.get(e);
    }

    /**
     * Add a new entity to the pasture.
     *
     * @param entity
     * @param pos
     */
    public void addEntity(Entity entity, Point pos) {

        world.add(entity);

        List<Entity> l = grid.get(pos);
        if (l == null) {
            l = new ArrayList<>();
            grid.put(pos, l);
        }
        l.add(entity);

        point.put(entity, pos);

        gui.addEntity(entity, pos);
    }

    public void moveEntity(Entity e, Point newPos) {

        Point oldPos = point.get(e);
        List<Entity> l = grid.get(oldPos);
        if (!l.remove(e)) {
            throw new IllegalStateException("Inconsistent stat in Pasture");
        }
        /* We expect the entity to be at its old position, before we
           move, right? */

        l = grid.get(newPos);
        if (l == null) {
            l = new ArrayList<>();
            grid.put(newPos, l);
        }
        l.add(e);

        point.put(e, newPos);

        gui.moveEntity(e, oldPos, newPos);
    }

    /**
     * Remove the specified entity from this pasture.
     *
     * @param entity Entity
     */
    public void removeEntity(Entity entity) {

        Point p = point.get(entity);
        world.remove(entity);
        grid.get(p).remove(entity);
        point.remove(entity);
        gui.removeEntity(entity, p);

    }

    /**
     * Various methods for getting information about the pasture
     *
     * @return ArrayList world
     */
    public List<Entity> getEntities() {
        return new ArrayList<>(world);
    }

    public Collection<Entity> getEntitiesAt(Point lookAt) {

        Collection<Entity> l = grid.get(lookAt);

        if (l == null) {
            return null;
        } else {
            return new ArrayList<>(l);
        }
    }

    public List<Point> getFreeNeighbours(Entity entity) {
        List<Point> free = new ArrayList<Point>();

        int entityX = getEntityPosition(entity).x;
        int entityY = getEntityPosition(entity).y;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Point p = new Point(entityX + x,
                        entityY + y);
                if (freeSpace(p, entity)) {
                    free.add(p);
                }
            }
        }
        return free;
    }

    /**
     * Get all Points close to origin
     *
     * @param origin Point
     * @return List<Point> surrounding
     */
    public List<Point> getAllNeighbours(Point origin) {
        List<Point> surrounding = new ArrayList<>();

        int originX = origin.x;
        int originY = origin.y;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Point p = new Point(originX + x, originY + y);
                surrounding.add(p);
            }
        }
        return surrounding;
    }

    private boolean freeSpace(Point p, Entity e) {

        List<Entity> l = grid.get(p);
        if (l == null) {
            return true;
        }
        return l.stream().noneMatch((old) -> (! old.isCompatible(e)));
    }

    public Point getEntityPosition(Entity entity) {
        return point.get(entity);
    }

    /**
     * Returns all points with an entity
     *
     * @return
     */
    private Set<Point> getOccupiedPoints() {
        return grid.keySet();
    }

    /**
     * Return all entities found within distance from a point.
     *
     * @param origin Point
     * @param maxDistance int maxDistance
     * @return List<Entity> found
     */
    public List<Entity> getEntitiesWithinDistance(Point origin, int maxDistance) {
        List<Entity> found = new ArrayList<>();
        world.stream().filter((e) -> (origin.distance(this.getPosition(e)) <= maxDistance)).forEachOrdered((e)
                -> {
            found.add(e);
        });
        return found;
    }

    /**
     * The method for the JVM to run.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Enter Y if you want to use default parameters for the pasture");
        Scanner input = new Scanner(System.in);
        String defaultParameters = input.next().toLowerCase();
        int sheepMove;
        int sheepView;
        int wolfMove;
        int wolfView;

        if (defaultParameters.equals("y")) {
            sheepMove = 10;
            sheepView = 3;
            wolfMove = 20;
            wolfView = 3;
        } else {
            System.out.println("Input moving interval of Sheep");
            sheepMove = input.nextInt();
            System.out.println("Input viewving distance of Sheep");
            sheepView = input.nextInt();
            System.out.println("Input moving interval of Wolf");
            wolfMove = input.nextInt();
            System.out.println("Input viewving distance of Wolf");
            wolfView = input.nextInt();
        }

        new Pasture(sheepMove, sheepView, wolfMove, wolfView);
    }

}
