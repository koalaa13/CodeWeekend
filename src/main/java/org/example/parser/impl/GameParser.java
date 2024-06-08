package org.example.parser.impl;

import org.example.model.Game;
import org.example.parser.Parser;

import java.io.File;
import java.io.IOException;

public class GameParser extends Parser<Game> {
    private final FieldParser fieldParser;
    private final HeroParser heroParser;
    private final NumTurnsParser numTurnsParser;

    public GameParser() {
        numTurnsParser = new NumTurnsParser();
        heroParser = new HeroParser();
        fieldParser = new FieldParser();
    }

    @Override
    public Game parse(File file) throws IOException {
        return new Game(heroParser.parse(file), fieldParser.parse(file), numTurnsParser.parse(file));
    }
}
