package bombastic.controller;

import java.nio.file.Files;
import java.nio.file.Path;

import bombastic.model.AppModel;
import bombastic.view.MainMenuView;
import bombastic.view.SettingsView;
import bombastic.view.ChatView;
import bombastic.view.GameView;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AppController {

    private final AppModel model = new AppModel();

    private final StackPane root = new StackPane();

    private final MainMenuView mainMenuView;
    private final SettingsView settingsView;
    private final ChatView chatView;
    private final GameView gameView;

    public AppController(Stage stage) {

        // Set the default background
        model.setBackground(loadBackground("bg1.png"));

        // Create the views and connect their actions
        mainMenuView = new MainMenuView(
                model,
                this::showGame,
                this::showChat,
                this::showSettings,
                Platform::exit
        );

        settingsView = new SettingsView(
                model,
                this::selectBackground,
                this::selectArena,
                this::showMainMenu
        );

        chatView = new ChatView(
                model,
                this::sendMessage,
                this::showMainMenu
        );

        gameView = new GameView(this::showMainMenu);

        // Create the application window
        Scene scene = new Scene(root, 960, 600);

        stage.setTitle("Bombastic");
        stage.setScene(scene);

        showMainMenu();

        stage.show();
    }

    // Switch between views
    private void showView(Pane view) {
        root.getChildren().setAll(view);
    }

    private void showMainMenu() {
        showView(mainMenuView);
    }

    private void showSettings() {
        showView(settingsView);
    }

    private void showChat() {
        showView(chatView);
    }

    private void showGame() {
        showView(gameView);
    }

    // Settings actions
    private void selectBackground(String fileName) {
        model.setBackground(loadBackground(fileName));
    }

    private void selectArena(String arena) {
        model.setSelectedArena(arena);
    }

    // Chat action
    private void sendMessage(String message) {
        model.addChatMessage("You: " + message);
    }

    // Load background images
    private Background loadBackground(String fileName) {

        Path path = Path.of("src", "resources", fileName);

        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException(
                    "Missing background image: " + path.toAbsolutePath()
            );
        }

        Image image = new Image(path.toUri().toString());

        if (image.isError()) {
            throw new IllegalStateException(
                    "Could not load image: " + path,
                    image.getException()
            );
        }

        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        100, 100,
                        true, true,
                        false, true
                )
        );

        return new Background(backgroundImage);
    }
}