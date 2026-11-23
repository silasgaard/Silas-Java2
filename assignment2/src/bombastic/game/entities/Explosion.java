
package bombastic.game.entities;

import bombastic.game.arena.Arena;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class Explosion extends Entity {

    public record Cell(int row, int col) {
    }

    private final List<Cell> cells;

    private final Set<Player> damagedPlayers =
            Collections.newSetFromMap(new IdentityHashMap<>());

    private double remainingTime = 0.45;

    public Explosion(List<Cell> cells, Image image) {

        super(
                cells.get(0).col() * Arena.TILE_SIZE,
                cells.get(0).row() * Arena.TILE_SIZE,
                Arena.TILE_SIZE,
                Arena.TILE_SIZE,
                image
        );

        this.cells = List.copyOf(cells);
    }

    @Override
    public boolean isOccupying() {
        return false;
    }

    @Override
    public void interact(Entity entity) {

        if (entity instanceof Player player
                && collidesWith(player)) {

            applyHit(player);
        }
    }

    // Called on the JavaFX thread after concurrent detection.
    public void applyHit(Player player) {

        // Each explosion damages a player only once.
        if (damagedPlayers.add(player)) {
            player.takeDamage();
        }
    }

    public List<Cell> getCells() {
        return cells;
    }

    @Override
    public void update(double dt) {
        remainingTime -= dt;
    }

    public boolean isExpired() {
        return remainingTime <= 0;
    }

    public boolean collidesWith(Entity entity) {

        for (Cell cell : cells) {

            double tileX = cell.col() * Arena.TILE_SIZE;
            double tileY = cell.row() * Arena.TILE_SIZE;

            boolean overlapping =
                    tileX < entity.getPositionX() + entity.getWidth()
                    && tileX + Arena.TILE_SIZE > entity.getPositionX()
                    && tileY < entity.getPositionY() + entity.getHeight()
                    && tileY + Arena.TILE_SIZE > entity.getPositionY();

            if (overlapping) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void render(GraphicsContext gc) {

        for (Cell cell : cells) {

            gc.drawImage(
                    image,
                    cell.col() * Arena.TILE_SIZE,
                    cell.row() * Arena.TILE_SIZE,
                    Arena.TILE_SIZE,
                    Arena.TILE_SIZE
            );
        }
    }
}
