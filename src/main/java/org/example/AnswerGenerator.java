package org.example;

import org.example.model.Game;
import org.example.model.move.Move;
import org.example.parser.impl.GameParser;
import org.example.solve.SolveFileWriter;
import org.example.solve.Solver;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AnswerGenerator {

    public void generateAllTestsAnswers(Solver solver, boolean onlyNew) throws IOException {
        File dir = new File(Config.TEST_FOLDER);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            System.out.println("Calculating answer for " + f.getName() + " (" + (i + 1) + "/" + files.length + ")");
            if (f.isDirectory()) {
                continue;
            }
            String tId = f.getName().split("\\.")[0];
            if (Integer.parseInt(tId) < 26 && onlyNew) {
                continue;
            }
            String filename = tId + "_ans.json";
            generateTestAnswer(solver, f, filename);
        }
    }

    private String getId(int testId) {
        String id = Integer.toString(testId);
        while (id.length() < 3) {
            id = "0" + id;
        }
        return id;
    }

    private String getTestFilename(int testId) {
        return getId(testId) + ".json";
    }

    private String getDefaultAnswerFilename(int testId) {
        return getId(testId) + "_ans.json";
    }

    public void generateTestAnswer(Solver solver, int testId) throws IOException {
        generateTestAnswer(
                solver,
                new File(Config.TEST_FOLDER + getTestFilename(testId)),
                getDefaultAnswerFilename(testId)
        );
    }

    public void generateTestAnswer(Solver solver, File file, String answerFilename) throws IOException {
        GameParser gameParser = new GameParser();
        Game game = gameParser.parse(file);
        List<Move> moves = solver.solve(game);
        SolveFileWriter solveFileWriter = new SolveFileWriter();
        solveFileWriter.writeToFile(moves, "ans/" + answerFilename);
    }
}