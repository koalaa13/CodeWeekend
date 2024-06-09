package org.example.solve.impl;

import org.example.core.CombatUtils;
import org.example.core.MoveUtils;
import org.example.geom.Position;
import org.example.model.Game;
import org.example.model.Hero;
import org.example.model.Monster;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.solve.Solver;

import java.util.*;

public class Solver50 extends Solver {
    private Map<Monster, List<Position>> safePositionsToKill;
    private Map<Long, TreeSet<Monster>> monsterGroups;
    // range -> count
    private Map<Long, Integer> monsterGroupSizes = Map.of(
            10L, 34,
            20L, 36,
            30L, 48,
            40L, 49,
            50L, 49
    );

    private void buildMonsterGroups(Game game) {
        monsterGroups = new HashMap<>();
        for (Monster monster : game.getField().getMonsters()) {
            long group = monster.getRange();
            monsterGroups.putIfAbsent(group, new TreeSet<>((m1, m2) -> -Long.compare(m1.getGold(), m2.getGold())));
            monsterGroups.get(group).add(monster);
            if (monsterGroups.get(group).size() > monsterGroupSizes.get(group)) {
                monsterGroups.get(group).removeLast();
            }
        }
    }

    private void filterUselessMonsters(Game game) {
        buildMonsterGroups(game);
//        List<Monster> needMonsters = game.getField().getMonsters().stream()
//                .filter(m -> monsterGroups.get(m.getRange()).contains(m))
//                .toList();
//        game.getField().setMonsters(needMonsters);
    }

