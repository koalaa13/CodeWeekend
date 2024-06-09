package org.example.solve.impl;

import org.example.core.CombatUtils;
import org.example.core.FatigueUtils;
import org.example.core.MoveUtils;
import org.example.geom.Position;
import org.example.model.Field;
import org.example.model.Game;
import org.example.model.Monster;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.scoring.BestForField;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;

import java.util.*;

public class FieldSolver extends Solver {
    public SolverConstants constants;

    public FieldSolver(SolverConstants constants) {
        this.constants = constants;
    }

    public Map<Position, List<Monster>> calcCloseMonsters(Field field, long range) {
        Map<Position, List<Monster>> res = new HashMap<>();
        for (Monster monster : field.getMonsters()) {
            for (long x = Math.max(0, monster.getX() - range); x <= Math.min(field.getWidth(), monster.getX() + range); x++) {
                for (long y = Math.max(0, monster.getY() - range); y <= Math.min(field.getHeight(), monster.getY() + range); y++) {
                    Position cur = new Position(x, y);
                    if (cur.distance(new Position(monster.getX(), monster.getY())) <= range * range) {
                        if (!res.containsKey(cur)) {
                            res.put(cur, new ArrayList<>());
                        }
                        res.get(cur).add(monster);
                    }
                }
            }
        }
        return res;
    }

    public void fillOptPoses(TreeMap<Long, Set<Position>> optPos, Map<Position, Long> fatigue, Field field) {
        for (long x = 0; x <= field.getWidth(); x++) {
            for (long y = 0; y <= field.getHeight(); y++) {
                Position curPos = new Position(x, y);
                long fatigueSum = fatigue.getOrDefault(curPos, 0L);
                if (!optPos.containsKey(fatigueSum)) {
                    optPos.put(fatigueSum, new HashSet<>());
                }
                optPos.get(fatigueSum).add(curPos);
            }
        }
    }

    @Override
    public List<Move> solve(Game game) {
        int movesLimit = (int) game.getNumTurns();
        List<Move> moves = new ArrayList<>();

        Map<Position, Long> fatigue = FatigueUtils.calcFatigueByPosition(game);
        TreeMap<Long, Set<Position>> optPos = new TreeMap<>();

        fillOptPoses(optPos, fatigue, game.getField());

        Map<Position, List<Monster>> closeMonsters = calcCloseMonsters(game.getField(), 45);

        BestForField provider = new BestForField(game.getField(), optPos, fatigue, closeMonsters, constants);

        for (int iter = 0; game.getNumTurns() > 0; iter++) {
            BestForField.BestPair bestPair = provider.getBest(game);
            if (bestPair.monsters == null) {
                break;
            }

            if (iter > 220) {
                int kek = 0;
            }

            List<TravelMove> travelMoves = MoveUtils.moveToPosition(game.getHero(), bestPair.position);
            moves.addAll(travelMoves);
            game.setNumTurns(game.getNumTurns() - travelMoves.size());
            game.setTurnsPassed(game.getTurnsPassed() + travelMoves.size());

            for (Monster monster : bestPair.monsters) {
                List<AttackMove> attackMoves = CombatUtils.killMonster(game.getHero(), monster);
                provider.eliminateMonstersFatigue(monster);
                moves.addAll(attackMoves);
                game.setNumTurns(game.getNumTurns() - attackMoves.size());
                game.setTurnsPassed(game.getTurnsPassed() + attackMoves.size());

                game.setGoldGained(game.getGoldGained() + monster.getGold());
            }

            /*if (game.getNumTurns() >= 0) {
                game.setGoldGained(game.getGoldGained() + bestPair.monster.getGold());
            }*/
            System.out.println(iter);
        }

        if (moves.size() > movesLimit) {
            moves = moves.subList(0, movesLimit);
        }
        return moves;
    }

    @Override
    public List<Move> doubleSolve(Game game1, Game game2) {
        return List.of();
    }
}
