package org.example.core;

import org.example.geom.Position;
import org.example.geom.Vec;
import org.example.model.Hero;
import org.example.model.Monster;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;

import java.util.ArrayList;
import java.util.List;

public class MoveUtils {
    public static List<TravelMove> calcMovesToShotRange(Hero hero, Monster monster) {
        List<TravelMove> moves = new ArrayList<>();
        Position heroPos = new Position(hero.getX(), hero.getY());
        Position monsterPos = new Position(monster.getX(), monster.getY());
        Vec monsterV = new Vec(monsterPos);

        while (true) {
            long distance = heroPos.distance(monsterPos);
            if (distance <= hero.getRange() * hero.getRange()) {
                break;
            }
            Vec heroV = new Vec(heroPos);
            Vec direction = monsterV.sub(heroV).norm();
            long minDist = Long.MAX_VALUE;
            Position nextPos = null;
            double shiftDistance = hero.getSpeed();
            if (hero.getSpeed() + hero.getRange() > Math.sqrt(heroPos.distance(monsterPos))) {
                shiftDistance = Math.sqrt(heroPos.distance(monsterPos)) - hero.getRange();
            }
            Position[] possiblePoses = heroV.add(direction.mul(shiftDistance)).integerSurround();
            for (Position possiblePos : possiblePoses) {
                if (heroPos.distance(possiblePos) <= hero.getSpeed() * hero.getSpeed() && possiblePos.distance(monsterPos) < minDist) {
                    minDist = possiblePos.distance(monsterPos);
                    nextPos = possiblePos;
                }
            }
            if (nextPos == null) {
                throw new RuntimeException("can't find next move");
            }
            moves.add(new TravelMove(nextPos.x, nextPos.y));
            moves.getLast().comment = "Move to " + nextPos.x + ", " + nextPos.y;
            heroPos = nextPos;
        }
        return moves;
    }

    public static List<TravelMove> moveToShotRange(Hero hero, Monster monster) {
        List<TravelMove> moves = calcMovesToShotRange(hero, monster);
        if (!moves.isEmpty()) {
            hero.setX(moves.getLast().getTargetX());
            hero.setY(moves.getLast().getTargetY());
        }
        return moves;
    }

    public static List<TravelMove> moveToPosition(Hero hero, Position position) {
        List<TravelMove> moves = new ArrayList<>();
        Position heroPos = new Position(hero.getX(), hero.getY());
        Vec posV = new Vec(position);

        while (true) {
            long distance = heroPos.distance(position);
            if (distance == 0) {
                break;
            }
            if (distance <= hero.getSpeed() * hero.getSpeed()) {
                moves.add(new TravelMove(position.x, position.y));
                moves.getLast().comment = "Move to " + position.x + ", " + position.y;
                hero.setX(position.x);
                hero.setY(position.y);
                break;
            }
            Vec heroV = new Vec(heroPos);
            Vec direction = posV.sub(heroV).norm();
            long minDist = Long.MAX_VALUE;
            Position nextPos = null;
            double shiftDistance = hero.getSpeed();
            Position[] possiblePoses = heroV.add(direction.mul(shiftDistance)).integerSurround();
            for (Position possiblePos : possiblePoses) {
                if (heroPos.distance(possiblePos) <= hero.getSpeed() * hero.getSpeed() && possiblePos.distance(position) < minDist) {
                    minDist = possiblePos.distance(position);
                    nextPos = possiblePos;
                }
            }
            if (nextPos == null) {
                throw new RuntimeException("can't find next move");
            }
            moves.add(new TravelMove(nextPos.x, nextPos.y));
            moves.getLast().comment = "Move to " + nextPos.x + ", " + nextPos.y;
            heroPos = nextPos;
        }
        return moves;
    }
}
