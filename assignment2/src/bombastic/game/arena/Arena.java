
package bombastic.game.arena;

import bombastic.game.entities.Entity;
import bombastic.game.entities.Floor;
import bombastic.game.entities.IndestructibleWall;
import bombastic.game.entities.DestructibleWall;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    public static final int TILE_SIZE = 32;
    public static final int COLUMNS = 25;
    public static final int ROWS = 15;

    public static final int WIDTH = COLUMNS * TILE_SIZE;
    public static final int HEIGHT = ROWS * TILE_SIZE;

    private final Floor[][] floors = new Floor[ROWS][COLUMNS];
    private final Entity[][] obstacles = new Entity[ROWS][COLUMNS];

    public Arena(String arenaName) {

        // Load the teacher's terrain images once.
        Image grassImage = loadTerrainImage("grass.png");
        Image wallImage = loadTerrainImage("wall.png");
        Image crateImage = loadTerrainImage("crate.png");

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {

                double x = col * TILE_SIZE;
                double y = row * TILE_SIZE;

                // Every tile contains grass.
                floors[row][col] = new Floor(
                        x, y, TILE_SIZE, grassImage
                );

                // Solid outer boundary.
                boolean boundary =
                        row == 0 ||
                        row == ROWS - 1 ||
                        col == 0 ||
                        col == COLUMNS - 1;

                // Traditional Bomberman maze pillars.
                boolean pillar =
                        row % 2 == 0 &&
                        col % 2 == 0;

                if (boundary || pillar) {
                    obstacles[row][col] = new IndestructibleWall(
                            x, y, TILE_SIZE, wallImage
                    );
                } else if (!isSpawnArea(row, col)
                        && shouldPlaceCrate(row, col, arenaName)) {

                    obstacles[row][col] = new DestructibleWall(
                            x, y, TILE_SIZE, crateImage
                    );
                }
            }
        }
    }

    // Keep space free near the players' starting positions.
    private boolean isSpawnArea(int row, int col) {

        boolean topLeft = row <= 2 && col <= 2;

        boolean bottomRight =
                row >= ROWS - 3 &&
                col >= COLUMNS - 3;

        return topLeft || bottomRight;
    }

    // Three deterministic layouts for the existing settings menu.
    private boolean shouldPlaceCrate(int row, int col, String arenaName) {

        return switch (arenaName) {
            case "Arena 2" -> (row * 5 + col * 3) % 7 < 3;
            case "Arena 3" -> (row * 2 + col * 7) % 9 < 4;
            default -> (row * 3 + col * 5) % 8 < 3;
        };
    }

    private Image loadTerrainImage(String fileName) {

        Path path = Path.of(
                "src", "resources", "assets", "terrain", fileName
        );

        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException(
                    "Missing terrain image: " + path.toAbsolutePath()
            );
        }

        Image image = new Image(path.toUri().toString());

        if (image.isError()) {
            throw new IllegalStateException(
                    "Could not load terrain image: " + path,
                    image.getException()
            );
        }

        return image;
    }

    public void render(GraphicsContext gc) {

        // Render the grass first.
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                floors[row][col].render(gc);
            }
        }

        // Render walls and crates over the grass.
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {

                Entity obstacle = obstacles[row][col];

                if (obstacle != null) {
                    obstacle.render(gc);
                }
            }
        }
    }

    public Entity getObstacleAt(int row, int col) {

        if (row < 0 || row >= ROWS ||
                col < 0 || col >= COLUMNS) {
            return null;
        }

        return obstacles[row][col];
    }

    public List<Entity> getObstacles() {

        List<Entity> result = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {

                Entity obstacle = obstacles[row][col];

                if (obstacle != null && obstacle.isOccupying()) {
                    result.add(obstacle);
                }
            }
        }

        return result;
    }

    public void removeDestructibleWall(int row, int col) {

        Entity obstacle = getObstacleAt(row, col);

        if (obstacle instanceof DestructibleWall) {
            obstacles[row][col] = null;
        }
    }
}
