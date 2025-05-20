package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DifficultySelectionScreen {

    public Scene getScene(Stage difficulties) {
        Label label = new Label("Select Difficulty:");

        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");
        Button backButton = new Button("Back");

        easyButton.setOnAction(e -> {

        });

        mediumButton.setOnAction(e -> {

        });

        backButton.setOnAction(e -> {
            HomeScreen homeScreen = new HomeScreen();
            difficulties.setScene(homeScreen.getScene(difficulties));
            difficulties.setMaximized(true);
        });

        VBox layout = new VBox(20, label, easyButton, mediumButton, hardButton, backButton);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 800, 600);
    }
}
