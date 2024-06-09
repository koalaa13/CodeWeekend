package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.MoveUtils;
import org.example.model.Game;
import org.example.model.Monster;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.parser.impl.GameParser;
import org.example.solve.SolveFileWriter;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;
import org.example.solve.impl.CleverSolver;
import org.example.solve.impl.SimpleSolver;
import org.example.solve.impl.Solver50;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
                new SolveFileWriter(stest + "_ans.json").writeToFile(moves);
                System.out.println(test + " done, result = " + game.getGoldGained());
            }
            System.out.println("late " + late + " checked");
        }
    }
    // 228 - разница между группами
    // 62 разница между соседними в группе

//    10 35
//    20 36
//    30 44
//    40 47
//    50 all that can

    private void calc() throws IOException {
        GameParser parser = new GameParser();
        Game game = parser.parse("050.json");
//        Monster m = game.getField().getMonsters().getFirst();
//        System.out.println(MoveUtils.getPositionsToSaveShoot(game, m));
        Map<Long, Monster> monsters = new HashMap<>();
        for (int i = 0; i < game.getField().getMonsters().size(); ++i) {
            game.getField().getMonsters().get(i).setId(i);
            monsters.put((long) i, game.getField().getMonsters().get(i));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        var json = objectMapper.readTree(new File(Config.TEST_FOLDER + "/ans/050_ans.json"));
        json = json.get("moves");
        List<Move> moves = new ArrayList<>();
        for (var move : json) {
            if (move.get("type").asText().equals("move")) {
                moves.add(new TravelMove(move.get("target_x").asLong(), move.get("target_y").asLong()));
            } else {
                moves.add(new AttackMove(move.get("target_id").asInt()));
            }
        }
        Map<Long, Long> map = new TreeMap<>();
//        Comparator<Monster> goldComparator = (m1, m2) -> {
//            if (m1.getGold() != m2.getGold()) {
//                return Long.compare(m1.getGold(), m2.getGold());
//            }
//            return Long.compare(m1.getId(), m2.getId());
//        };
        List<Monster> attackedMonsters = moves.stream()
                .filter(m -> m.getType().equals("attack"))
                .map(m -> ((AttackMove) m).getTargetId())
                .map(id -> monsters.get((long) id))
                .toList();
        for (Monster m : attackedMonsters) {
            map.put(m.getRange(), map.getOrDefault(m.getRange(), 0L) + 1);
        }
        for (var e : map.entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }
    }

    public static void main(String[] args) throws IOException {
        Solver solver = new Solver50();
        new AnswerGenerator().generateTestAnswer(solver, 51);
    }
}