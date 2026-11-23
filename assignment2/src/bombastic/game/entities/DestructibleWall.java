
package bombastic.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class DestructibleWall extends Entity {

    private boolean destroyed = false;

    public DestructibleWall(double x, double y, double size, Image image) {
        super(x, y, size, size, image);
    }

    @Override
    public boolean isOccupying() {
        return !destroyed;
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof Explosion) {
            destroy();
        }
    }

    public void destroy() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void update(double dt) {
        // No time-based behavior.
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!destroyed) {
            gc.drawImage(image, x, y, width, height);
        }
    }
}
