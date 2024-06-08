package org.example;

import org.example.model.Game;
import org.example.model.move.Move;
import org.example.parser.impl.GameParser;
import org.example.solve.SolveFileWriter;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;
import org.example.solve.impl.CleverSolver;
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

    public static void main(String[] args) throws IOException {
        AnswerGenerator answerGenerator = new AnswerGenerator();
        Solver solver = new CleverSolver(new SolverConstants());
        answerGenerator.generateAllTestsAnswers(solver);
    }
}