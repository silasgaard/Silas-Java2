package bombastic.view;

import java.util.function.Consumer;

import bombastic.model.AppModel;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsView extends StackPane {

    public SettingsView(
            AppModel model,
            Consumer<String> onBackgroundSelected,
            Consumer<String> onArenaSelected,
            Runnable onBack
    ) {

        // Bind background to the shared model
        backgroundProperty().bind(model.backgroundProperty());

        // Settings title
        Label title = new Label("SETTINGS");

        title.setStyle(
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        // Background selection menu
        Menu backgroundMenu = new Menu("Background");

        String[] backgrounds = {
                "bg1.png",
                "bg2.png",
                "bg3.png"
        };

        for (String background : backgrounds) {

            MenuItem item = new MenuItem(background);

            item.setOnAction(event ->
                    onBackgroundSelected.accept(background)
            );

            backgroundMenu.getItems().add(item);
        }

        // Arena selection menu
        Menu arenaMenu = new Menu("Arena");

        String[] arenas = {
                "Arena 1",
                "Arena 2",
                "Arena 3"
        };

        for (String arena : arenas) {

            MenuItem item = new MenuItem(arena);

            item.setOnAction(event ->
                    onArenaSelected.accept(arena)
            );

            arenaMenu.getItems().add(item);
        }

        // Combine both menus
        MenuBar menuBar = new MenuBar(
                backgroundMenu,
                arenaMenu
        );

        // Display currently selected arena
        Label selectedArenaLabel = new Label();

        selectedArenaLabel.textProperty().bind(
                Bindings.concat(
                        "Selected arena: ",
                        model.selectedArenaProperty()
                )
        );

        selectedArenaLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;"
        );

        // Return to main menu
        Button backButton = new Button("Main Menu");

        backButton.setOnAction(event -> onBack.run());

        // Arrange the GUI elements
        VBox layout = new VBox(
                25,
                title,
                menuBar,
                selectedArenaLabel,
                backButton
        );

        layout.setAlignment(Pos.CENTER);
        layout.setMaxWidth(400);

        layout.setStyle(
                "-fx-background-color: rgba(0,0,0,0.55);" +
                "-fx-padding: 30;" +
                "-fx-background-radius: 12;"
        );

        getChildren().add(layout);
    }
}