
package bombastic.view;

import bombastic.game.arena.Arena;
import bombastic.game.entities.Bomb;
import bombastic.game.entities.Explosion;
import bombastic.game.entities.Player;
import bombastic.game.entities.powerups.PowerUp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class GameView extends BorderPane {

    private final Canvas canvas;

    private final Label livesLabel = new Label();
    private final Label statsLabel = new Label();
    private final Label resultLabel = new Label();

    public GameView(Runnable onBack) {

        setStyle("-fx-background-color: #1e1e1e;");
        setPadding(new Insets(20));

        Button backButton = new Button("Main Menu");
        backButton.setOnAction(event -> onBack.run());

        Label controls = new Label(
                "Blue: WASD + Space   |   Red: IJKL + Enter"
        );

        controls.setStyle(
                "-fx-text-fill: white; -fx-font-size: 13px;"
        );

        livesLabel.setStyle(
                "-fx-text-fill: white; -fx-font-size: 13px;"
        );

        statsLabel.setStyle(
                "-fx-text-fill: #dddddd; -fx-font-size: 12px;"
        );

        resultLabel.setStyle(
                "-fx-text-fill: #ffd54f;" +
                "-fx-font-size: 18px; -fx-font-weight: bold;"
        );

        HBox topBar = new HBox(
                18, backButton, controls, livesLabel
        );

        topBar.setAlignment(Pos.CENTER_LEFT);
        setTop(topBar);

        canvas = new Canvas(Arena.WIDTH, Arena.HEIGHT);
        canvas.setFocusTraversable(true);

        StackPane gameArea = new StackPane(canvas);
        setCenter(gameArea);

        VBox bottomBar = new VBox(4, statsLabel, resultLabel);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(4));

        setBottom(bottomBar);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void showResult(String message) {
        resultLabel.setText(message);
    }

    public void renderGame(
            Arena arena,
            Player player1,
            Player player2,
            List<Bomb> bombs,
            List<Explosion> explosions,
            List<PowerUp> powerUps) {

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(
                0, 0,
                canvas.getWidth(),
                canvas.getHeight()
        );

        arena.render(gc);

        for (PowerUp powerUp : powerUps) {
            powerUp.render(gc);
        }

        for (Bomb bomb : bombs) {
            bomb.render(gc);
        }

        player1.render(gc);
        player2.render(gc);

        for (Explosion explosion : explosions) {
            explosion.render(gc);
        }

        livesLabel.setText(
                "Blue: " + player1.getLives() +
                " lives  |  Red: " + player2.getLives() + " lives"
        );

        statsLabel.setText(
                "BLUE   Bombs: " + player1.getMaxBombs() +
                "  Range: " + player1.getBombRange() +
                "  Speed: " + (int) player1.getSpeed() +
                "       |       RED   Bombs: " + player2.getMaxBombs() +
                "  Range: " + player2.getBombRange() +
                "  Speed: " + (int) player2.getSpeed()
        );
    }
}
