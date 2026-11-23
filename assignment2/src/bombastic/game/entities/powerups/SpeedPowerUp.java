
package bombastic.game.entities.powerups;

import bombastic.game.entities.Player;
import javafx.scene.image.Image;

public class SpeedPowerUp extends PowerUp {

    public SpeedPowerUp(double x, double y, Image image) {
        super(x, y, image);
    }

    @Override
    protected void applyEffect(Player player) {
        player.increaseSpeed();
    }
}
