package org.example;

import org.example.model.Game;
import org.example.model.move.Move;
import org.example.parser.impl.GameParser;
import org.example.solve.SolveFileWriter;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;
import org.example.solve.impl.SimpleSolver;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void bruteVip(int test) throws IOException {
        Set<Double> have = new HashSet<>();
        long best = 0;
        for (int earlyExp = 0; earlyExp <= 5; earlyExp++) {
            for (int earlyGold = 0; earlyGold <= 5; earlyGold++) {
                double cur = -1.0;
                if (earlyGold > 0) {
                    cur = (double) earlyExp / (double) earlyGold;
                }
                if (have.contains(cur)) {
                    System.out.println(earlyExp + " " + earlyGold + " skipped");
                    continue;
                }
                have.add(cur);
                for (int late = 0; late <= 500; late += 25) {
                    SolverConstants constants = new SolverConstants();
                    constants.goldCoeff = earlyGold;
                    constants.expCoeff = earlyExp;
                    constants.lateStart = late;

                    // ----------------------------------------------------

                    Solver solver = new SimpleSolver(constants);

                    String stest = String.valueOf(test);
                    while (stest.length() != 3) {
                        stest = "0" + stest;
                    }
                    Game game1 = new GameParser().parse(stest + ".json");
                    Game game2 = new GameParser().parse(stest + ".json");
                    List<Move> moves = solver.doubleSolve(game1, game2);
                    if (game2.getGoldGained() > best) {
                        best = game2.getGoldGained();
                        new SolveFileWriter().writeToFile(moves, stest + "_ans.json");
                        System.out.println(test + " done, result = " + game2.getGoldGained());
                    }
                    if (late % 25 == 0) {
                        System.out.println("late " + late + " checked");
                    }
                }
                System.out.println(earlyExp + " " + earlyGold + " checked");
            }
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

    public static void experimentVip(int test) throws IOException {
        Solver solver = new SimpleSolver(new SolverConstants());

        String stest = String.valueOf(test);
        while (stest.length() != 3) {
            stest = "0" + stest;
        }
        Game game1 = new GameParser().parse(stest + ".json");
        Game game2 = new GameParser().parse(stest + ".json");
        new SolveFileWriter().writeToFile(solver.doubleSolve(game1, game2), stest + "_ans.json");
        System.out.println(test + " done, result = " + game2.getGoldGained());
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

    public static void brute(int test) throws IOException {
        Set<Double> have = new HashSet<>();
        long best = 0;
        for (int earlyExp = 0; earlyExp <= 5; earlyExp++) {
            for (int earlyGold = 0; earlyGold <= 5; earlyGold++) {
                double cur = -1.0;
                if (earlyGold > 0) {
                    cur = (double) earlyExp / (double) earlyGold;
                }
                if (have.contains(cur)) {
                    System.out.println(earlyExp + " " + earlyGold + " skipped");
                    continue;
                }
                have.add(cur);
                for (int late = 0; late <= 420; late += 10) {
                    SolverConstants constants = new SolverConstants();
                    constants.goldCoeff = earlyGold;
                    constants.expCoeff = earlyExp;
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
                    if (late % 1 == 0) {
                        System.out.println("late " + late + " checked");
                    }
                }
                System.out.println(earlyExp + " " + earlyGold + " checked");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //bruteVip(15);
        //brute(50);
        experiment(36);
        //experimentVip(15);
    }
}