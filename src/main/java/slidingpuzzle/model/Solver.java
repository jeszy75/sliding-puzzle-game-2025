package slidingpuzzle.model;

import puzzle.solver.BreadthFirstSearch;

/**
 * Solves the puzzle and prints the solution on the console.
 */
public class Solver {

    public static void main(String[] args) {
        final var bfs = new BreadthFirstSearch<Direction>();
        bfs.solveAndPrintSolution(new PuzzleState());
    }

}
