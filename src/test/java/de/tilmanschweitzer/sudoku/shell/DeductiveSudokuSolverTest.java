package de.tilmanschweitzer.sudoku.shell;

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

    Sudoku unsolvedSudokuLevel2;
    Sudoku solvedSudokuLevel2;

    Sudoku unsolvedSudokuLevel3;
    Sudoku solvedSudokuLevel3;

    @BeforeEach
    public void setup() {
        unsolvedSudokuLevel1 = Sudoku.fromString("000003610000015007000008090086000700030800100500120309005060904060900530403701008");
        solvedSudokuLevel1 = Sudoku.fromString("728493615349615827651278493186539742932847156574126389815362974267984531493751268");

        unsolvedSudokuLevel2 = Sudoku.fromString("620740100070100052508000370067300900090000060800970031002000006000800000450002003");
        solvedSudokuLevel2 = Sudoku.fromString("623745198974138652518269374267381945391524867845976231782493516136857429459612783");

        unsolvedSudokuLevel3 = Sudoku.fromString("010060078000821004400500012000050460000206000706300080390000705000003120672005009");
        solvedSudokuLevel3 = Sudoku.fromString("215469378937821654468537912129758463843216597756394281391682745584973126672145839");
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
    public void solve_solvesTheSudokuLevel2() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuLevel2);

        assertThat(result, equalTo(solvedSudokuLevel2));
    }

    @Test
    public void solve_solvesTheSudokuLevel3() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuLevel3);

        assertThat(result, equalTo(solvedSudokuLevel3));
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
