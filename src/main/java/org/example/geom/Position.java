package org.example.geom;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
