package org.example.core;

import org.example.model.Hero;
import org.example.model.Monster;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;

import java.util.ArrayList;
import java.util.List;

public class CombatUtils {
    public static void levelUpHero(Hero hero, Monster killedMonster) {
        // Если за убийство получаем сразу несколько лвлов
        int kek = 0;
        for (long exp = killedMonster.getExp(); exp > 0; ) {
            long level = hero.getLevel() + 1;
            long toLvlUpExp = 1000L + level * (level - 1L) * 50L;
            long curExp = hero.getExp();
            long needExp = toLvlUpExp - curExp;
            // Не хватает опыта, просто докидываем что есть
            if (needExp > exp) {
                hero.setExp(hero.getExp() + exp);
                exp = 0;
            } else { // Хватает на лвл ап
                exp -= needExp;
                hero.setExp(0);
                hero.setLevel(level);
            }
        }
    }

    public static List<AttackMove> killMonster(Hero hero, Monster monster) {
        List<AttackMove> moves = new ArrayList<>();
        while (monster.getHp() > 0) {
            monster.setHp(monster.getHp() - hero.getPower());
            moves.add(new AttackMove(monster.getId()));
        }
        monster.setKilled(true);
        levelUpHero(hero, monster);
        return moves;
    }

    public static long movesToKill(Hero hero, Monster monster) {
        return Math.ceilDiv(monster.getHp(), hero.getPower());
    }
}
