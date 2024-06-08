package org.example.parser.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.model.Field;
import org.example.model.Monster;
import org.example.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FieldParser extends Parser<Field> {
    private Field parseField(JsonNode json) throws JsonProcessingException {
        Field field = new Field();

        field.setWidth(json.get("width").asLong());
        field.setHeight(json.get("height").asLong());

        field.setMonsters(new ArrayList<>());

        for (JsonNode m : json.get("monsters")) {
            field.getMonsters().add(objectMapper.treeToValue(m, Monster.class));
        }

        return field;
    }

    @Override
    public Field parse(File file) throws IOException {
        return parseField(parseJson(file));
    }
}
