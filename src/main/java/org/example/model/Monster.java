package org.example.model;

import lombok.Data;

@Data
public class Monster {
    private long x;
    private long y;
    private long exp;
    private long hp;
    private long gold;
    private int id;
    private long range;
    private long attack;

    public Monster() {}

    public Monster(long x, long y, long exp, long hp, long gold, int id) {
        this.x = x;
        this.y = y;
        this.exp = exp;
        this.hp = hp;
        this.gold = gold;
        this.id = id;
    }

    public Monster makeCopy() {
        return new Monster(x, y, exp, hp, gold, id);
    }

    public boolean isKilled() {
        return hp <= 0;
    }
}
