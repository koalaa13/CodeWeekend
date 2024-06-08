package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Game {
    private Hero hero;
    private Field field;
    private long numTurns;
    private long goldGained;
    private long turnsPassed;

    public Game() {}

    public Game(Hero hero, Field field, long numTurns, long goldGained, long turnsPassed) {
        this.hero = hero.makeCopy();
        this.field = field.makeCopy();
        this.numTurns = numTurns;
        this.goldGained = goldGained;
        this.turnsPassed = turnsPassed;
    }

    public Game makeCopy() {
        return new Game(hero, field, numTurns, goldGained, turnsPassed);
    }
}
