package de.tilmanschweitzer.sudoku.shell;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SudokuPosition {
    public static List<SudokuPosition> allPositions = IntStream.range(0, 81).boxed().map(SudokuPosition::new).collect(Collectors.toUnmodifiableList());
    private final int index;

    private SudokuPosition(int index) {
        this.index = index;
    }

    public static SudokuPosition of(int row, int col) {
        return new SudokuPosition(row * 9 + col);
    }

    public int getIndex() {
        return index;
    }

    public int getRow() {
        return index / 9;
    }

    public int getCol() {
        return index % 9;
    }
}
