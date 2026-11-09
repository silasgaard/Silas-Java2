import bombastic.controller.AppController;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        new AppController(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}