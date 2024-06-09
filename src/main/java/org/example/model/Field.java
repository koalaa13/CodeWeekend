package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Field {
    private List<Monster> monsters;
    private long width;
    private long height;

    public Field() {
        monsters = new ArrayList<>();
    }

    public Field(List<Monster> monsters, long width, long height) {
        this.monsters = monsters;
        this.width = width;
        this.height = height;
    }

    public Field makeCopy() {
        List<Monster> newMonsters = new ArrayList<>();
        for (Monster m : monsters) {
            if (!m.isKilled()) newMonsters.add(m);
        }
        return new Field(newMonsters, width, height);
    }

    public Monster snapshotMonster(int id) {
        Monster nM = monsters.get(id).makeCopy();
        monsters.set(id, nM);
        return nM;
    }
}
