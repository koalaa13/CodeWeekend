package org.example.solve;

import org.example.model.Game;
import org.example.model.move.Move;

import java.util.List;

public abstract class Solver {
    public abstract List<Move> solve(Game game);
}