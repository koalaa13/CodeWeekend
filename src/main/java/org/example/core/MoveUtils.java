package org.example.core;

import org.example.geom.Position;
import org.example.geom.Vec;
import org.example.model.Game;
import org.example.model.Hero;
import org.example.model.Monster;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;

import java.util.ArrayList;
import java.util.List;

public class MoveUtils {
    public static List<TravelMove> calcMovesToGoToPoint(Hero hero, Position position) {
        long wasRange = hero.getRange();
        long wasLevel = hero.getLevel();
        hero.setBaseRange(0);
        hero.setLevel(0);

        Monster monster = new Monster();
        monster.setX(position.x);
        monster.setY(position.y);
        List<TravelMove> moves = calcMovesToShotRange(hero, monster);

        hero.setBaseRange(wasRange);
        hero.setLevel(wasLevel);

        return moves;
    }

    public static List<TravelMove> calcMovesToShotRangeWithMonsterRange(Hero hero, Monster monster) {
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
                if (
                        heroPos.distance(possiblePos) <= hero.getSpeed() * hero.getSpeed() &&
                                possiblePos.distance(monsterPos) < minDist &&
                                heroPos.distance(possiblePos) > monster.getRange() * monster.getRange()
                ) {
                    minDist = possiblePos.distance(monsterPos);
                    nextPos = possiblePos;
                }
            }
            if (nextPos == null) {
                throw new RuntimeException("can't find next move");
            }
            moves.add(new TravelMove(nextPos.x, nextPos.y));
            heroPos = nextPos;
        }
        return moves;
    }

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
            heroPos = nextPos;
        }
        return moves;
    }

    public static List<TravelMove> moveToShotRange(Hero hero, Monster monster) {
        List<TravelMove> moves = calcMovesToShotRange(hero, monster);
        if (!moves.isEmpty()) {
            hero.setX(moves.get(moves.size() - 1).getTargetX());
            hero.setY(moves.get(moves.size() - 1).getTargetY());
        }
        return moves;
    }

    public static List<TravelMove> moveToShotRangeWithMonsterRange(Hero hero, Monster monster) {
        List<TravelMove> moves = calcMovesToShotRangeWithMonsterRange(hero, monster);
        if (!moves.isEmpty()) {
            hero.setX(moves.get(moves.size() - 1).getTargetX());
            hero.setY(moves.get(moves.size() - 1).getTargetY());
        }
        return moves;
    }

    public static List<Position> getPositionsToSaveShoot(Game game, Monster monster) {
        List<Position> res = new ArrayList<>();
        Hero hero = game.getHero();
        Position monsterPos = new Position(monster.getX(), monster.getY());
        for (int x = 0; x < game.getField().getWidth(); ++x) {
            for (int y = 0; y < game.getField().getHeight(); ++y) {
                Position pos = new Position(x, y);
                if (pos.distance(monsterPos) > hero.getRange() * hero.getRange()) {
                    continue;
                }
                if (pos.distance(monsterPos) <= monster.getRange() * monster.getRange()) {
                    continue;
                }
                res.add(pos);
            }
        }
        return res;
    }

    public static List<TravelMove> moveToPosition(Hero hero, Position position) {
        return moveToPosition(hero, position, true);
    }

    public static List<TravelMove> moveToPosition(Hero hero, Position position, boolean changeHero) {
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
                if (changeHero) {
                    hero.setX(position.x);
                    hero.setY(position.y);
                }
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
            heroPos = nextPos;
        }
        return moves;
    }
}
