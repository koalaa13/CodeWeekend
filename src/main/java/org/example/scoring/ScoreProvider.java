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
            return 0;
        }
        List<TravelMove> travelMoves = MoveUtils.calcMovesToShotRange(hero, monster);
        long moves = CombatUtils.movesToKill(hero, monster) + travelMoves.size();
        if (moves > game.getNumTurns()) {
            return 0;
        }
        long goldCoeff = constants.goldCoeff;
        long expCoeff = constants.expCoeff;
        if (game.getTurnsPassed() >= constants.lateStart) {
            goldCoeff = constants.lateGoldCoeff;
            expCoeff = constants.lateExpCoeff;
        }
        return (double)(monster.getGold() * goldCoeff + monster.getExp() * expCoeff) / (double) moves;
    }

    private double monsterRelativeScoreWithZone(Monster monster, Hero hero) {
        if (monster.isKilled()) {
            return 0;
        }
        List<TravelMove> travelMoves = MoveUtils.calcMovesToShotRange(hero, monster);
        long moves = CombatUtils.movesToKill(hero, monster) + travelMoves.size();
        if (moves > game.getNumTurns()) {
            return 0;
        }
        Hero futureHero = hero.makeCopy();
        if (!travelMoves.isEmpty()) {
            futureHero.setX(travelMoves.get(travelMoves.size() - 1).getTargetX());
            futureHero.setY(travelMoves.get(travelMoves.size() - 1).getTargetY());
        }
        double zoneScore = zoneScore(futureHero);
        return (double)(monster.getGold() * constants.goldCoeff + monster.getExp() * constants.expCoeff) / (double) moves
                + zoneScore * constants.zoneCoeff;
    }

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
