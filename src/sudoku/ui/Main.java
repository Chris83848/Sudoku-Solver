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
    public void start(Stage home) {
        Button loadButton = new Button("Solve Own Puzzle");
        Button generateButton = new Button("Solve Random Puzzle");

        VBox layout = new VBox(20, loadButton, generateButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 400, 300);

        home.setScene(scene);
        home.setTitle("Sudoku Solver");
        home.setMaximized(true);
        home.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
