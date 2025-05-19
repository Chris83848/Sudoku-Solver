package sudoku.logic;
import java.util.*;

public class SudokuBoardGenerator {

    public static void main(String[] args) {
        int[][] testBoard = generateMediumBoardPuzzle();
        SudokuUtils.printBoard(testBoard);

    }

    //This method generates a unique, original, sudoku board puzzle of easy difficulty
    // using a random form of easy symmetry.
    public static int[][] generateEasyBoardPuzzle() {
        int GRID_SIZE = 9;
        Random random = new Random();
        int[][] baseBoard = generateRandomSolvedBoard();

        int cellsToEmpty = random.nextInt(41, 52);
        int cellsEmptied = 0;

        //Shuffle cell order
        ArrayList<int[]> cells = new ArrayList<>();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                cells.add(new int[]{row, column});
            }
        }
        Collections.shuffle(cells);

        //Find which symmetry to use
        int randomNum = random.nextInt(1, 4);

        //Attempt to empty cells until enough are emptied
        for (int[] cell : cells) {
            if (cellsEmptied >= cellsToEmpty) {
                break;
            }

            //Find current cell coordinates
            int row = cell[0];
            int column = cell[1];
            if (baseBoard[row][column] == 0) {
                continue;
            }

            int symmetricalRow, symmetricalColumn, temp1, temp2;

            //Find symmetrical cell coordinates
            int[] symmetricalCoordinate = applyEasySymmetry(row, column, randomNum);
            symmetricalRow = symmetricalCoordinate[0];
            symmetricalColumn = symmetricalCoordinate[1];

            //Create backup values in case emptied cells result in unsolvable puzzle
            temp1 = baseBoard[row][column];
            temp2 = baseBoard[symmetricalRow][symmetricalColumn];

            //Empty cells
            baseBoard[row][column] = 0;
            baseBoard[symmetricalRow][symmetricalColumn] = 0;

            //Test puzzle to ensure solvability and increment emptied cells counter if valid
            if (!isUnique(baseBoard) || !SudokuSolverApplication.boardHumanlySolvable(baseBoard, 1)) {
                baseBoard[row][column] = temp1;
                baseBoard[symmetricalRow][symmetricalColumn] = temp2;
            } else {
                if (row == symmetricalRow && column == symmetricalColumn) {
                    cellsEmptied++;
                } else {
                    cellsEmptied += 2;
                }
            }
        }
        return baseBoard;
    }


    public static int[][] generateMediumBoardPuzzle() {
        int GRID_SIZE = 9;
        Random random = new Random();
        int[][] baseBoard = generateRandomSolvedBoard();

        int cellsToEmpty = random.nextInt(51, 62);
        int cellsEmptied = 0;

        //Shuffle cell order
        ArrayList<int[]> cells = new ArrayList<>();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                cells.add(new int[]{row, column});
            }
        }
        Collections.shuffle(cells);

        //Find which symmetry to use
        int randomNum = random.nextInt(1, 6);

        //Attempt to empty cells until enough are emptied
        for (int[] cell : cells) {
            if (cellsEmptied >= cellsToEmpty) {
                break;
            }

            //Find current cell coordinates
            int row = cell[0];
            int column = cell[1];
            if (baseBoard[row][column] == 0) {
                continue;
            }

            int symmetricalRow, symmetricalColumn, temp1, temp2;

            //Find symmetrical cell coordinates
            int[] symmetricalCoordinate = applyMediumSymmetry(row, column, randomNum);
            symmetricalRow = symmetricalCoordinate[0];
            symmetricalColumn = symmetricalCoordinate[1];

            //Create backup values in case emptied cells result in unsolvable puzzle
            temp1 = baseBoard[row][column];
            temp2 = baseBoard[symmetricalRow][symmetricalColumn];

            //Empty cells
            baseBoard[row][column] = 0;
            baseBoard[symmetricalRow][symmetricalColumn] = 0;

            //Test puzzle to ensure solvability and increment emptied cells counter if valid
            if (!isUnique(baseBoard) || !SudokuSolverApplication.boardHumanlySolvable(baseBoard, 2)) {
                baseBoard[row][column] = temp1;
                baseBoard[symmetricalRow][symmetricalColumn] = temp2;
            } else {
                if (row == symmetricalRow && column == symmetricalColumn) {
                    cellsEmptied++;
                } else {
                    cellsEmptied += 2;
                }
            }
        }
        return baseBoard;
    }

    //Returns coordinates of symmetrical cell based on random number
    public static int[] applyEasySymmetry(int row, int column, int randomNum) {
        return switch (randomNum) {
            case 1 -> findRotationalCell(row, column);
            case 2 -> findVerticalReflectiveCell(row, column);
            default -> findHorizontalReflectiveCell(row, column);
        };
    }

    public static int[] applyMediumSymmetry(int row, int column, int randomNum) {
        return switch (randomNum) {
            case 1 -> findRotationalCell(row, column);
            case 2 -> findVerticalReflectiveCell(row, column);
            case 3 -> findHorizontalReflectiveCell(row, column);
            case 4 -> findMajorCrossReflectiveCell(row, column);
            default -> findMinorCrossReflectiveCell(row, column);
        };
    }

    public static int[] applyAdvancedSymmetry(int row, int column, int randomNum) {
        return switch (randomNum) {
            case 1 -> findRotationalCell(row, column);
            default -> findSpiralCell(row, column);
            //Hard symmetry: rotational, asymmetric, spiral
        };
    }

    public static int[] findRotationalCell(int row, int column) {
        return new int[]{8 - row, 8 - column};
    }

    public static int[] findVerticalReflectiveCell(int row, int column) {
        return new int[]{row, 8 - column};
    }

    public static int[] findHorizontalReflectiveCell(int row, int column) {
        return new int[]{8 - row, column};
    }

    public static int[] findMajorCrossReflectiveCell(int row, int column) {
        return new int[]{column, row};
    }

    public static int[] findMinorCrossReflectiveCell(int row, int column) {
        return new int[]{8 - column, 8 - row};
    }

    public static int[] findSpiralCell(int row, int column) {
        return new int[]{0, 0};
    }

    //Randomly generates a solved sudoku board
    public static int[][] generateRandomSolvedBoard() {
        int[][] emptyBoard = new int[9][9];

        ArrayList<Integer> topRow = new ArrayList<>();
        topRow.add(1);
        topRow.add(2);
        topRow.add(3);
        topRow.add(4);
        topRow.add(5);
        topRow.add(6);
        topRow.add(7);
        topRow.add(8);
        topRow.add(9);
        int length = topRow.size();

        //Fill out top row of board randomly
        for (int i = 0; i < 9; i++) {
            Random random = new Random();
            int randomIndex = random.nextInt(length);
            emptyBoard[0][i] = topRow.get(randomIndex);
            topRow.remove(randomIndex);
            length--;
        }

        //Solves rest of board based off of complete top board and returns it
        return SudokuSolverTechniques.solveRecursivelyHelper(emptyBoard, 1, 0);
    }


    //This method test if there is only one solution to the given sudoku puzzle.
    public static boolean isUnique(int[][] sudokuBoard) {
        int[] numSolutions = {0};
        int[][] board = new int[9][9];
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                board[x][y] = sudokuBoard[x][y];
            }
        }
        helpTestUniqueness(board, 0, 0, numSolutions);
        return numSolutions[0] == 1;
    }


    //This method runs similar to the solveBoard method, but instead looks for more than one solution.
    public static int[][] helpTestUniqueness(int[][] sudokuBoard, int i, int j, int[] solutions) {
        if (j == 9) {
            j = 0;
            i++;
        }

        if (sudokuBoard[i][j] == 0) {
            if (!SudokuSolverTechniquesHelpers.placeNumber(sudokuBoard, i, j)) {
                return sudokuBoard;
            } else {
                if (SudokuSolverApplication.boardComplete(sudokuBoard)) {
                    solutions[0]++;
                    if (solutions[0] > 1) {
                        return sudokuBoard;
                    }
                    sudokuBoard[i][j] = 0;
                    return sudokuBoard;
                } else {
                    int[][] currentBoard = helpTestUniqueness(sudokuBoard, i, j + 1, solutions);
                    while (!SudokuSolverApplication.boardComplete(currentBoard)) {
                        if (!SudokuSolverTechniquesHelpers.placeNumber(sudokuBoard, i, j)) {
                            return sudokuBoard;
                        } else {
                            currentBoard = helpTestUniqueness(sudokuBoard, i, j + 1, solutions);
                        }
                    }
                    return currentBoard;
                }
            }
        } else {
            return helpTestUniqueness(sudokuBoard, i, j + 1, solutions);
        }
    }


}
