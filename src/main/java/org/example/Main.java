package org.example;

import org.example.model.Game;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.parser.Parser;
import org.example.parser.impl.GameParser;
import org.example.solve.SolveFileWriter;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;
import org.example.solve.impl.SimpleSolver;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Solver solver = new SimpleSolver(new SolverConstants());

        //new SolveFileWriter().writeToFile(solver.solve(new GameParser().parse("00" + 2 + ".json")), "00" + 2 + "_ans.json");

        for (int i = 1; i <= 9; i++) {
            Game game = new GameParser().parse("00" + i + ".json");
            new SolveFileWriter().writeToFile(solver.solve(game), "00" + i + "_ans.json");
            System.out.println(i + " done, result = " + game.getGoldGained());
        }
        for (int i = 10; i <= 25; i++) {
            Game game = new GameParser().parse("0" + i + ".json");
            new SolveFileWriter().writeToFile(solver.solve(game), "0" + i + "_ans.json");
            System.out.println(i + " done, result = " + game.getGoldGained());
        }
    }
}