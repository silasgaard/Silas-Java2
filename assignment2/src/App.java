
import bombastic.controller.AppController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        // Create the application controller.
        AppController controller = new AppController();

        // The application owns the window and scene.
        Scene scene = new Scene(controller.getRoot(), 960, 600);

        stage.setTitle("Bombastic");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
