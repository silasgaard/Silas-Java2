
package bombastic.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class IndestructibleWall extends Entity {

    public IndestructibleWall(double x, double y, double size, Image image) {
        super(x, y, size, size, image);
    }

    @Override
    public boolean isOccupying() {
        return true;
    }

    @Override
    public void interact(Entity entity) {
        // Indestructible walls cannot be destroyed.
    }

    @Override
    public void update(double dt) {
        // Walls do not change over time.
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }
}
