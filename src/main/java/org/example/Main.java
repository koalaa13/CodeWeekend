package org.example;

import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.solve.SolveFileWriter;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        SolveFileWriter writer = new SolveFileWriter();
        List<Move> moves = List.of(
                new AttackMove(0),
                new TravelMove(0, 0)
        );
        writer.writeToFile(moves, "001_ans.json");
    }
}