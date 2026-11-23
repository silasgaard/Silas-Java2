
package bombastic.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bomb extends Entity {

    private final Player owner;
    private final int range;
    private double timer = 2.5;
    private boolean ownerCanLeave = true;

    public Bomb(double x, double y, Player owner, Image image) {
        super(x, y, 32, 32, image);
        this.owner = owner;
        this.range = owner.getBombRange();
    }

    @Override
    public boolean isOccupying() {
        return true;
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof Explosion) {
            timer = 0;
        }
    }

    @Override
    public void update(double dt) {
        timer -= dt;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    public boolean isReady() {
        return timer <= 0;
    }

    public Player getOwner() {
        return owner;
    }

    public int getRange() {
        return range;
    }

    public boolean allowsOwnerToLeave(Player player) {
        return player == owner && ownerCanLeave;
    }

    public void updateOwnerAccess() {
        boolean overlapping =
                owner.getPositionX() < x + width &&
                owner.getPositionX() + owner.getWidth() > x &&
                owner.getPositionY() < y + height &&
                owner.getPositionY() + owner.getHeight() > y;

        if (!overlapping) {
            ownerCanLeave = false;
        }
    }
}
