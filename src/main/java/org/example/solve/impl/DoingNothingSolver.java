package org.example.solve.impl;

import org.example.model.Game;
import org.example.model.move.Move;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;

import java.util.Collections;
import java.util.List;

public class DoingNothingSolver extends Solver {
    @Override
    public List<Move> solve(Game game) {
        return Collections.emptyList();
    }

    @Override
    public List<Move> doubleSolve(Game game1, Game game2) {
        return Collections.emptyList();
    }
}
