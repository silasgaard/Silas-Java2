
package bombastic.view;

import java.util.function.Consumer;

import bombastic.model.AppModel;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsView extends StackPane {

    public SettingsView(
            AppModel model,
            Consumer<String> onBackgroundSelected,
            Consumer<String> onArenaSelected,
            Runnable onBack
    ) {

        // Keep the background synchronized with the model.
        backgroundProperty().bind(model.backgroundProperty());

        // Title and description.
        Label title = new Label("SETTINGS");
        title.setStyle(
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label description = new Label(
                "Customize your Bombastic experience"
        );
        description.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #dddddd;"
        );

        // Background selection.
        Menu backgroundMenu = new Menu("Background 1 ▾");

        for (int i = 1; i <= 3; i++) {

            final int number = i;
            String fileName = "bg" + i + ".png";

            MenuItem item = new MenuItem("Background " + i);

            item.setOnAction(event -> {
                onBackgroundSelected.accept(fileName);
                backgroundMenu.setText("Background " + number + " ▾");
            });

            backgroundMenu.getItems().add(item);
        }

        MenuBar backgroundBar = createMenuBar(backgroundMenu);

        // Arena selection.
        Menu arenaMenu = new Menu(model.getSelectedArena() + " ▾");

        for (int i = 1; i <= 3; i++) {

            String arenaName = "Arena " + i;

            MenuItem item = new MenuItem(arenaName);

            item.setOnAction(event -> {
                onArenaSelected.accept(arenaName);
                arenaMenu.setText(arenaName + " ▾");
            });

            arenaMenu.getItems().add(item);
        }

        MenuBar arenaBar = createMenuBar(arenaMenu);

        // Labels.
        Label backgroundLabel = new Label("Background");
        Label arenaLabel = new Label("Arena");

        String labelStyle =
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;";

        backgroundLabel.setStyle(labelStyle);
        arenaLabel.setStyle(labelStyle);

        // Align settings in two columns.
        GridPane options = new GridPane();

        options.setAlignment(Pos.CENTER);
        options.setHgap(25);
        options.setVgap(20);
        options.setPadding(new Insets(20));

        options.setStyle(
                "-fx-background-color: rgba(15,23,42,0.80);" +
                "-fx-background-radius: 10;"
        );

        options.add(backgroundLabel, 0, 0);
        options.add(backgroundBar, 1, 0);
        options.add(arenaLabel, 0, 1);
        options.add(arenaBar, 1, 1);

        // Show currently selected arena.
        Label selectedArenaLabel = new Label();

        selectedArenaLabel.textProperty().bind(
                Bindings.concat(
                        "Selected arena: ",
                        model.selectedArenaProperty()
                )
        );

        selectedArenaLabel.setStyle(
                "-fx-text-fill: #dddddd;" +
                "-fx-font-size: 14px;"
        );

        // Back button.
        Button backButton = new Button("Main Menu");

        backButton.setPrefWidth(200);
        backButton.setPrefHeight(40);

        backButton.setOnAction(event -> onBack.run());

        // Main settings layout.
        VBox layout = new VBox(
                20,
                title,
                description,
                options,
                selectedArenaLabel,
                backButton
        );

        layout.setAlignment(Pos.CENTER);
        layout.setMaxWidth(520);
        layout.setPadding(new Insets(30));

        layout.setStyle(
                "-fx-background-color: rgba(0,0,0,0.55);" +
                "-fx-background-radius: 14;"
        );

        getChildren().add(layout);
    }

    // Creates a compact styled JavaFX menu bar.
    private MenuBar createMenuBar(Menu menu) {

        MenuBar menuBar = new MenuBar(menu);

        menuBar.setUseSystemMenuBar(false);

        // Prevent the menu bar from stretching horizontally.
        menuBar.setMinWidth(Region.USE_PREF_SIZE);
        menuBar.setMaxWidth(Region.USE_PREF_SIZE);

        menuBar.setStyle(
                "-fx-background-color: #f1f3f5;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 6;" +
                "-fx-padding: 4 8 4 8;"
        );

        return menuBar;
    }
}
