
package bombastic.game.entities.powerups;

import bombastic.game.entities.Entity;
import bombastic.game.entities.Player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class PowerUp extends Entity {

    private boolean collected = false;

    public PowerUp(double x, double y, Image image) {
        super(x, y, 32, 32, image);
    }

    @Override
    public boolean isOccupying() {
        return false;
    }

    @Override
    public void interact(Entity entity) {

        if (!collected && entity instanceof Player player) {
            applyEffect(player);
            collected = true;
        }
    }

    protected abstract void applyEffect(Player player);

    @Override
    public void update(double dt) {
        // Power-ups do not change over time.
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!collected) {
            gc.drawImage(image, x, y, width, height);
        }
    }

    public boolean isCollected() {
        return collected;
    }
}
