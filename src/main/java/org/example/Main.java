package org.example;

import org.example.model.Game;
import org.example.model.move.Move;
import org.example.parser.impl.GameParser;
import org.example.solve.SolveFileWriter;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;
import org.example.solve.impl.SimpleSolver;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void brute(int test) throws IOException {
        long best = 1831031;
        for (int late = 0; late <= 230; late++) {
            SolverConstants constants = new SolverConstants();
            constants.lateStart = late;

            // ----------------------------------------------------

            Solver solver = new SimpleSolver(constants);

            String stest = String.valueOf(test);
            while (stest.length() != 3) {
                stest = "0" + stest;
            }
            Game game = new GameParser().parse(stest + ".json");
            List<Move> moves = solver.solve(game);
            if (game.getGoldGained() > best) {
                best = game.getGoldGained();
                new SolveFileWriter().writeToFile(moves, stest + "_ans.json");
                System.out.println(test + " done, result = " + game.getGoldGained());
            }
            System.out.println("late " + late + " checked");
        }
    }

    public static void experiment(int test) throws IOException {
        Solver solver = new SimpleSolver(new SolverConstants());

        String stest = String.valueOf(test);
        while (stest.length() != 3) {
            stest = "0" + stest;
        }
        Game game = new GameParser().parse(stest + ".json");
        new SolveFileWriter().writeToFile(solver.solve(game), stest + "_ans.json");
        System.out.println(test + " done, result = " + game.getGoldGained());
    }

    public static void all() throws IOException {
        Solver solver = new SimpleSolver(new SolverConstants());

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

    public static void main(String[] args) throws IOException {
        //experiment(15);
        brute(15);
    }
}