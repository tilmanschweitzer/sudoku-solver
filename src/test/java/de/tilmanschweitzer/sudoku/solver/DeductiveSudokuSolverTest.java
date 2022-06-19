package de.tilmanschweitzer.sudoku.solver;

import de.tilmanschweitzer.sudoku.model.Sudoku;
import de.tilmanschweitzer.sudoku.model.SudokuPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeductiveSudokuSolverTest {
    final DeductiveSudokuSolver sudokuSolver = DeductiveSudokuSolver.createWithFallbackSolver(new BacktrackingSudokuSolver());

    Sudoku unsolvedSudokuLevel1;
    Sudoku solvedSudokuLevel1;

    Sudoku unsolvedSudokuLevel2Rows;
    Sudoku solvedSudokuLevel2Rows;

    Sudoku unsolvedSudokuLevel2Columns;
    Sudoku solvedSudokuLevel2Columns;

    Sudoku unsolvedSudokuLevel2Boxes;
    Sudoku solvedSudokuLevel2Boxes;

    Sudoku unsolvedSudokuLevel3NakedPair;
    Sudoku solvedSudokuLevel3NakedPair;

    Sudoku unsolvedSudokuLevel3XYWing;
    Sudoku solvedSudokuLevel3XYWing;

    @BeforeEach
    public void setup() {
        unsolvedSudokuLevel1 = Sudoku.fromString("000003610000015007000008090086000700030800100500120309005060904060900530403701008");
        solvedSudokuLevel1 = Sudoku.fromString("728493615349615827651278493186539742932847156574126389815362974267984531493751268");

        // Needs to check for unique value in rows to be solved
        unsolvedSudokuLevel2Rows = Sudoku.fromString("620740100070100052508000370067300900090000060800970031002000006000800000450002003");
        solvedSudokuLevel2Rows = Sudoku.fromString("623745198974138652518269374267381945391524867845976231782493516136857429459612783");

        // Needs to check for unique value in columns to be solved
        unsolvedSudokuLevel2Columns = Sudoku.fromString("050600041003045600960800300600000100007008204402000003029500036006024510000000802");
        solvedSudokuLevel2Columns = Sudoku.fromString("258693741173245698964817325695432187317958264482176953729581436836724519541369872");

        // Needs to check for unique value in boxes to be solved
        unsolvedSudokuLevel2Boxes = Sudoku.fromString("090060085180002369360900000050040008000009004074200050000736590700000000509000073");
        solvedSudokuLevel2Boxes = Sudoku.fromString("497361285185472369362958417953647128621589734874213956248736591736195842519824673");

        // Naked pair
        unsolvedSudokuLevel3NakedPair = Sudoku.fromString("600090103500008400000200090000006005000000930020070608050302019098400000207059004");
        solvedSudokuLevel3NakedPair = Sudoku.fromString("642795183519638427873241596384916275765824931921573648456382719198467352237159864");

        unsolvedSudokuLevel3XYWing = Sudoku.fromString("010060078000821004400500012000050460000206000706300080390000705000003120672005009");
        solvedSudokuLevel3XYWing = Sudoku.fromString("215469378937821654468537912129758463843216597756394281391682745584973126672145839");
    }

    @Test
    public void solve_doesNotChangeTheInitialSudoku() {
        final Sudoku copy = Sudoku.fromSudoku(unsolvedSudokuLevel1);

        sudokuSolver.solve(unsolvedSudokuLevel1);

        assertThat(unsolvedSudokuLevel1, equalTo(copy));
    }

    @Test
    public void solve_solvesTheSudokuLevel1() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuLevel1);
        assertThat(result, equalTo(solvedSudokuLevel1));
    }

    @Test
    public void solve_solvesTheSudokuLevel2Rows() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuLevel2Rows);
        assertThat(result, equalTo(solvedSudokuLevel2Rows));
    }

    @Test
    public void solve_solvesTheSudokuLevel2Columns() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuLevel2Columns);
        assertThat(result, equalTo(solvedSudokuLevel2Columns));
    }

    @Test
    public void solve_solvesTheSudokuLevel2Boxes() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuLevel2Boxes);
        assertThat(result, equalTo(solvedSudokuLevel2Boxes));
    }

    @Test
    public void solve_solvesTheSudokuLevel3NakedPair() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuLevel3NakedPair);
        assertThat(result, equalTo(solvedSudokuLevel3NakedPair));
    }

    @Test
    public void solve_solvesTheSudokuLevel3XYWing() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuLevel3XYWing);
        assertThat(result, equalTo(solvedSudokuLevel3XYWing));
    }

    @Test
    public void solve_throwsRuntimeExceptionIfTheSudokuIsNotSolvable() {
        unsolvedSudokuLevel1.setValueForPosition(SudokuPosition.of(0,0), 9);

        assertThrows(RuntimeException.class, () -> {
            sudokuSolver.solve(unsolvedSudokuLevel1);
        });
    }

    @Test
    public void solve_throwsRuntimeExceptionIfTheSudokuHasMultipleSolutions() {
        unsolvedSudokuLevel1.setValueForPosition(SudokuPosition.of(0,5), 0);

        assertThrows(RuntimeException.class, () -> {
            sudokuSolver.solve(unsolvedSudokuLevel1);
        });
    }

}
