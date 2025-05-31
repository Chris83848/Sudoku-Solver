package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoadPuzzleScreen {
    public Scene getScene(Stage load) {
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(0);
        board.setVgap(0);
        board.setStyle("-fx-background-color: white;");

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Label cell = new Label();
                cell.setPrefSize(60, 60);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-border-color: black;" +
                        "-fx-border-width: " + getBorderWidth(row, column) + ";" +
                        "-fx-font-size: 20;");

                board.add(cell, column, row);
            }
        }

        VBox layout = new VBox(20, board);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 800, 600);
    }

    private String getBorderWidth(int row, int column) {
        int top = (row % 3 == 0) ? 3 : 1;
        int right = (column == 8) ? 3 : 1;
        int bottom = (row == 8) ? 3 : 1;
        int left = (column % 3 == 0) ? 3 : 1;
        return top + " " + right + " " + bottom + " " + left;
    }
}