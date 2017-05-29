package pasture;

/**
 * Implemented by all feeding entities that feeds on plants.
 * @author Tomas Perers
 * @version 2015-05-01
 */
public interface Feeder {
    public void feed (Entity cohabitant);
    
}
