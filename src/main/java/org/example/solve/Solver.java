package org.example.solve;

import org.example.model.Game;
import org.example.model.move.Move;

import java.util.List;

public abstract class Solver {
    public List<Move> solve(Game game) {
        return solve(game, "-");
    }

    public List<Move> solve(Game game, String name) {
        return solve(game);
    }
}
