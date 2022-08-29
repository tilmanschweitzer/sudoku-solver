package de.tilmanschweitzer.sudoku.solver;

import de.tilmanschweitzer.sudoku.model.Sudoku;
import de.tilmanschweitzer.sudoku.model.SudokuPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class BacktrackingSudokuSolverTest {
    final BacktrackingSudokuSolver sudokuSolver = new BacktrackingSudokuSolver();

    Sudoku unsolvedSudokuA;
    Sudoku solvedSudokuA;

    Sudoku unsolvedSudokuB;
    Sudoku solvedSudokuB;

    @BeforeEach
    public void setup() {
        unsolvedSudokuA = Sudoku.fromString("000003610000015007000008090086000700030800100500120309005060904060900530403701008");
        solvedSudokuA = Sudoku.fromString("728493615349615827651278493186539742932847156574126389815362974267984531493751268");

        unsolvedSudokuB = Sudoku.fromString("070000043040009610800634900094052000358460020000800530080070091902100005007040802");
        solvedSudokuB = Sudoku.fromString("679518243543729618821634957794352186358461729216897534485276391962183475137945862");
    }

    @Test
    public void solve_doesNotChangeTheInitialSudoku() {
        final Sudoku copy = Sudoku.fromSudoku(unsolvedSudokuA);

        sudokuSolver.solve(unsolvedSudokuA);

        assertThat(unsolvedSudokuA, equalTo(copy));
    }

    @Test
    public void solve_solvesTheSudokuA() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuA);

        assertThat(result, equalTo(solvedSudokuA));
    }

    @Test
    public void solve_solvesTheSudokuB() {
        final Sudoku result = sudokuSolver.solve(unsolvedSudokuB);

        assertThat(result, equalTo(solvedSudokuB));
    }


    @Test
    public void solve_throwsRuntimeExceptionIfTheSudokuIsNotSolvable() {
        unsolvedSudokuA.setValueForPosition(SudokuPosition.of(0,0), 9);

        assertThrows(RuntimeException.class, () -> {
            sudokuSolver.solve(unsolvedSudokuA);
        });
    }

    @Test
    public void solve_throwsRuntimeExceptionIfTheSudokuHasMultipleSolutions() {
        unsolvedSudokuA.setValueForPosition(SudokuPosition.of(0,5), 0);

        assertThrows(RuntimeException.class, () -> {
            sudokuSolver.solve(unsolvedSudokuA);
        });
    }

    @Test
    public void findSolutions_findsNoSolutionsForUnsolvableSudoku() {
        unsolvedSudokuA.setValueForPosition(SudokuPosition.of(0,0), 9);

        final List<Sudoku> solutions = sudokuSolver.findSolutions(unsolvedSudokuA);

        assertThat(solutions.size(), is(0));
    }

    @Test
    public void findSolutions_findsFoundSolutionsForAmbiguousSudoku() {
        unsolvedSudokuA.setValueForPosition(SudokuPosition.of(0,5), 0);

        final List<Sudoku> solutions = sudokuSolver.findSolutions(unsolvedSudokuA);

        assertThat(solutions.size(), is(4));
    }
}
