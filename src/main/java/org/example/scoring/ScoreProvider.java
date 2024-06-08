package org.example.scoring;

import org.example.core.CombatUtils;
import org.example.core.MoveUtils;
import org.example.model.Game;
import org.example.model.Hero;
import org.example.model.Monster;
import org.example.solve.SolverConstants;

import java.util.Comparator;

public class ScoreProvider {
    private SolverConstants constants;
    private Game game;

    public ScoreProvider(Game game, SolverConstants constants) {
        this.game = game;
        this.constants = constants;
    }

    private double monsterRelativeScore(Monster monster, Hero hero) {
        if (monster.isKilled()) {
            return -1;
        }
        long moves = CombatUtils.movesToKill(hero, monster) + MoveUtils.calcMovesToShotRange(hero, monster).size();
        return (double)(monster.getGold() * constants.goldCoeff + monster.getExp() * constants.expCoeff) / (double) moves;
    }

    public Monster findOptimalMonstersByScore() {
        return game.getField().getMonsters().stream().max(Comparator.comparingDouble((Monster m) -> monsterRelativeScore(m, game.getHero()))).get();
    }
}
