
package bombastic.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Floor extends Entity {

    public Floor(double x, double y, double size, Image image) {
        super(x, y, size, size, image);
    }

    @Override
    public boolean isOccupying() {
        return false;
    }

    @Override
    public void interact(Entity entity) {
        // Floor has no special interactions.
    }

    @Override
    public void update(double dt) {
        // Floor does not change over time.
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }
}
