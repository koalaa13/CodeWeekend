package org.example.scoring;

import org.example.core.CombatUtils;
import org.example.core.MoveUtils;
import org.example.geom.Position;
import org.example.model.Game;
import org.example.model.Hero;
import org.example.model.Monster;
import org.example.model.move.TravelMove;
import org.example.solve.SolverConstants;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ScoreProvider {
    private SolverConstants constants;
    private Game game;
    private boolean zoneUsed;

    public ScoreProvider(Game game, SolverConstants constants) {
        this.game = game;
        this.constants = constants;
        this.zoneUsed = false;
    }

    private double zoneScore(Hero futureHero) {
        return game.getField().getMonsters().stream()
                .filter(m -> !m.isKilled())
                .filter(m -> Math.sqrt(Math.pow(m.getX() - futureHero.getX(), 2) + Math.pow(m.getY() - futureHero.getY(), 2)) <= futureHero.getRange() + futureHero.getSpeed())
                .map(m -> monsterRelativeScore(m, futureHero))
                .reduce(0.0, Double::sum);
    }

    public double monsterRelativeScore(Monster monster, Hero hero) {
        if (monster.isKilled()) {
            return -1e9;
        }
        List<TravelMove> travelMoves = MoveUtils.calcMovesToShotRange(hero, monster);
        long moves = CombatUtils.movesToKill(hero, monster) + travelMoves.size();
        if (moves > game.getNumTurns()) {
            return -1e9 + 1;
        }
        /*if (monster.getRange() >= hero.getRange()) {
            return -1e9 + 2;
        }*/
        if (monster.isVip()) {
            return -1e9 + 3;
        }
        long turns = game.getTurnsPassed();
        //long pureDist = new Position(hero.getX(), hero.getY()).distance(new Position(monster.getX(), monster.getY()));
        long pureDist = (long) Math.pow(hero.getX() - monster.getX(), 2) * 5 + (long) Math.pow(hero.getY() - monster.getY(), 2);
        if (turns < constants.lateStart) {
            return -pureDist;
            //return -(constants.movesCoef * travelMoves.size() + constants.rangeCoef * monster.getRange());
        }

        /*if (monster.isVip() && game.getTurnsPassed() < constants.lateStart) {
            return constants.vipCoeff / (double) travelMoves.size();
        }*/

        if (true) {
            return Math.pow(monster.getGold(), 1) / (double) pureDist;
        }

        long goldCoeff = constants.goldCoeff;
        long expCoeff = constants.expCoeff;
        if (game.getTurnsPassed() >= constants.lateStart) {
            goldCoeff = constants.lateGoldCoeff;
            expCoeff = constants.lateExpCoeff;
        }
        return (double)(monster.getGold() * goldCoeff + monster.getExp() * expCoeff) / movesFunc((double) moves);
    }

    private double movesFunc(double moves) {
        // return moves * Math.log(moves);
        return moves;
        //return moves;
    }

    /*private double monsterRelativeScoreWithZone(Monster monster, Hero hero) {
        if (monster.isKilled()) {
            return -1;
        }
        List<TravelMove> travelMoves = MoveUtils.calcMovesToShotRange(hero, monster);
        long moves = CombatUtils.movesToKill(hero, monster) + travelMoves.size();
        if (moves > game.getNumTurns()) {
            return 0;
        }
        Hero futureHero = hero.makeCopy();
        if (!travelMoves.isEmpty()) {
            futureHero.setX(travelMoves.getLast().getTargetX());
            futureHero.setY(travelMoves.getLast().getTargetY());
        }
        double zoneScore = zoneScore(futureHero);
        return (double)(monster.getGold() * constants.goldCoeff + monster.getExp() * constants.expCoeff) / movesFunc((double) moves);
                + zoneScore * constants.zoneCoeff;
    }*/

    private BiFunction<Monster, Hero, Double> getScoreFunction() {
        /*if (game.getTurnsPassed() > 50) {
            if (!zoneUsed) {
                zoneUsed = true;
                return this::monsterRelativeScoreWithZone;
            }
        }*/
        return this::monsterRelativeScore;
    }

    public Monster findOptimalMonstersByScore() {
        return game.getField().getMonsters().stream().max(Comparator.comparingDouble((Monster m) -> getScoreFunction().apply(m, game.getHero()))).get();
    }
}
