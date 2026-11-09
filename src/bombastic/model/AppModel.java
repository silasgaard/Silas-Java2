package bombastic.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Background;

public class AppModel {

    // Shared background for all menus
    private final ObjectProperty<Background> background =
            new SimpleObjectProperty<>();

    // Selected arena
    private final StringProperty selectedArena =
            new SimpleStringProperty("Arena 1");

    // Chat messages
    private final StringProperty chatMessages =
            new SimpleStringProperty("");

    // Mock users in the chat
    private final ObservableList<String> onlineUsers =
            FXCollections.observableArrayList(
                    "Silas",
                    "Ervin",
                    "Nicolai"
            );

    // Background
    public ObjectProperty<Background> backgroundProperty() {
        return background;
    }

    public void setBackground(Background newBackground) {
        background.set(newBackground);
    }

    // Arena
    public StringProperty selectedArenaProperty() {
        return selectedArena;
    }

    public String getSelectedArena() {
        return selectedArena.get();
    }

    public void setSelectedArena(String arena) {
        selectedArena.set(arena);
    }

    // Chat
    public StringProperty chatMessagesProperty() {
        return chatMessages;
    }

    public void addChatMessage(String message) {
        chatMessages.set(chatMessages.get() + message + "\n");
    }

    public ObservableList<String> getOnlineUsers() {
        return onlineUsers;
    }
}