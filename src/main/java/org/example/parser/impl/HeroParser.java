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

        hero.setBaseSpeed(heroInfo.get("base_speed").asInt());
        hero.setBasePower(heroInfo.get("base_power").asInt());
        hero.setBaseRange(heroInfo.get("base_range").asInt());

        hero.setLevelSpeedCoeff(heroInfo.get("level_speed_coeff").asInt());
        hero.setLevelPowerCoeff(heroInfo.get("level_power_coeff").asInt());
        hero.setLevelRangeCoeff(heroInfo.get("level_range_coeff").asInt());

        hero.setX(json.get("start_x").asInt());
        hero.setY(json.get("start_y").asInt());

        return hero;
    }

    @Override
    public Hero parse(File file) throws IOException {
        return parseHero(parseJson(file));
    }
}
