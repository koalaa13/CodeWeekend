package org.example.core;

import org.example.model.Hero;
import org.example.model.Monster;
import org.example.model.move.TravelMove;

import java.util.List;

public class Util {
    public static void levelUpHero(Hero hero, Monster killedMonster) {
        // Если за убийство получаем сразу несколько лвлов
        for (int exp = killedMonster.getExp(); exp > 0; ) {
            int level = hero.getLevel() + 1;
            int toLvlUpExp = 1000 + level * (level - 1) * 50;
            int curExp = hero.getExp();
            int needExp = toLvlUpExp - curExp;
            // Не хватает опыта, просто докидываем что есть
            if (needExp > exp) {
                hero.setExp(hero.getExp() + exp);
                exp = 0;
            } else { // Хватает на лвл ап
                exp -= needExp;
                hero.setExp(0);
                hero.setLevel(level + 1);
            }
        }
    }

    public static int turnsToKill(Hero hero, Monster monster) {
        return Math.ceilDiv(monster.getHp(), hero.getPower());
    }
}
