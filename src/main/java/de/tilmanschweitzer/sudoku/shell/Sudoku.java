package de.tilmanschweitzer.sudoku.shell;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Sudoku {
    final int[][] sudoku = new int[9][9]; // int[row][col]

    private Sudoku(int[][] sudoku) {
        for (SudokuPosition position : SudokuPosition.allPositions) {
            setValueForPosition(position, sudoku[position.getRow()][position.getCol()]);
        }
    }

    public static Sudoku fromString(String sudokuAsString) throws SudokuFormatException {
        return new Sudoku(parseSudoku(sudokuAsString));
    }

    public static int[][] parseSudoku(String sudokuAsString) throws SudokuFormatException {
        final int[][] sudoku = new int[9][9];
        final List<Integer> valuesFromString = Arrays.stream(sudokuAsString.split("")).map(Integer::parseInt).collect(Collectors.toUnmodifiableList());
        if (valuesFromString.size() != SudokuPosition.allPositions.size()) {
            throw new SudokuFormatException();
        }
        for (SudokuPosition position : SudokuPosition.allPositions) {
            sudoku[position.getRow()][position.getCol()] = valuesFromString.get(position.getIndex());
        }

        return sudoku;
    }
    public int getValueForPosition(SudokuPosition position) {
        return sudoku[position.getRow()][position.getCol()];
    }

    public void setValueForPosition(SudokuPosition position, int value) {
        sudoku[position.getRow()][position.getCol()] = value;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer();

        for (SudokuPosition position : SudokuPosition.allPositions) {
            int value = getValueForPosition(position);
            final String valueAsString = value == 0 ? " " : value + "";
            if (position.getRow() % 3 == 0) {
                sb.append("| ");
            }
            sb.append(valueAsString + " ");
            if (position.getRow() == 8) {
                sb.append("|\n");
                if (position.getCol() % 3 == 2) {
                    sb.append("| - - - | - - - | - - - |\n");
                }
            }
        }

        return sb.toString();
    }

}
