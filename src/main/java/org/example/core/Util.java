package org.example.core;

import org.example.model.Hero;
import org.example.model.Monster;

public class Util {
    public static final String TEST_FOLDER =
            "/home/nmaksimov/Рабочий стол/codeweekend/codeweekend/src/main/resources/";

    public static void levelUpHero(Hero hero, Monster killedMonster) {
        // Если за убийство получаем сразу несколько лвлов
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
                hero.setLevel(level + 1);
            }
        }
    }

    public static long turnsToKill(Hero hero, Monster monster) {
        return Math.ceilDiv(monster.getHp(), hero.getPower());
    }
}
