package de.tilmanschweitzer.sudoku.solver;

import de.tilmanschweitzer.sudoku.model.Sudoku;

public interface SudokuSolver {
    Sudoku solve(Sudoku sudoku);
}
