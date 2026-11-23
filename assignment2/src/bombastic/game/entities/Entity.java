
package bombastic.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity {

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Image image;

    public Entity(double x, double y, double width, double height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    // Position getters
    public double getPositionX() {
        return x;
    }

    public double getPositionY() {
        return y;
    }

    // Size getters
    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    // Methods implemented by subclasses
    public abstract boolean isOccupying();

    public abstract void interact(Entity entity);

    public abstract void update(double dt);

    public abstract void render(GraphicsContext gc);
}
