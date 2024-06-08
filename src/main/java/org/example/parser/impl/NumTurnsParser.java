package org.example.parser.impl;

import org.example.parser.Parser;

import java.io.File;
import java.io.IOException;

public class NumTurnsParser extends Parser<Integer> {
    private static final String NUM_TURNS_KEY = "num_turns";

    @Override
    public Integer parse(File file) throws IOException {
        return parseJson(file).get(NUM_TURNS_KEY).asInt();
    }
}
