package org.example.parser.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.model.Field;
import org.example.model.Monster;
import org.example.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class FieldParser extends Parser<Field> {
    private Field parseField(JsonNode json) throws JsonProcessingException {
        Field field = new Field();

        field.setWidth(json.get("width").asLong());
        field.setHeight(json.get("height").asLong());

        field.setMonsters(new ArrayList<>());

        for (JsonNode m : json.get("monsters")) {
            field.getMonsters().add(objectMapper.treeToValue(m, Monster.class));
        }

        for (int i = 0; i < field.getMonsters().size(); i++) {
            field.getMonsters().get(i).setId(i);
            if (field.getMonsters().get(i).getAttack() > 900000000) {
                field.getMonsters().get(i).setVip(true);
            }
            /*if (field.getMonsters().get(i).getExp() > 1) {
                field.getMonsters().get(i).setVip(true);
            }*/
        }

        Collections.shuffle(field.getMonsters(), new Random(1373737));

        //field.setMonsters(field.getMonsters().stream().filter(m -> m.getGold() > 1).toList());

        return field;
    }

    @Override
    public Field parse(File file) throws IOException {
        return parseField(parseJson(file));
    }
}
