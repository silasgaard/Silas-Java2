package bombastic.view;

import java.util.function.Consumer;

import bombastic.model.AppModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChatView extends BorderPane {

    public ChatView(
            AppModel model,
            Consumer<String> onSend,
            Runnable onBack
    ) {

        // Bind background to the shared model
        backgroundProperty().bind(model.backgroundProperty());

        setPadding(new Insets(25));

        // Chat title
        Label title = new Label("CHAT ROOM");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        setTop(title);

        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

        // Message display
        TextArea messages = new TextArea();

        messages.setEditable(false);
        messages.setWrapText(true);

        messages.textProperty().bind(
                model.chatMessagesProperty()
        );

        // Online users
        ListView<String> users = new ListView<>(
                model.getOnlineUsers()
        );

        users.setPrefWidth(180);
        users.setMinWidth(150);

        // Labels
        Label messagesLabel = new Label("Messages");
        Label usersLabel = new Label("Online Users");

        messagesLabel.setStyle("-fx-text-fill: white;");
        usersLabel.setStyle("-fx-text-fill: white;");

        // Message area layout
        VBox messageBox = new VBox(
                8,
                messagesLabel,
                messages
        );

        // Online users layout
        VBox userBox = new VBox(
                8,
                usersLabel,
                users
        );

        VBox.setVgrow(messages, Priority.ALWAYS);
        VBox.setVgrow(users, Priority.ALWAYS);

        // Main chat area
        HBox mainArea = new HBox(
                15,
                messageBox,
                userBox
        );

        HBox.setHgrow(messageBox, Priority.ALWAYS);

        setCenter(mainArea);

        // Message input
        TextArea input = new TextArea();

        input.setPromptText("Write a message...");
        input.setPrefRowCount(2);
        input.setWrapText(true);

        // Send button
        Button sendButton = new Button("Send");

        sendButton.setOnAction(event -> {

            String text = input.getText().trim();

            if (!text.isEmpty()) {
                onSend.accept(text);
                input.clear();
            }
        });

        // Main menu button
        Button backButton = new Button("Main Menu");

        backButton.setOnAction(event -> onBack.run());

        // Bottom layout
        HBox bottomArea = new HBox(
                10,
                input,
                sendButton,
                backButton
        );

        bottomArea.setAlignment(Pos.BOTTOM_CENTER);

        HBox.setHgrow(input, Priority.ALWAYS);

        setBottom(bottomArea);

        BorderPane.setMargin(
                bottomArea,
                new Insets(20, 0, 0, 0)
        );
    }
}