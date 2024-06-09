package org.example.solve.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.core.CombatUtils;
import org.example.core.MoveUtils;
import org.example.geom.Position;
import org.example.model.Game;
import org.example.model.Monster;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.parser.impl.GameParser;
import org.example.scoring.ScoreProvider;
import org.example.solve.SolveFileWriter;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;

public class BruteSolver extends Solver {

    long width = 100;
    long height = 200;

    List<Move> bestMoves = new ArrayList<>();
    long bestScore = 687;
    long localBestScore = 0;

    double attackOrMoveChoice = 0.95;
    double shotRangeOrMoveChoice = 0.25;

    public void BRUTE(int turns, long currentScore, Game game, List<Move> moves) throws IOException {
        if (turns < 0) {
            return;
        }
//        System.out.println("Turns left: " + turns + " Score: " + currentScore);
        if (currentScore > bestScore) {
            System.out.println("New best score: " + currentScore);
            bestScore = currentScore;
            bestMoves = new ArrayList<>(moves);
            new SolveFileWriter().writeToFile(moves, "015" + "_ans.json");
        }
        if (currentScore > localBestScore) {
            localBestScore = currentScore;
            System.out.println("New local best score: " + currentScore);
            new SolveFileWriter().writeToFile(moves, "015" + "_ans.json");
        }
        if (turns == 0) {
            return;
        }
        var attackOrMove = Math.random() < attackOrMoveChoice;
        var hero = game.getHero();
        var monsters = game.getField().getMonsters().stream().filter(m -> m.canBeAttacked(hero.getX(), hero.getY(), hero.getRange()) && !m.isKilled()).toList();
        var canBeKilled = game.getField().getMonsters().stream().filter(m -> !m.isKilled()).toList();
        if (monsters.isEmpty()) {
            attackOrMove = false;
        }
        if (attackOrMove) {
            var randomMonster = monsters.get((int) (Math.random() * monsters.size()));
            var movesToKill = CombatUtils.killMonster(hero, randomMonster);
            moves.addAll(movesToKill.stream().toList());
            BRUTE((turns - movesToKill.size()), currentScore + randomMonster.getGold(), game, moves);
        } else {
            var shotRangeOrMove = Math.random() < shotRangeOrMoveChoice;
            List<TravelMove> movesToTravel;
            if (shotRangeOrMove) {
                var minX = Math.max(0, hero.getX() - hero.getSpeed());
                var maxX = Math.min(width, hero.getX() + hero.getSpeed());
                var minY = Math.max(0, hero.getY() - hero.getSpeed());
                var maxY = Math.min(height, hero.getY() + hero.getSpeed());
                var randomX = (long) (Math.random() * (maxX - minX) + minX);
                var randomY = (long) (Math.random() * (maxY - minY) + minY);
                movesToTravel = MoveUtils.moveToPosition(hero, new Position(randomX, randomY));
            } else {
                movesToTravel = MoveUtils.moveToShotRange(hero, canBeKilled.get((int) (Math.random() * canBeKilled.size())));
            }

            moves.addAll(movesToTravel.stream().toList());
            BRUTE((turns - movesToTravel.size()), currentScore, game, moves);
        }
    }

    @Override
    public List<Move> solve(Game game) {
//        int iterations = 0;
        while (true) {
            try {
                var savedGame = new GameParser().parse("002.json");
//                final int monsterCount = savedGame.getField().getMonsters().size();
//                int split = 5;
//                int newMonsterId = monsterCount + 1;
//                for (int i = 0; i <= split; i++) {
//                    for (int j = 0; j <= split; j++) {
//                        long x = savedGame.getField().getWidth() * i / split;
//                        long y = savedGame.getField().getHeight() * j / split;
//                        Monster monster = new Monster(x, y, 0, 1, 0, newMonsterId++, 0, 0);
//                        savedGame.getField().getMonsters().add(monster);
//                    }
//                }
                BRUTE((int) savedGame.getNumTurns(), 0, savedGame, new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Move> doubleSolve(Game game1, Game game2) {
        return List.of();
    }
}
