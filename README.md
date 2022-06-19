# sudoku-solver

## Executions times for 100 Sudokus

### BacktrackingSudokuSolver

Execution time statistics for 100 Sudokus

#### First implementation (git hash: 36bfaf7)

* Sum of execution times: 7815ms
* Average execution time: 78ms
* Median execution time: 23ms
* Min execution time: 2ms
* Max execution time: 745ms


#### Improve implementation with parallel streams (git hash: aecfb3f)

* Sum of execution times: 2142ms
* Average execution time: 21ms
* Median execution time: 8ms
* Min execution time: 1ms
* Max execution time: 196ms

### DeductiveSudokuSolver

#### First implementation (git hash: a7c8982)

Only solves 89 of 100 sudokus

* Sum of execution times: 41ms
* Average execution time: 0ms
* Median execution time: 0ms
* Min execution time: 0ms
* Max execution time: 6ms

#### Improved DeductiveSudokuSolver (git hash: )

Can solve 100 of 100 test sudokus

* Sum of execution times: 61ms
* Average execution time: 0ms
* Median execution time: 0ms
* Min execution time: 0ms
* Max execution time: 10ms

### DeductiveSudokuSolver (with BacktrackingSudokuSolver as fallback)

#### First implementation (git hash: a7c8982)

* Sum of execution times: 556ms
* Average execution time: 5ms
* Median execution time: 0ms
* Min execution time: 0ms
* Max execution time: 115ms
