package bombastic.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class GameView extends BorderPane {

    public GameView(Runnable onBack) {

        // Blank game background
        setStyle("-fx-background-color: #1e1e1e;");

        setPadding(new Insets(20));

        // Return to main menu
        Button backButton = new Button("Main Menu");

        backButton.setOnAction(event -> onBack.run());

        setTop(backButton);
    }
}