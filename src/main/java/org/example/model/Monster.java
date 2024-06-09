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
}
