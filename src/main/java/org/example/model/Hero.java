package org.example.model;

import lombok.Data;

@Data
public class Hero {
    private int baseSpeed;
    private int basePower;
    private int baseRange;
    private int levelSpeedCoeff;
    private int levelPowerCoeff;
    private int levelRangeCoeff;
    private int x;
    private int y;
    private int level;
    private int exp;

    public int getSpeed() {
        return (int) Math.floor((double) baseSpeed * (1 + (double) level * levelSpeedCoeff / 100.0));
    }

    public int getPower() {
        return (int) Math.floor((double) basePower * (1 + (double) level * levelPowerCoeff / 100.0));
    }

    public int getRange() {
        return (int) Math.floor((double) baseRange * (1 + (double) level * levelRangeCoeff / 100.0));
    }
}
