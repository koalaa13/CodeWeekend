package org.example.scoring;

import org.example.core.FatigueUtils;
import org.example.geom.Position;
import org.example.model.Field;
import org.example.model.Game;
import org.example.model.Hero;
import org.example.model.Monster;
import org.example.solve.SolverConstants;

import java.util.*;

public class BestForField {

    private SolverConstants constants;

    private Field field;

    private TreeMap<Long, Set<Position>> optPos;

    private Map<Position, Long> fatigue;

    private Map<Position, List<Monster>> closeMonsters;

    public BestForField(Field field, TreeMap<Long, Set<Position>> optPos, Map<Position, Long> fatigue, Map<Position, List<Monster>> closeMonsters, SolverConstants constants) {
        this.field = field;
        this.optPos = optPos;
        this.fatigue = fatigue;
        this.closeMonsters = closeMonsters;
        this.constants = constants;
    }

    public void eliminateMonstersFatigue(Monster killedMonster) {
        for (Position position : FatigueUtils.coveredByMonster(killedMonster, field)) {
            Long curFatigue = fatigue.getOrDefault(position, 0L);
            Long newFatigue = curFatigue - killedMonster.getAttack();
            fatigue.put(position, newFatigue);
            optPos.get(curFatigue).remove(position);
            if (!optPos.containsKey(newFatigue)) {
                optPos.put(newFatigue, new HashSet<>());
            }
            optPos.get(newFatigue).add(position);
        }
    }

    private boolean secondBetter(List<Monster> m1, List<Monster> m2) {
        if (m1 == null) {
            return true;
        }
        long gold1 = 0;
        for (Monster monster : m1) {
            gold1 += monster.getGold();
        }
        long gold2 = 0;
        for (Monster monster : m2) {
            gold2 += monster.getGold();
        }
        return gold2 > gold1;
    }

    public class BestPair {
        public Position position;
        public List<Monster> monsters;

        public BestPair(Position position, List<Monster> monsters) {
            this.position = position;
            this.monsters = monsters;
        }
    }

    public BestPair getBest(Game game) {
        Hero hero = game.getHero();
        while (!optPos.isEmpty() && optPos.firstEntry().getValue().isEmpty()) {
            optPos.remove(optPos.firstKey());
        }
        Position bestp = null;
        List<Monster> bestm = null;

        List<Long> available = new ArrayList<>();
        available.add(0L);
        if (game.getTurnsPassed() >= constants.lateStart) {
            available.add(1L);
        }

        for (Long threshold : available) {
            for (Position position : optPos.get(threshold)) {
                if (!closeMonsters.containsKey(position)) {
                    continue;
                }
                List<Monster> monsters = new ArrayList<>();
                for (Monster monster : closeMonsters.get(position)) {
                    if (monster.isKilled()) {
                        continue;
                    }
                    Position monsterP = new Position(monster.getX(), monster.getY());
                    if (position.distance(monsterP) > hero.getRange() * hero.getRange()) {
                        continue;
                    }
                    monsters.add(monster);
                }
                if (secondBetter(bestm, monsters)) {
                    bestp = position;
                    bestm = monsters;
                }
            }
        }
        return new BestPair(bestp, bestm);
    }
}
