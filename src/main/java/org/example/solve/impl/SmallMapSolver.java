package org.example.solve.impl;

import org.example.core.CombatUtils;
import org.example.geom.Position;
import org.example.model.Game;
import org.example.model.Hero;
import org.example.model.Monster;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.scoring.ScoreProvider;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;

import java.util.*;

public class SmallMapSolver extends Solver {
    private Map<Position, List<Monster>> nearestMonsters;
    private ScoreProvider scoreProvider;
    private static final long RANGE_DIFF = 10;

    private void putNearMonster(Position pos, List<Monster> allMonsters, long range) {
        nearestMonsters.put(pos,
                allMonsters.stream()
                        .filter(m -> {
                            Position monsterPos = new Position(m.getX(), m.getY());
                            return pos.distance(monsterPos) <= (range + RANGE_DIFF) * (range + RANGE_DIFF);
                        })
                        .toList()
        );
    }

    private boolean canAttackSomeone(Position pos, long heroRange, long x, long y) {
        return nearestMonsters.get(pos).stream().anyMatch(m -> canAttack(m, heroRange, x, y));
    }

    private boolean canAttack(Monster m, long heroRange, long x, long y) {
        Position monsterPos = new Position(m.getX(), m.getY());
        Position heroPos = new Position(x, y);
        return !m.isKilled() && monsterPos.distance(heroPos) <= heroRange * heroRange;
    }

    private List<AttackMove> attackAllNear(Position pos, Hero hero) {
        List<AttackMove> moves = new ArrayList<>();
        List<Monster> nearMonsters = new ArrayList<>(nearestMonsters.get(pos));
        nearMonsters.sort((m1, m2) ->
                Double.compare(
                        scoreProvider.monsterRelativeScore(m1, hero),
                        scoreProvider.monsterRelativeScore(m2, hero)
                ));
        for (Monster monster : nearMonsters) {
            if (!canAttack(monster, hero.getRange(), hero.getX(), hero.getY())) {
                continue;
            }
            moves.addAll(CombatUtils.killMonster(hero, monster));
        }

        return moves;
    }

    private double metricCalc(Monster monster, Hero hero, long x, long y) {
        if (!canAttack(monster, hero.getRange(), x, y)) {
            return 0.0;
        }
        return scoreProvider.monsterRelativeScore(monster, hero);
    }

    private boolean hasNonZeroMetric(double[][] metrics) {
        return Arrays.stream(metrics).anyMatch(row -> Arrays.stream(row).anyMatch(v -> v != 0.0));
    }

    @Override
    public List<Move> solve(Game game) {
        scoreProvider = new ScoreProvider(game, new SolverConstants());
        nearestMonsters = new HashMap<>();

        int movesLimit = (int) game.getNumTurns();
        List<Monster> monsters = game.getField().getMonsters();
        Hero hero = game.getHero();
        long range = hero.getRange();

        int width = (int) game.getField().getWidth();
        int height = (int) game.getField().getHeight();

        double[][] metrics = new double[width][height];
        List<Position> pointsOrder = new ArrayList<>();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pointsOrder.add(new Position(i, j));
                Position position = new Position(i, j);
                putNearMonster(position, monsters, range);

                int finalI = i;
                int finalJ = j;
                metrics[i][j] = nearestMonsters.get(new Position(i, j))
                        .stream()
                        .mapToDouble(m ->
                                metricCalc(m, hero, finalI, finalJ)
                        )
                        .sum();
                System.out.println("calculated for pos " + i + ' ' + j);
            }
        }
//        System.out.println(hasNonZeroMetric(metrics));
        pointsOrder.sort(Comparator.comparingDouble(p -> -metrics[(int) p.x][(int) p.y]));

        List<Move> moves = new ArrayList<>();
        while (moves.size() < game.getNumTurns()) {
//            System.out.println(hasNonZeroMetric(metrics));
            Position toMove = pointsOrder.getFirst();
            System.out.println("tried to move " + toMove.x + ' ' + toMove.y);
            // Если мы уже убили всех в округе такое может быть
            if (!canAttackSomeone(toMove, hero.getRange(), toMove.x, toMove.y)) {
                continue;
            }
//            System.out.println("went to point " + toMove.x + ' ' + toMove.y);
            // just go to point
            moves.add(new TravelMove(toMove.x, toMove.y));
            hero.setX(toMove.x);
            hero.setY(toMove.y);
            moves.addAll(attackAllNear(toMove, hero));

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
//                    System.out.println("updating metric for " + x + ' ' + y);
                    Position newPos = new Position(x, y);
                    if (nearestMonsters.get(newPos).isEmpty() || metrics[x][y] <= 0.0) {
                        continue;
                    }
                    int finalX = x;
                    int finalY = y;
                    metrics[x][y] = nearestMonsters.get(newPos)
                            .stream()
                            .mapToDouble(m -> metricCalc(m, hero, finalX, finalY))
                            .sum();
                }
            }
            pointsOrder.sort(Comparator.comparingDouble(p -> -metrics[(int) p.x][(int) p.y]));
        }

        if (moves.size() > movesLimit) {
            moves = moves.subList(0, movesLimit);
        }
        return moves;
    }
}
