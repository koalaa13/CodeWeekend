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
    private long level;
    private long exp;

    public long getSpeed() {
        return (long) Math.floor((double) baseSpeed * (1 + (double) level * levelSpeedCoeff / 100.0));
    }

    public long getPower() {
        return (long) Math.floor((double) basePower * (1 + (double) level * levelPowerCoeff / 100.0));
    }

    public long getRange() {
        return (long) Math.floor((double) baseRange * (1 + (double) level * levelRangeCoeff / 100.0));
    }
}
