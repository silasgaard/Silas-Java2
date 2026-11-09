package bombastic.view;

import bombastic.model.AppModel;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenuView extends StackPane {

    public MainMenuView(
            AppModel model,
            Runnable onStart,
            Runnable onChat,
            Runnable onSettings,
            Runnable onExit
    ) {

        // Bind background to the shared model
        backgroundProperty().bind(model.backgroundProperty());

        // Game title
        Label title = new Label("BOMBASTIC");

        title.setStyle(
                "-fx-font-size: 42px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        // Main menu buttons
        Button startButton = createButton("Start Game", onStart);
        Button chatButton = createButton("Chat", onChat);
        Button settingsButton = createButton("Settings", onSettings);
        Button exitButton = createButton("Exit", onExit);

        // Arrange buttons vertically
        VBox menu = new VBox(
                15,
                title,
                startButton,
                chatButton,
                settingsButton,
                exitButton
        );

        menu.setAlignment(Pos.CENTER);
        menu.setMaxWidth(350);

        menu.setStyle(
                "-fx-background-color: rgba(0,0,0,0.55);" +
                "-fx-padding: 30;" +
                "-fx-background-radius: 12;"
        );

        // Add menu to the main view
        getChildren().add(menu);
    }

    // Helper method for creating buttons
    private Button createButton(String text, Runnable action) {

        Button button = new Button(text);

        button.setPrefWidth(220);
        button.setPrefHeight(42);

        button.setOnAction(event -> action.run());

        return button;
    }
}