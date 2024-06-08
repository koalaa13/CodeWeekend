package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Field {
    private List<Monster> monsters;
    private long width;
    private long height;

    public Field() {}

    public Field(List<Monster> monsters, long width, long height) {
        this.monsters = monsters;
        this.width = width;
        this.height = height;
    }

    public Field makeCopy() {
        return new Field(new ArrayList<>(monsters), width, height);
    }

    public Monster snapshotMonster(int id) {
        Monster nM = monsters.get(id).makeCopy();
        monsters.set(id, nM);
        return nM;
    }
}
