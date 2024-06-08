package org.example.model;

import lombok.Data;

@Data
public class Game {
    private Hero hero;
    private Field field;
    private long numTurns;
    private long goldGained;
    private long turnsPassed;
    private long travelsCount;

    public Game() {}

    public Game(Hero hero, Field field, long numTurns, long goldGained, long turnsPassed, long travelsCount) {
        this.hero = hero.makeCopy();
        this.field = field.makeCopy();
        this.numTurns = numTurns;
        this.goldGained = goldGained;
        this.turnsPassed = turnsPassed;
        this.travelsCount = travelsCount;
    }

    public Game makeCopy() {
        return new Game(hero, field, numTurns, goldGained, turnsPassed, travelsCount);
    }
}
