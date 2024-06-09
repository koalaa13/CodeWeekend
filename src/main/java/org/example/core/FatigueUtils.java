package org.example.core;

import org.example.geom.Position;
import org.example.model.Field;
import org.example.model.Game;
import org.example.model.Monster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FatigueUtils {
    public static List<Position> coveredByMonster(Monster monster, Field field) {
        List<Position> positions = new ArrayList<>();
        for (long x = Math.max(0, monster.getX() - monster.getRange()); x <= Math.min(field.getWidth(), monster.getX() + monster.getRange()); x++) {
            for (long y = Math.max(0, monster.getY() - monster.getRange()); y <= Math.min(field.getHeight(), monster.getY() + monster.getRange()); y++) {
                Position cur = new Position(x, y);
                if (cur.distance(new Position(monster.getX(), monster.getY())) <= monster.getRange() * monster.getRange()) {
                    positions.add(cur);
                }
            }
        }
        return positions;
    }

    public static Map<Position, Long> calcFatigueByPosition(Game game) {
        Map<Position, Long> fatigue = new HashMap<>();
        for (Monster monster : game.getField().getMonsters()) {
            for (Position position : coveredByMonster(monster, game.getField())) {
                if (!fatigue.containsKey(position)) {
                    fatigue.put(position, 0L);
                }
                fatigue.put(position, fatigue.get(position) + monster.getAttack());
            }
        }
        return fatigue;
    }
}
