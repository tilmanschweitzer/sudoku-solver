package de.tilmanschweitzer.sudoku.shell;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static de.tilmanschweitzer.sudoku.shell.SudokuPosition.allPositions;
import static java.util.stream.Collectors.toUnmodifiableList;

public class Sudoku {
    private final int[][] sudoku = new int[9][9]; // int[row][col]

    private Sudoku(int[][] sudoku) {
        for (SudokuPosition position : allPositions) {
            setValueForPosition(position, sudoku[position.getRow()][position.getCol()]);
        }
    }

    public static Sudoku fromString(String sudokuAsString) throws SudokuFormatException {
        return new Sudoku(parseSudoku(sudokuAsString));
    }

    public static Sudoku fromSudoku(Sudoku sudoku) {
        return new Sudoku(sudoku.sudoku);
    }

    public static Sudoku empty() {
        return new Sudoku(new int[9][9]);
    }

    public static int[][] parseSudoku(String sudokuAsString) throws SudokuFormatException {
        final int[][] sudoku = new int[9][9];
        final List<Integer> valuesFromString = Arrays.stream(sudokuAsString.split("")).map(Integer::parseInt).collect(toUnmodifiableList());
        if (valuesFromString.size() != allPositions.size()) {
            throw new SudokuFormatException();
        }
        for (SudokuPosition position : allPositions) {
            sudoku[position.getRow()][position.getCol()] = valuesFromString.get(position.getIndex());
        }

        return sudoku;
    }

    public boolean isValid() {
        return allRowsAreValid() && allColsAreValid() && allFieldsAreValid();
    }

    public int[][] getSudoku() {
        return Arrays.stream(sudoku).map(int[]::clone).toArray(int[][]::new);
    }

    public List<Integer> getRow(int row) {
        return Arrays.stream(sudoku[row]).boxed().collect(toUnmodifiableList());
    }
    public List<Integer> getCol(int col) {
        return IntStream.range(0, 9).map(row -> sudoku[row][col]).boxed().collect(toUnmodifiableList());
    }

    public List<Integer> getField(int field) {
        return IntStream.range(0, 9).map(index -> {
            final int row = (field / 3) * 3 + index / 3;
            final int col = (field % 3) * 3 + index % 3;
            return sudoku[row][col];
        }).boxed().collect(toUnmodifiableList());
    }

    private boolean hasDuplicateValues(List<Integer> values) {
        final List<Integer> validValues = values.stream().filter(Sudoku::isValidValue).collect(toUnmodifiableList());
        return validValues.size() > validValues.stream().distinct().count();
    }
    private boolean allRowsAreValid() {
        return IntStream.range(0, 9).boxed().filter(row -> hasDuplicateValues(getRow(row))).findAny().isEmpty();
    }
    private boolean allColsAreValid() {
        return IntStream.range(0, 9).boxed().filter(col -> hasDuplicateValues(getCol(col))).findAny().isEmpty();
    }
    private boolean allFieldsAreValid() {
        return IntStream.range(0, 9).boxed().filter(field -> hasDuplicateValues(getField(field))).findAny().isEmpty();
    }

    private static boolean isValidValue(int value) {
        return value >= 1 && value <= 9;
    }

    public boolean isCompleted() {
        return allPositions.size() == allPositions.stream().map(this::getValueForPosition).filter(Sudoku::isValidValue).count();
    }

    public int getValueForPosition(SudokuPosition position) {
        return sudoku[position.getRow()][position.getCol()];
    }

    public void setValueForPosition(SudokuPosition position, int value) {
        sudoku[position.getRow()][position.getCol()] = value;
    }

    public String toPrintableString(String horizontalFieldDelimiter, String spacer) {
        final StringBuffer sb = new StringBuffer();

        for (SudokuPosition position : allPositions) {
            int value = getValueForPosition(position);
            final String valueAsString = value == 0 ? " " : value + "";
            if (position.getCol() % 3 == 0 && !horizontalFieldDelimiter.isBlank()) {
                sb.append(horizontalFieldDelimiter).append(spacer);
            }
            sb.append(valueAsString).append(spacer);
            if (position.getCol() == 8) {
                sb.append(horizontalFieldDelimiter).append("\n");
                if (position.getRow() % 3 == 2) {
                    sb.append("| - - - | - - - | - - - |\n");
                }
            }
        }

        return sb.toString();
    }

    public String toString() {
        return toPrintableString("|", " ");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sudoku)) {
            return false;
        }
        final Sudoku other = (Sudoku) obj;
        return allPositions.stream()
                .filter((position) -> getValueForPosition(position) != other.getValueForPosition(position))
                .findFirst()
                .isEmpty();
    }
}
