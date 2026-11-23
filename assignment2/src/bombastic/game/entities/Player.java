
package bombastic.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player extends Entity {

    private int lives = 3;
    private int maxBombs = 1;
    private int bombRange = 2;
    private double speed = 110;

    private double directionX;
    private double directionY;

    public Player(double x, double y, Image image) {
        super(x, y, 24, 24, image);
    }

    public void setDirection(double dx, double dy) {
        directionX = dx;
        directionY = dy;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(double dt) {
        double length = Math.sqrt(
                directionX * directionX +
                directionY * directionY
        );

        if (length > 0) {
            x += (directionX / length) * speed * dt;
            y += (directionY / length) * speed * dt;
        }
    }

    @Override
    public boolean isOccupying() {
        return !isDead();
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof Explosion explosion) {
            explosion.interact(this);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDead()) {
            gc.drawImage(image, x - 4, y - 4, 32, 32);
        }
    }

    public int getLives() {
        return lives;
    }

    public double getSpeed() {
        return speed;
    }

    public int getMaxBombs() {
        return maxBombs;
    }

    public int getBombRange() {
        return bombRange;
    }

    public boolean isDead() {
        return lives <= 0;
    }

    public void takeDamage() {
        if (lives > 0) {
            lives--;
        }
    }

    public void addLife() {
        lives++;
    }

    public void increaseSpeed() {
        speed += 25;
    }

    public void increaseBombCapacity() {
        maxBombs++;
    }

    public void increaseBombRange() {
        bombRange++;
    }
}
