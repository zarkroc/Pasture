package pasture;

import javax.swing.*;

/**
 * A representation of a fence.
 *
 * @author Tomas Perers
 * @version 2017-05-01
 */
public class Fence extends Entity {

    public Fence(Pasture pasture) {
        super(pasture);
        this.image = new ImageIcon(getClass().getResource("fence.gif"));
    }
    public void tick() {
        // We are a fence we do nothing.
    }
}
