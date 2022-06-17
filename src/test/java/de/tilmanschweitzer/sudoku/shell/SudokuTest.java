package de.tilmanschweitzer.sudoku.shell;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SudokuTest {

    Sudoku testSudoku;

    @BeforeEach
    public void setup() {
        testSudoku = Sudoku.fromString("679518243543729618821634957794352186358461729216897534485276391962183475137945862");
    }

    @Test
    public void parseSudoku_parsesStringToIntArray() {
        final int[][] sudoku = Sudoku.parseSudoku("679518243543729618821634957794352186358461729216897534485276391962183475137945862");

        assertThat(sudoku, Matchers.equalTo(testSudoku.getSudoku()));
    }

    @Test
    public void parseSudoku_throwsExceptionIfInputIsNotExactly81NumbersLong() {
        final String stringWith100Chars = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
        assertThrows(SudokuFormatException.class, () -> Sudoku.parseSudoku(stringWith100Chars.substring(0, 80)));
        assertThrows(SudokuFormatException.class, () -> Sudoku.parseSudoku(stringWith100Chars.substring(0, 82)));
    }

    @Test
    public void getRow_returnsExpectedRows() {
        assertEquals(List.of(6, 7, 9, 5, 1, 8, 2, 4, 3), testSudoku.getRow(0));
        assertEquals(List.of(5, 4, 3, 7, 2, 9, 6, 1, 8), testSudoku.getRow(1));
        assertEquals(List.of(8, 2, 1, 6, 3, 4, 9, 5, 7), testSudoku.getRow(2));
        assertEquals(List.of(7, 9, 4, 3, 5, 2, 1, 8, 6), testSudoku.getRow(3));
        assertEquals(List.of(3, 5, 8, 4, 6, 1, 7, 2, 9), testSudoku.getRow(4));
        assertEquals(List.of(2, 1, 6, 8, 9, 7, 5, 3, 4), testSudoku.getRow(5));
        assertEquals(List.of(4, 8, 5, 2, 7, 6, 3, 9, 1), testSudoku.getRow(6));
        assertEquals(List.of(9, 6, 2, 1, 8, 3, 4, 7, 5), testSudoku.getRow(7));
        assertEquals(List.of(1, 3, 7, 9, 4, 5, 8, 6, 2), testSudoku.getRow(8));
    }

    @Test
    public void getCol_returnsExpectedCols() {
        assertEquals(List.of(6, 5, 8, 7, 3, 2, 4, 9, 1), testSudoku.getCol(0));
        assertEquals(List.of(7, 4, 2, 9, 5, 1, 8, 6, 3), testSudoku.getCol(1));
        assertEquals(List.of(9, 3, 1, 4, 8, 6, 5, 2, 7), testSudoku.getCol(2));
        assertEquals(List.of(5, 7, 6, 3, 4, 8, 2, 1, 9), testSudoku.getCol(3));
        assertEquals(List.of(1, 2, 3, 5, 6, 9, 7, 8, 4), testSudoku.getCol(4));
        assertEquals(List.of(8, 9, 4, 2, 1, 7, 6, 3, 5), testSudoku.getCol(5));
        assertEquals(List.of(2, 6, 9, 1, 7, 5, 3, 4, 8), testSudoku.getCol(6));
        assertEquals(List.of(4, 1, 5, 8, 2, 3, 9, 7, 6), testSudoku.getCol(7));
        assertEquals(List.of(3, 8, 7, 6, 9, 4, 1, 5, 2), testSudoku.getCol(8));
    }

    @Test
    public void getField_returnsExpectedFields() {
        assertEquals(List.of(6, 7, 9, 5, 4, 3, 8, 2, 1), testSudoku.getField(0));
        assertEquals(List.of(5, 1, 8, 7, 2, 9, 6, 3, 4), testSudoku.getField(1));
        assertEquals(List.of(2, 4, 3, 6, 1, 8, 9, 5, 7), testSudoku.getField(2));
        assertEquals(List.of(7, 9, 4, 3, 5, 8, 2, 1, 6), testSudoku.getField(3));
        assertEquals(List.of(3, 5, 2, 4, 6, 1, 8, 9, 7), testSudoku.getField(4));
        assertEquals(List.of(1, 8, 6, 7, 2, 9, 5, 3, 4), testSudoku.getField(5));
        assertEquals(List.of(4, 8, 5, 9, 6, 2, 1, 3, 7), testSudoku.getField(6));
        assertEquals(List.of(2, 7, 6, 1, 8, 3, 9, 4, 5), testSudoku.getField(7));
        assertEquals(List.of(3, 9, 1, 4, 7, 5, 8, 6, 2), testSudoku.getField(8));
    }

    @Test
    public void isValid_testSudokuIsValid() {
        assertTrue(testSudoku.isValid());
    }

    @Test
    public void isValid_emptySudokuIsValid() {
        final Sudoku emptySudoku = Sudoku.empty();
        assertTrue(emptySudoku.isValid());
    }

    @Test
    public void isValid_detectedDuplicateValueInRow() {
        final Sudoku emptySudoku = Sudoku.empty();
        emptySudoku.setValueForPosition(SudokuPosition.of(0, 0 ), 1);
        emptySudoku.setValueForPosition(SudokuPosition.of(0, 3 ), 1);
        assertFalse(emptySudoku.isValid());
    }

    @Test
    public void isValid_detectedDuplicateValueInCol() {
        final Sudoku emptySudoku = Sudoku.empty();
        emptySudoku.setValueForPosition(SudokuPosition.of(1, 8 ), 3);
        emptySudoku.setValueForPosition(SudokuPosition.of(8, 8 ), 3);
        assertFalse(emptySudoku.isValid());
    }

    @Test
    public void isValid_detectedDuplicateValueInField() {
        final Sudoku emptySudoku = Sudoku.empty();
        emptySudoku.setValueForPosition(SudokuPosition.of(7, 8 ), 7);
        emptySudoku.setValueForPosition(SudokuPosition.of(8, 7 ), 7);
        assertFalse(emptySudoku.isValid());
    }

    @Test
    public void isCompleted_testSudokuIsCompleted() {
        assertTrue(testSudoku.isCompleted());
    }

    @Test
    public void isCompleted_testSudokuIsNotCompletedIfOneValueIsResetted() {
        testSudoku.setValueForPosition(SudokuPosition.of(8, 8), 0);
        assertFalse(testSudoku.isCompleted());
    }

    @Test
    public void isValid_emptySudokuIsNotCompleted() {
        final Sudoku emptySudoku = Sudoku.empty();
        assertFalse(emptySudoku.isCompleted());
    }

    @Test
    public void toString_generatesPrintableVersionOfTheSudoku() {
        final String expectedString = "| 6 7 9 | 5 1 8 | 2 4 3 |\n" +
                "| 5 4 3 | 7 2 9 | 6 1 8 |\n" +
                "| 8 2 1 | 6 3 4 | 9 5 7 |\n" +
                "| - - - | - - - | - - - |\n" +
                "| 7 9 4 | 3 5 2 | 1 8 6 |\n" +
                "| 3 5 8 | 4 6 1 | 7 2 9 |\n" +
                "| 2 1 6 | 8 9 7 | 5 3 4 |\n" +
                "| - - - | - - - | - - - |\n" +
                "| 4 8 5 | 2 7 6 | 3 9 1 |\n" +
                "| 9 6 2 | 1 8 3 | 4 7 5 |\n" +
                "| 1 3 7 | 9 4 5 | 8 6 2 |\n" +
                "| - - - | - - - | - - - |\n";

        assertEquals(expectedString, testSudoku.toString());
    }

    @Test
    public void equals_emptySudokuEqualsOtherEmptySudoku() {
        assertTrue(Sudoku.empty().equals(Sudoku.empty()));
    }

    @Test
    public void equals_testSudokuEqualsClone() {
        final Sudoku clone = Sudoku.fromSudoku(testSudoku);
        assertTrue(testSudoku.equals(clone));
    }

    @Test
    public void equals_testSudokuNotEqualsModifiedClone() {
        final Sudoku clone = Sudoku.fromSudoku(testSudoku);
        clone.setValueForPosition(SudokuPosition.of(0, 0), 1);
        assertFalse(testSudoku.equals(clone));
    }

    @Test
    public void equals_testSudokuNotEqualsOtherClass() {
        assertFalse(testSudoku.equals(new Object()));
    }
}
