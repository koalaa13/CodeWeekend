package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Game {
    private Hero hero;
    private Field field;
    private long numTurns;
    private long goldGained;
    private long turnsPassed;
}
