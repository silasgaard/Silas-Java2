
package bombastic.game;

import bombastic.game.entities.Entity;
import bombastic.game.entities.Explosion;
import bombastic.game.entities.powerups.PowerUp;

import java.util.ArrayList;
import java.util.List;

public final class CollisionDetection {

    private CollisionDetection() {
        // Utility class: no instances needed.
    }

    // Immutable rectangle containing only position and size.
    public record Bounds(
            double x, double y,
            double width, double height) {
    }

    // Snapshot of an explosion's affected rectangles.
    public record Blast(
            Explosion explosion,
            List<Bounds> tiles) {

        public Blast {
            tiles = List.copyOf(tiles);
        }
    }

    // Snapshot of a power-up's position.
    public record Pickup(
            PowerUp powerUp,
            Bounds bounds) {
    }

    // Collision results returned by a worker.
    public record Hits(
            List<Explosion> explosions,
            List<PowerUp> powerUps) {

        public Hits {
            explosions = List.copyOf(explosions);
            powerUps = List.copyOf(powerUps);
        }
    }

    // Called on the JavaFX thread to capture an entity's position.
    public static Bounds snapshot(Entity entity) {
        return new Bounds(
                entity.getPositionX(),
                entity.getPositionY(),
                entity.getWidth(),
                entity.getHeight()
        );
    }

    // Also used for immediate wall and bomb collision checks.
    public static boolean aabb(Entity a, Entity b) {
        return aabb(snapshot(a), snapshot(b));
    }

    // The teacher's AABB algorithm, using immutable rectangles.
    public static boolean aabb(Bounds a, Bounds b) {

        return a.x() + a.width() > b.x()
                && b.x() + b.width() > a.x()
                && a.y() + a.height() > b.y()
                && b.y() + b.height() > a.y();
    }

    // Executed by worker threads.
    // Detects collisions without changing any game state.
    public static Hits detect(
            Bounds player,
            List<Blast> blasts,
            List<Pickup> pickups) {

        List<Explosion> hitExplosions = new ArrayList<>();
        List<PowerUp> collectedPowerUps = new ArrayList<>();

        for (Blast blast : blasts) {

            for (Bounds tile : blast.tiles()) {

                if (aabb(player, tile)) {
                    hitExplosions.add(blast.explosion());
                    break;
                }
            }
        }

        for (Pickup pickup : pickups) {

            if (aabb(player, pickup.bounds())) {
                collectedPowerUps.add(pickup.powerUp());
            }
        }

        return new Hits(hitExplosions, collectedPowerUps);
    }
}