    private Monster findById(Game game, long id) {
        return game.getField().getMonsters().stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    private List<Monster> buildOrder(Game game) {
        List<Integer> firstGroupOrder = List.of(
                21, 22, 15, 16, 23, 24, 17, 18, 25, 26, 19, 20, 27,
                34, 33, 32, 31, 30, 29, 28,
                35, 42, 43, 36, 37, 44, 45, 38, 39, 46, 47, 40, 41, 48
        );
//        System.out.println("first group is " + Boolean.toString(firstGroupOrder.size() == 34));
        List<Monster> res = new ArrayList<>(firstGroupOrder.stream().map(id -> findById(game, id)).toList());

        List<Integer> secondGroupOrder = List.of(
                42, 43, 44, 45, 46, 47, 48,
                41, 40, 39, 38, 37, 36, 35,
                28, 29, 30, 31, 32, 33, 34,
                27, 26, 25, 24, 23, 22, 21,
                14, 15, 16, 17, 18, 19, 20,
                13
        );
        res.addAll(secondGroupOrder.stream().map(id -> findById(game, id + 49)).toList());

        List<Integer> thirdGroupOrder = List.of(
                7, 8, 1, 2, 9, 10, 3, 4, 11, 12, 5, 6, 13,
                20, 19, 18, 17, 16, 15, 14,
                21, 22, 23, 24, 25, 26, 27,
                34, 33, 32, 31, 30, 29, 28,
                35, 42, 43, 36, 37, 44, 45, 38, 39, 46, 47, 40, 41, 48
        );
        res.addAll(thirdGroupOrder.stream().map(id -> findById(game, id + 2 * 49)).toList());

        List<Integer> fourthGroupOrder = List.of(
                42, 43, 44, 45, 46, 47, 48,
                41, 40, 39, 38, 37, 36, 35,
                28, 29, 30, 31, 32, 33, 34,
                27, 26, 25, 24, 23, 22, 21,
                14, 15, 16, 17, 18, 19, 20,
                13, 12, 11, 10, 9, 8, 7,
                0, 1, 2, 3, 4, 5, 6
        );
        res.addAll(fourthGroupOrder.stream().map(id -> findById(game, id + 3 * 49)).toList());

        List<Integer> fivethGroup =
//                List.of(
//                        0, 7, 8, 1, 2, 9, 10, 3, 4, 11, 12, 5, 6, 13, 20, 27
//        );

                List.of(
                        0, 1, 2, 3, 4, 5, 6,
                        13, 12, 11, 10, 9, 8, 7,
                        14, 15, 16, 17, 18, 19, 20,
                        27, 26, 25, 24, 23, 22, 21,
                        28, 29, 30, 31, 32, 33, 34,
                        41, 40, 39, 38, 37, 36, 35,
                        42, 43, 44, 45, 46, 47, 48
                );
        res.addAll(fivethGroup.stream().map(id -> findById(game, id + 4 * 49)).toList());

        return res;
    }

    private Monster getNearestMonster(List<Monster> monsters, Hero hero) {
        return monsters.stream()
                .filter(m -> !m.isKilled())
                .filter(m -> !safePositionsToKill.get(m).isEmpty())
                .filter(m -> m.getRange() * 2 > hero.getRange())
                .sorted((m1, m2) -> {

//                    double d1 = Math.ceil(getDistanceToKillMonster(hero, m1) / (double) hero.getSpeed());
//                    double d2 = Math.ceil(getDistanceToKillMonster(hero, m2) / (double) hero.getSpeed());
//                    double v1 = ;
//                    double v2 = ;
//                    if (d1 != d2) {
//                        return Double.compare(d1, d2);
//                    }
                    return Long.compare(m1.getGold(), m2.getGold());
                })
                .findFirst()
                .get();
    }

    private double calcMovesCount(double dist, Hero hero) {
        return Math.ceil(dist / (double) hero.getSpeed());
    }

    private double calcPosMetr(Position p, Hero hero, List<Monster> nextMonsters, List<Monster> allMonsters) {
//        if (hero.getRange() != 55) {
        List<TravelMove> travelMovesToP = MoveUtils.moveToPosition(hero, p, false);
        boolean containsBanPointsInPath = travelMovesToP.stream()
                .map(m -> new Position(m.getTargetX(), m.getTargetY()))
                .anyMatch(pp -> {
                    for (Monster monster : allMonsters) {
                        if (monster.isKilled()) {
                            continue;
                        }
                        if (pp.distance(new Position(monster.getX(), monster.getY())) <= monster.getRange() * monster.getRange()) {
                            return true;
                        }
                    }
                    return false;
                });
        if (containsBanPointsInPath) {
            return Double.MAX_VALUE;
        }
//        }

        Position heroPos = new Position(hero.getX(), hero.getY());
        double res1 = calcMovesCount(p.distance(heroPos), hero);
        double res3 = 0.0;
        Monster nextMonster = nextMonsters.isEmpty() ? null : nextMonsters.getFirst();
        double res2 = nextMonster == null ? Double.MIN_VALUE : calcMovesCount(p.distance(new Position(nextMonster.getX(), nextMonster.getY())), hero);
        for (Monster monster : nextMonsters) {
            if (monster.isKilled()) {
                continue;
            }
            if (p.distance(new Position(monster.getX(), monster.getY())) <= hero.getRange() * hero.getRange()) {
                res3 += 1.0;
            }
        }
//        if (res3 > 1.0) {
//            System.out.println("can kill few in a point!!");
//            return Double.MIN_VALUE;
//        } else {
        if (nextMonster == null) {
            return res1;
        }
        double w1 = 0.25;
        double w2 = 1.0 - w1;
        return (res1 * w1 + res2 * w2);
//        }
    }

    private Position getPositionToKillMonsterFrom(Hero hero, List<Monster> monsters, int i) {
        final int nextMonstersCount = 14;
        List<Monster> nextMonsters = monsters.subList(i + 1, Math.min(monsters.size(), i + 1 + nextMonstersCount));
        Comparator<Position> comp = (p1, p2) -> Double.compare(calcPosMetr(p1, hero, nextMonsters, monsters), calcPosMetr(p2, hero, nextMonsters, monsters));
        Position heroPos = new Position(hero.getX(), hero.getY());
//        Comparator<Position> comp = (p1, p2) -> Double.compare(p1.distance(heroPos), p2.distance(heroPos));
        Monster monster = monsters.get(i);
        return safePositionsToKill.get(monster).stream()
                .min(comp)
                .orElse(new Position(1_000_000L, 1_000_000L));
    }

//    private double getDistanceToKillMonster(Hero hero, Monster monster) {
//        Position heroPos = new Position(hero.getX(), hero.getY());
//        return getPositionToKillMonsterFrom(hero, monster).distance(heroPos);
//    }

    private void calcSafePos(Game game) {
        safePositionsToKill = new HashMap<>();
        for (Monster m : game.getField().getMonsters()) {
            safePositionsToKill.put(m, MoveUtils.getPositionsToSaveShoot(game, m));
            System.out.println("calculated for monster " + m.getId());
        }
    }

    @Override
    public List<Move> solve(Game game) {
        buildMonsterGroups(game);
        calcSafePos(game);
        long movesLimit = game.getNumTurns();
        List<Move> moves = new ArrayList<>();
        Hero hero = game.getHero();
        List<Monster> monsterOrder = buildOrder(game);

        int i = 0;
        while (moves.size() < movesLimit && i < monsterOrder.size()) {
            long wasRange = hero.getRange();
            Monster toKill = monsterOrder.get(i);
            Position pos = getPositionToKillMonsterFrom(hero, monsterOrder, i++);
            List<TravelMove> travelMoves = new ArrayList<>();
            travelMoves.add(new TravelMove(hero.getX(), hero.getY()));
            travelMoves.addAll(MoveUtils.moveToPosition(hero, pos, false));
            for (TravelMove travelMove : travelMoves) {
                Position step = new Position(travelMove.getTargetX(), travelMove.getTargetY());
                if (step.distance(new Position(toKill.getX(), toKill.getY())) <= wasRange * wasRange) {
                    pos = step;
                    break;
                }
            }

            moves.addAll(MoveUtils.moveToPosition(hero, pos));
            moves.addAll(CombatUtils.killMonster(hero, toKill));

            long curRange = hero.getRange();
            if (curRange != wasRange) {
                calcSafePos(game);
            }
        }

        if (moves.size() > movesLimit) {
            moves = moves.subList(0, (int) movesLimit);
        }
        return moves;
    }
}
