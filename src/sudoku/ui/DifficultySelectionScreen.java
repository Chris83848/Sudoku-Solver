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

        // Create buttons for different difficulties
        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");
        Button backButton = new Button("Back");

        //
        easyButton.setOnAction(e -> {

        });

        //
        mediumButton.setOnAction(e -> {

        });

        // Call previous screen (home screen) when back button is clicked
        backButton.setOnAction(e -> {
            HomeScreen homeScreen = new HomeScreen();
            difficulties.setScene(homeScreen.getScene(difficulties));
            difficulties.setMaximized(true);
        });

        // Set layout and return screen
        VBox layout = new VBox(20, label, easyButton, mediumButton, hardButton, backButton);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 800, 600);
    }
}
