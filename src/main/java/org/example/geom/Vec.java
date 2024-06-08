package org.example.geom;

public class Vec {
    public double x;
    public double y;

    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    public double len() {
        return Math.sqrt(x * x + y * y);
    }

    public Vec add(Vec v) {
        return new Vec(x + v.x, y + v.y);
    }

    public Vec sub(Vec v) {
        return new Vec(x - v.x, y - v.y);
    }

    public Vec mul(double m) {
        return new Vec(x * m, y * m);
    }

    public Vec norm() {
        return this.mul(1.0 / len());
    }

    public Position[] integerSurround() {
        Position[] res = new Position[4];
        res[0] = new Position((long) Math.floor(x), (long) Math.floor(y));
        res[1] = new Position((long) Math.floor(x), (long) Math.ceil(y));
        res[2] = new Position((long) Math.ceil(x), (long) Math.floor(y));
        res[3] = new Position((long) Math.ceil(x), (long) Math.ceil(y));
        return res;
    }
}
