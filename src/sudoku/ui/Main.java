package sudoku.ui;
import javafx.geometry.Pos;
import sudoku.logic.*;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primary) {
        primary.setTitle("Sudoku Solver");

        // Create home screen of program.
        HomeScreen homeScreen = new HomeScreen();

        // Call home screen and show it.
        primary.setScene(homeScreen.getScene(primary));
        primary.setMaximized(true);
        primary.show();
    }
    // Launch application.
    public static void main(String[] args) {
        launch();
    }
}
