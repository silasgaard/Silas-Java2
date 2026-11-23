
package bombastic.game.entities.powerups;

import bombastic.game.entities.Player;
import javafx.scene.image.Image;

public class LifePowerUp extends PowerUp {

    public LifePowerUp(double x, double y, Image image) {
        super(x, y, image);
    }

    @Override
    protected void applyEffect(Player player) {
        player.addLife();
    }
}
