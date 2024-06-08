package org.example.geom;

public class Position {
    public long x;
    public long y;

    public Position(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public long distance(Position p) {
        return (x - p.x) * (x - p.x) + (y - p.y) * (y - p.y);
    }
}
