package org.example.model;

import lombok.Data;

@Data
public class Hero {
    private long baseSpeed;
    private long basePower;
    private long baseRange;
    private long levelSpeedCoeff;
    private long levelPowerCoeff;
    private long levelRangeCoeff;
    private long x;
    private long y;
    private long startX;
    private long startY;
    private long level;
    private long exp;
    private long totalExp;
    private long fatigue;

    public long getSpeed() {
        return (long) Math.floor((double) baseSpeed * (1 + (double) level * levelSpeedCoeff / 100.0));
    }

    public long getPower() {
        return (long) Math.floor((double) basePower * (1 + (double) level * levelPowerCoeff / 100.0));
    }

    public long getRange() {
        return (long) Math.floor((double) baseRange * (1 + (double) level * levelRangeCoeff / 100.0));
    }

    public long getGold(long monsterGold) {
        return (1000 * monsterGold) / (1000 + fatigue);
    }

    public void initStart() {
        startX = x;
        startY = y;
    }

    public long getShift() {
        return Math.abs(startX - x) + Math.abs(startY - y);
    }

    public Hero makeCopy() {
        Hero hero = new Hero();
        hero.baseSpeed = baseSpeed;
        hero.basePower = basePower;
        hero.baseRange = baseRange;
        hero.levelSpeedCoeff = levelSpeedCoeff;
        hero.levelPowerCoeff = levelPowerCoeff;
        hero.levelRangeCoeff = levelRangeCoeff;
        hero.x = x;
        hero.y = y;
        hero.startX = startX;
        hero.startY = startY;
        hero.level = level;
        hero.exp = exp;
        hero.fatigue = fatigue;
        return hero;
    }
}
