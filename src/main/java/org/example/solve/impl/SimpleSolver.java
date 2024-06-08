package org.example.solve.impl;

import org.example.core.CombatUtils;
import org.example.core.MoveUtils;
import org.example.model.Game;
import org.example.model.Monster;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.scoring.ScoreProvider;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleSolver extends Solver {
    public SolverConstants constants;

    public SimpleSolver(SolverConstants constants) {
        this.constants = constants;
    }

    @Override
    public List<Move> solve(Game game) {
        int movesLimit = (int) game.getNumTurns();
        List<Move> moves = new ArrayList<>();
        ScoreProvider scoreProvider = new ScoreProvider(game, constants);
        for (int iter = 0; game.getNumTurns() > 0; iter++) {
            Monster optimalMonster = scoreProvider.findOptimalMonstersByScore();
            if (optimalMonster.isKilled()) {
                break;
            }
            scoreProvider.monsterRelativeScore(optimalMonster, game.getHero());
            List<TravelMove> travelMoves = MoveUtils.moveToShotRange(game.getHero(), optimalMonster);
            moves.addAll(travelMoves);
            game.setNumTurns(game.getNumTurns() - travelMoves.size());
            game.setTurnsPassed(game.getTurnsPassed() + travelMoves.size());

            List<AttackMove> attackMoves = CombatUtils.killMonster(game.getHero(), optimalMonster);
            moves.addAll(attackMoves);
            game.setNumTurns(game.getNumTurns() - attackMoves.size());
            game.setTurnsPassed(game.getTurnsPassed() + attackMoves.size());

            if (game.getNumTurns() >= 0) {
                game.setGoldGained(game.getGoldGained() + optimalMonster.getGold());
            }
            //System.out.println(iter);
        }

        if (moves.size() > movesLimit) {
            moves = moves.subList(0, movesLimit);
        }
        return moves;
    }

    @Override
    public List<Move> doubleSolve(Game game1, Game game2) {
        solve(game1);
        Set<Integer> vips = new HashSet<>();
        for (Monster monster : game1.getField().getMonsters()) {
            if (monster.isKilled()) {
                vips.add(monster.getId());
            }
        }
        for (Monster monster : game2.getField().getMonsters()) {
            if (vips.contains(monster.getId())) {
                monster.setVip(true);
            }
        }
        return solve(game2);
    }
}
