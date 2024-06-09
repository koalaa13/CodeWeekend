package org.example.geom;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
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
