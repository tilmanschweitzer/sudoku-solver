package de.tilmanschweitzer.sudoku.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.*;

class SudokuPositionTest {

    @ParameterizedTest(name = "getRow returns {arguments} for given row")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8})
    public void getRow_returnsExpectedRow(int row) {
        assertEquals(row, SudokuPosition.of(row, 0).getRow());
        assertEquals(row, SudokuPosition.of(row, 8).getRow());
    }

    @ParameterizedTest(name = "getRow returns {arguments} for given col")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8})
    public void getBox_returnsExpectedRow(int col) {
        assertEquals(col, SudokuPosition.of( 0, col).getCol());
        assertEquals(col, SudokuPosition.of( 7, col).getCol());
    }

    @Test
    public void getBox_returnsExpectedBox() {
        // Check all four edges of box 0
        assertEquals(0, SudokuPosition.of( 0, 0).getBox());
        assertEquals(0, SudokuPosition.of( 0, 2).getBox());
        assertEquals(0, SudokuPosition.of( 2, 0).getBox());
        assertEquals(0, SudokuPosition.of( 2, 2).getBox());

        // Check all four edges of box 2
        assertEquals(2, SudokuPosition.of( 0, 6).getBox());
        assertEquals(2, SudokuPosition.of( 0, 8).getBox());
        assertEquals(2, SudokuPosition.of( 2, 6).getBox());
        assertEquals(2, SudokuPosition.of( 2, 8).getBox());

        // Check all four edges of box 6
        assertEquals(6, SudokuPosition.of( 6, 0).getBox());
        assertEquals(6, SudokuPosition.of( 6, 2).getBox());
        assertEquals(6, SudokuPosition.of( 8, 0).getBox());
        assertEquals(6, SudokuPosition.of( 8, 2).getBox());

        // Check all four edges of box 8
        assertEquals(8, SudokuPosition.of( 6, 6).getBox());
        assertEquals(8, SudokuPosition.of( 6, 8).getBox());
        assertEquals(8, SudokuPosition.of( 8, 6).getBox());
        assertEquals(8, SudokuPosition.of( 8, 8).getBox());

        // Check middle positions of all boxes
        assertEquals(0, SudokuPosition.of( 1, 1).getBox());
        assertEquals(1, SudokuPosition.of( 1, 4).getBox());
        assertEquals(2, SudokuPosition.of( 1, 7).getBox());
        assertEquals(3, SudokuPosition.of( 4, 1).getBox());
        assertEquals(4, SudokuPosition.of( 4, 4).getBox());
        assertEquals(5, SudokuPosition.of( 4, 7).getBox());
        assertEquals(6, SudokuPosition.of( 7, 1).getBox());
        assertEquals(7, SudokuPosition.of( 7, 4).getBox());
        assertEquals(8, SudokuPosition.of( 7, 7).getBox());
    }

    @Test
    public void getPositionsInSameRow_returnsListOfNinePositionsFromSameRow() {
        final SudokuPosition sudokuPosition = SudokuPosition.of(3, 7);

        final List<SudokuPosition> positionsInSameRow = sudokuPosition.getPositionsInSameRow();
        assertEquals(9, positionsInSameRow.size());
        assertEquals(List.of(3, 3, 3, 3, 3, 3, 3, 3, 3), positionsInSameRow.stream().map(SudokuPosition::getRow).collect(toUnmodifiableList()));
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), positionsInSameRow.stream().map(SudokuPosition::getCol).collect(toUnmodifiableList()));
    }

    @Test
    public void getPositionsInSameColumn_returnsListOfNinePositionsFromSameColumn() {
        final SudokuPosition sudokuPosition = SudokuPosition.of(3, 7);

        final List<SudokuPosition> positionsInSameRow = sudokuPosition.getPositionsInSameColumn();
        assertEquals(9, positionsInSameRow.size());
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), positionsInSameRow.stream().map(SudokuPosition::getRow).collect(toUnmodifiableList()));
        assertEquals(List.of(7, 7, 7, 7, 7, 7, 7, 7, 7), positionsInSameRow.stream().map(SudokuPosition::getCol).collect(toUnmodifiableList()));
    }

    @Test
    public void getPositionsInSameBox_returnsListOfNinePositionsFromSameBox() {
        final SudokuPosition sudokuPosition = SudokuPosition.of(3, 7);

        final List<SudokuPosition> positionsInSameRow = sudokuPosition.getPositionsInSameBox();
        assertEquals(9, positionsInSameRow.size());
        assertEquals(List.of(3, 3, 3, 4, 4, 4, 5, 5, 5), positionsInSameRow.stream().map(SudokuPosition::getRow).collect(toUnmodifiableList()));
        assertEquals(List.of(6, 7, 8, 6, 7, 8, 6, 7, 8), positionsInSameRow.stream().map(SudokuPosition::getCol).collect(toUnmodifiableList()));
    }
}
