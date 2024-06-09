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
    private boolean killed;
    private boolean vip;

    public Monster() {}

    public Monster(long x, long y, long exp, long hp, long gold, int id, long range, long attack) {
        this.x = x;
        this.y = y;
        this.exp = exp;
        this.hp = hp;
        this.gold = gold;
        this.id = id;
        this.range = range;
        this.attack = attack;
    }

    public Monster makeCopy() {
        return new Monster(x, y, exp, hp, gold, id, range, attack);
    }

    public boolean isKilled() {
        return hp <= 0;
    }

    public boolean canBeAttacked(long hX, long hY, long heroRange) {
        return (hX - x) * (hX - x) + (hY - y) * (hY - y) <= heroRange * heroRange;
    }

    public long attack(long hX, long hY) {
        if ((hX - x) * (hX - x) + (hY - y) * (hY - y) <= range * range) {
            return attack;
        }
        return 0;
    }
}
