package org.example.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.model.Hero;
import org.example.parser.Parser;

import java.io.File;
import java.io.IOException;

public class HeroParser extends Parser<Hero> {
    private Hero parseHero(JsonNode json) {
        Hero hero = new Hero();
        JsonNode heroInfo = json.get("hero");

        hero.setBaseSpeed(heroInfo.get("base_speed").asLong());
        hero.setBasePower(heroInfo.get("base_power").asLong());
        hero.setBaseRange(heroInfo.get("base_range").asLong());

        hero.setLevelSpeedCoeff(heroInfo.get("level_speed_coeff").asLong());
        hero.setLevelPowerCoeff(heroInfo.get("level_power_coeff").asLong());
        hero.setLevelRangeCoeff(heroInfo.get("level_range_coeff").asLong());

        hero.setX(json.get("start_x").asLong());
        hero.setY(json.get("start_y").asLong());

        return hero;
    }

    @Override
    public Hero parse(File file) throws IOException {
        return parseHero(parseJson(file));
    }
}
