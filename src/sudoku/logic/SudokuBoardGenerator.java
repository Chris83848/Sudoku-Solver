package sudoku.logic;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// This class houses the methods needed to create different and distinct sudoku puzzles of varying difficulties.
public class SudokuBoardGenerator {

    // This method generates a unique, original, sudoku board puzzle of easy difficulty
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

    // This method generates a unique, original, sudoku board puzzle of medium difficulty
    // using a random form of medium symmetry.
    public static int[][] generateMediumBoardPuzzle() {
        int GRID_SIZE = 9;
        Random random = new Random();
        int[][] baseBoard = generateRandomSolvedBoard();

        int cellsToEmpty = random.nextInt(51, 62);
        int cellsEmptied = 0;

        // Shuffle cell order
        ArrayList<int[]> cells = new ArrayList<>();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                cells.add(new int[]{row, column});
            }
        }
        Collections.shuffle(cells);

        // Find which symmetry to use
        int randomNum = random.nextInt(1, 6);

        // Attempt to empty cells until enough are emptied
        for (int[] cell : cells) {
            if (cellsEmptied >= cellsToEmpty) {
                break;
            }

            // Find current cell coordinates
            int row = cell[0];
            int column = cell[1];
            if (baseBoard[row][column] == 0) {
                continue;
            }

            int symmetricalRow, symmetricalColumn, temp1, temp2;

            // Find symmetrical cell coordinates
            int[] symmetricalCoordinate = applyMediumSymmetry(row, column, randomNum);
            symmetricalRow = symmetricalCoordinate[0];
            symmetricalColumn = symmetricalCoordinate[1];

            // Create backup values in case emptied cells result in unsolvable puzzle
            temp1 = baseBoard[row][column];
            temp2 = baseBoard[symmetricalRow][symmetricalColumn];

            // Empty cells
            baseBoard[row][column] = 0;
            baseBoard[symmetricalRow][symmetricalColumn] = 0;

            // Test puzzle to ensure solvability and increment emptied cells counter if valid
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

    // Returns coordinates of symmetrical cell based on random number
    public static int[] applyEasySymmetry(int row, int column, int randomNum) {
        return switch (randomNum) {
            case 1 -> findRotationalCell(row, column);
            case 2 -> findVerticalReflectiveCell(row, column);
            default -> findHorizontalReflectiveCell(row, column);
        };
    }

    // Returns coordinates of symmetrical cell based on random number
    public static int[] applyMediumSymmetry(int row, int column, int randomNum) {
        return switch (randomNum) {
            case 1 -> findRotationalCell(row, column);
            case 2 -> findVerticalReflectiveCell(row, column);
            case 3 -> findHorizontalReflectiveCell(row, column);
            case 4 -> findMajorCrossReflectiveCell(row, column);
            default -> findMinorCrossReflectiveCell(row, column);
        };
    }

    // Returns coordinates of symmetrical cell based on random number
    public static int[] applyAdvancedSymmetry(int row, int column, int randomNum) {
        return switch (randomNum) {
            case 1 -> findRotationalCell(row, column);
            default -> findSpiralCell(row, column);
            //Hard symmetry: rotational, asymmetric, spiral
        };
    }

    // Returns coordinates of the rotational symmetric cell
    public static int[] findRotationalCell(int row, int column) {
        return new int[]{8 - row, 8 - column};
    }

    // Returns coordinates of the vertical reflective symmetric cell
    public static int[] findVerticalReflectiveCell(int row, int column) {
        return new int[]{row, 8 - column};
    }

    // Returns coordinates of the horizontal reflective symmetric cell
    public static int[] findHorizontalReflectiveCell(int row, int column) {
        return new int[]{8 - row, column};
    }

    // Returns coordinates of the major cross reflective symmetric cell
    public static int[] findMajorCrossReflectiveCell(int row, int column) {
        return new int[]{column, row};
    }

    // Returns coordinates of the minor cross reflective symmetric cell
    public static int[] findMinorCrossReflectiveCell(int row, int column) {
        return new int[]{8 - column, 8 - row};
    }

    // Returns coordinates of the spiral symmetric cell
    public static int[] findSpiralCell(int row, int column) {
        return new int[]{0, 0};
    }

    // Randomly generates and returns a solved sudoku board for puzzle creation
    public static int[][] generateRandomSolvedBoard() {
        int[][] emptyBoard = new int[9][9];
        randomRecursion(emptyBoard, 0, 0);
        return emptyBoard;
    }


    // Returns whether given puzzle has a unique solution
    public static boolean isUnique(int[][] sudokuBoard) {
        // Create indicator of uniqueness
        int[] numSolutions = {0};

        // Make a copy of puzzle
        int[][] board = new int[9][9];
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                board[x][y] = sudokuBoard[x][y];
            }
        }

        // Test puzzle for uniqueness by attempting to solve it multiple times
        helpTestUniqueness(board, 0, 0, numSolutions);

        // Return indicator
        return numSolutions[0] == 1;
    }


    // This method runs similar to the solveBoard method, but instead looks for more than one solution by
    // continuing to solve even after finding an initial solution.
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
                    // Update indicator to reflect found solution
                    solutions[0]++;
                    // If more than one solution exists, return immediately since there is no need to continue.
                    if (solutions[0] > 1) {
                        return sudokuBoard;
                    }
                    // Delete solution and continue as if no solution has been found.
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

    // Recursion that randomizes number placements to best fill sudoku boards.
    public static boolean randomRecursion(int[][] sudokuBoard, int i, int j) {
        if (j == 9) {
            j = 0;
            i++;
        }
        if (i == 9) {
            return true;
        }

        if (sudokuBoard[i][j] == 0) {

            // Create randomized list of digits to test
            List<Integer> digits = IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toList());
            Collections.shuffle(digits);

            // Test each digit
            for (int num : digits) {
                sudokuBoard[i][j] = num;
                if (!SudokuSolverApplication.checkBoardError(sudokuBoard, i, j)) {
                    if (randomRecursion(sudokuBoard, i, j + 1)) {
                        return true;
                    }
                    sudokuBoard[i][j] = 0;
                } else {
                    sudokuBoard[i][j] = 0;
                }
            }
            return false;
        } else {
            return randomRecursion(sudokuBoard, i, j + 1);
        }
    }
}
