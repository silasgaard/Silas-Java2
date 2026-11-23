
package bombastic.game;

import bombastic.game.arena.Arena;
import bombastic.game.entities.Bomb;
import bombastic.game.entities.DestructibleWall;
import bombastic.game.entities.Entity;
import bombastic.game.entities.Explosion;
import bombastic.game.entities.IndestructibleWall;
import bombastic.game.entities.Player;
import bombastic.game.entities.powerups.CapacityPowerUp;
import bombastic.game.entities.powerups.LifePowerUp;
import bombastic.game.entities.powerups.PowerUp;
import bombastic.game.entities.powerups.RangePowerUp;
import bombastic.game.entities.powerups.SpeedPowerUp;
import bombastic.view.GameView;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameController {

    private final GameView view;
    private final Arena arena;

    private final Player player1;
    private final Player player2;

    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    private final Image bombImage;
    private final Image explosionImage;
    private final Image capacityImage;
    private final Image rangeImage;
    private final Image speedImage;
    private final Image lifeImage;

    private final Random random = new Random(2026);

    private final EnumSet<KeyCode> pressedKeys =
            EnumSet.noneOf(KeyCode.class);

    // Two workers execute collision detection concurrently.
    private final ExecutorService collisionWorkers =
            Executors.newFixedThreadPool(2, task -> {
                Thread thread = new Thread(task, "Bombastic-collision");
                thread.setDaemon(true);
                return thread;
            });

    // Results are checked later without blocking the GUI thread.
    private Future<CollisionDetection.Hits> blueCollisionTask;
    private Future<CollisionDetection.Hits> redCollisionTask;

    private final AnimationTimer gameLoop;
    private boolean roundFinished = false;

    public GameController(GameView view, Arena arena) {

        this.view = view;
        this.arena = arena;

        bombImage = loadImage("bombs", "bomb_0.png");
        explosionImage = loadImage("explosions", "explosion_3.png");

        capacityImage = loadImage("powerups", "capacity.png");
        rangeImage = loadImage("powerups", "range.png");
        speedImage = loadImage("powerups", "speed.png");
        lifeImage = loadImage("powerups", "life.png");

        player1 = new Player(
                Arena.TILE_SIZE + 4,
                Arena.TILE_SIZE + 4,
                loadImage("players", "blue_down_0.png")
        );

        player2 = new Player(
                (Arena.COLUMNS - 2) * Arena.TILE_SIZE + 4,
                (Arena.ROWS - 2) * Arena.TILE_SIZE + 4,
                loadImage("players", "red_down_0.png")
        );

        initializeKeyBindings();

        gameLoop = new AnimationTimer() {

            private long lastTime = -1;

            @Override
            public void handle(long currentTime) {

                if (lastTime == -1) {
                    lastTime = currentTime;
                    return;
                }

                double dt =
                        (currentTime - lastTime) / 1_000_000_000.0;

                lastTime = currentTime;
                dt = Math.min(dt, 0.05);

                update(dt);
                render();
            }
        };
    }

    private Image loadImage(String folder, String fileName) {

        Path path = Path.of(
                "src", "resources", "assets", folder, fileName
        );

        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException(
                    "Missing game image: " + path.toAbsolutePath()
            );
        }

        Image image = new Image(path.toUri().toString());

        if (image.isError()) {
            throw new IllegalStateException(
                    "Could not load image: " + path,
                    image.getException()
            );
        }

        return image;
    }

    private void initializeKeyBindings() {

        Canvas canvas = view.getCanvas();
        canvas.setFocusTraversable(true);

        canvas.setOnKeyPressed(event -> {

            KeyCode key = event.getCode();
            boolean newlyPressed = pressedKeys.add(key);

            if (newlyPressed && !roundFinished) {

                if (key == KeyCode.SPACE) {
                    placeBomb(player1);
                } else if (key == KeyCode.ENTER) {
                    placeBomb(player2);
                }
            }

            event.consume();
        });

        canvas.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            event.consume();
        });
    }

    private void placeBomb(Player player) {

        if (player.isDead() || roundFinished) {
            return;
        }

        int activeBombs = 0;

        for (Bomb bomb : bombs) {
            if (bomb.getOwner() == player) {
                activeBombs++;
            }
        }

        if (activeBombs >= player.getMaxBombs()) {
            return;
        }

        int col = (int) (
                (player.getPositionX() + player.getWidth() / 2)
                        / Arena.TILE_SIZE
        );

        int row = (int) (
                (player.getPositionY() + player.getHeight() / 2)
                        / Arena.TILE_SIZE
        );

        if (arena.getObstacleAt(row, col) != null) {
            return;
        }

        for (Bomb bomb : bombs) {

            int bombCol =
                    (int) (bomb.getPositionX() / Arena.TILE_SIZE);

            int bombRow =
                    (int) (bomb.getPositionY() / Arena.TILE_SIZE);

            if (bombCol == col && bombRow == row) {
                return;
            }
        }

        bombs.add(new Bomb(
                col * Arena.TILE_SIZE,
                row * Arena.TILE_SIZE,
                player,
                bombImage
        ));
    }

    private void update(double dt) {

        if (roundFinished) {
            return;
        }

        // Apply results from completed worker tasks.
        processCompletedCollisionChecks();

        if (roundFinished) {
            return;
        }

        // Player movement stays on the JavaFX thread.
        movePlayer(
                player1,
                pressedKeys.contains(KeyCode.A),
                pressedKeys.contains(KeyCode.D),
                pressedKeys.contains(KeyCode.W),
                pressedKeys.contains(KeyCode.S),
                dt
        );

        movePlayer(
                player2,
                pressedKeys.contains(KeyCode.J),
                pressedKeys.contains(KeyCode.L),
                pressedKeys.contains(KeyCode.I),
                pressedKeys.contains(KeyCode.K),
                dt
        );

        for (Bomb bomb : bombs) {
            bomb.updateOwnerAccess();
            bomb.update(dt);
        }

        processBombs();

        // Schedule concurrent explosion and power-up collision checks.
        scheduleCollisionChecks();

        for (Explosion explosion : explosions) {
            explosion.update(dt);
        }

        explosions.removeIf(Explosion::isExpired);

        checkGameOver();
    }

    private void movePlayer(
            Player player,
            boolean left,
            boolean right,
            boolean up,
            boolean down,
            double dt) {

        double dx = (right ? 1 : 0) - (left ? 1 : 0);
        double dy = (down ? 1 : 0) - (up ? 1 : 0);

        double oldX = player.getPositionX();
        double oldY = player.getPositionY();

        player.setDirection(dx, dy);
        player.update(dt);

        double newX = player.getPositionX();
        double newY = player.getPositionY();

        // Resolve horizontal movement.
        player.setPosition(newX, oldY);

        if (isBlocked(player)) {
            player.setPosition(oldX, oldY);
        }

        double validX = player.getPositionX();

        // Resolve vertical movement.
        player.setPosition(validX, newY);

        if (isBlocked(player)) {
            player.setPosition(validX, oldY);
        }
    }

    private boolean isBlocked(Player player) {

        for (Entity obstacle : arena.getObstacles()) {

            if (obstacle.isOccupying()
                    && CollisionDetection.aabb(player, obstacle)) {
                return true;
            }
        }

        for (Bomb bomb : bombs) {

            if (CollisionDetection.aabb(player, bomb)
                    && !bomb.allowsOwnerToLeave(player)) {
                return true;
            }
        }

        Player other = player == player1 ? player2 : player1;

        return CollisionDetection.aabb(player, other);
    }

    // Submit two collision calculations to the worker pool.
    private void scheduleCollisionChecks() {

        // Never submit a new batch while one is unfinished.
        if (blueCollisionTask != null) {
            return;
        }

        if (explosions.isEmpty() && powerUps.isEmpty()) {
            return;
        }

        // Capture immutable positions on the JavaFX thread.
        var blueBounds = CollisionDetection.snapshot(player1);
        var redBounds = CollisionDetection.snapshot(player2);

        List<CollisionDetection.Blast> blastSnapshots =
                new ArrayList<>();

        for (Explosion explosion : explosions) {

            List<CollisionDetection.Bounds> tiles =
                    new ArrayList<>();

            for (Explosion.Cell cell : explosion.getCells()) {

                tiles.add(new CollisionDetection.Bounds(
                        cell.col() * Arena.TILE_SIZE,
                        cell.row() * Arena.TILE_SIZE,
                        Arena.TILE_SIZE,
                        Arena.TILE_SIZE
                ));
            }

            blastSnapshots.add(
                    new CollisionDetection.Blast(explosion, tiles)
            );
        }

        List<CollisionDetection.Pickup> pickupSnapshots =
                new ArrayList<>();

        for (PowerUp powerUp : powerUps) {

            pickupSnapshots.add(
                    new CollisionDetection.Pickup(
                            powerUp,
                            CollisionDetection.snapshot(powerUp)
                    )
            );
        }

        // Copy lists so workers never see them change.
        var blasts = List.copyOf(blastSnapshots);
        var pickups = List.copyOf(pickupSnapshots);

        blueCollisionTask = collisionWorkers.submit(
                () -> CollisionDetection.detect(
                        blueBounds, blasts, pickups
                )
        );

        redCollisionTask = collisionWorkers.submit(
                () -> CollisionDetection.detect(
                        redBounds, blasts, pickups
                )
        );
    }

    // Runs on the JavaFX thread, never on a worker.
    private void processCompletedCollisionChecks() {

        if (blueCollisionTask == null
                || redCollisionTask == null) {
            return;
        }

        // Never block the JavaFX thread waiting for workers.
        if (!blueCollisionTask.isDone()
                || !redCollisionTask.isDone()) {
            return;
        }

        try {
            var blueHits = blueCollisionTask.get();
            var redHits = redCollisionTask.get();

            blueCollisionTask = null;
            redCollisionTask = null;

            // All state-changing interactions occur here.
            applyCollisionResults(player1, blueHits);
            applyCollisionResults(player2, redHits);

            powerUps.removeIf(PowerUp::isCollected);

            checkGameOver();

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();
            System.err.println(
                    "Collision processing interrupted: " + exception
            );

            stop();

        } catch (ExecutionException exception) {

            System.err.println(
                    "Collision worker failed: " + exception.getCause()
            );

            blueCollisionTask = null;
            redCollisionTask = null;
        }
    }

    private void applyCollisionResults(
            Player player,
            CollisionDetection.Hits hits) {

        if (player.isDead()) {
            return;
        }

        // Damage first, then collect any surviving player's power-ups.
        for (Explosion explosion : hits.explosions()) {
            explosion.applyHit(player);
        }

        if (player.isDead()) {
            return;
        }

        for (PowerUp powerUp : hits.powerUps()) {

            if (powerUps.contains(powerUp)
                    && !powerUp.isCollected()) {
                powerUp.interact(player);
            }
        }
    }

    private void processBombs() {

        // The while loop also handles chain reactions.
        while (true) {

            Bomb triggeredBomb = null;

            for (Bomb bomb : bombs) {

                if (bomb.isReady()) {
                    triggeredBomb = bomb;
                    break;
                }
            }

            if (triggeredBomb == null) {
                break;
            }

            bombs.remove(triggeredBomb);

            Explosion explosion = createExplosion(triggeredBomb);
            explosions.add(explosion);

            for (Bomb other : bombs) {

                if (explosion.collidesWith(other)) {
                    other.interact(explosion);
                }
            }
        }
    }

    private Explosion createExplosion(Bomb bomb) {

        int centerCol =
                (int) (bomb.getPositionX() / Arena.TILE_SIZE);

        int centerRow =
                (int) (bomb.getPositionY() / Arena.TILE_SIZE);

        List<Explosion.Cell> cells = new ArrayList<>();

        cells.add(new Explosion.Cell(centerRow, centerCol));

        int[][] directions = {
                {0, 1},
                {0, -1},
                {1, 0},
                {-1, 0}
        };

        for (int[] direction : directions) {

            for (int distance = 1;
                    distance <= bomb.getRange();
                    distance++) {

                int row = centerRow + direction[0] * distance;
                int col = centerCol + direction[1] * distance;

                if (row < 0 || row >= Arena.ROWS
                        || col < 0 || col >= Arena.COLUMNS) {
                    break;
                }

                Entity obstacle = arena.getObstacleAt(row, col);

                if (obstacle instanceof IndestructibleWall) {
                    break;
                }

                cells.add(new Explosion.Cell(row, col));

                if (obstacle instanceof DestructibleWall) {
                    break;
                }
            }
        }

        Explosion explosion = new Explosion(cells, explosionImage);

        for (Explosion.Cell cell : cells) {

            Entity obstacle =
                    arena.getObstacleAt(cell.row(), cell.col());

            if (obstacle instanceof DestructibleWall) {

                obstacle.interact(explosion);

                arena.removeDestructibleWall(
                        cell.row(), cell.col()
                );

                spawnPowerUp(cell.row(), cell.col());
            }
        }

        return explosion;
    }

    private void spawnPowerUp(int row, int col) {

        if (random.nextDouble() >= 0.45) {
            return;
        }

        double x = col * Arena.TILE_SIZE;
        double y = row * Arena.TILE_SIZE;

        PowerUp powerUp = switch (random.nextInt(4)) {
            case 0 -> new CapacityPowerUp(x, y, capacityImage);
            case 1 -> new RangePowerUp(x, y, rangeImage);
            case 2 -> new SpeedPowerUp(x, y, speedImage);
            default -> new LifePowerUp(x, y, lifeImage);
        };

        powerUps.add(powerUp);
    }

    private void checkGameOver() {

        if (roundFinished) {
            return;
        }

        boolean blueDead = player1.isDead();
        boolean redDead = player2.isDead();

        if (!blueDead && !redDead) {
            return;
        }

        roundFinished = true;
        pressedKeys.clear();

        if (blueDead && redDead) {
            view.showResult("DRAW!");
        } else if (blueDead) {
            view.showResult("RED WINS!");
        } else {
            view.showResult("BLUE WINS!");
        }

        gameLoop.stop();
        collisionWorkers.shutdownNow();
    }

    private void render() {

        view.renderGame(
                arena,
                player1,
                player2,
                bombs,
                explosions,
                powerUps
        );
    }

    public void start() {

        pressedKeys.clear();
        roundFinished = false;

        view.showResult("");
        render();

        view.getCanvas().requestFocus();
        gameLoop.start();
    }

    public void stop() {

        gameLoop.stop();
        pressedKeys.clear();

        if (blueCollisionTask != null) {
            blueCollisionTask.cancel(true);
        }

        if (redCollisionTask != null) {
            redCollisionTask.cancel(true);
        }

        collisionWorkers.shutdownNow();

        Canvas canvas = view.getCanvas();

        canvas.setOnKeyPressed(null);
        canvas.setOnKeyReleased(null);
    }
}
