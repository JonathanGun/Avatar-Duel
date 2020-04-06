package com.avatarduel.model.card;

import com.avatarduel.model.element.Element;

public class Character extends SummonableCard {
    private Integer attack;
    private Integer defend;

    public Character(String name, String description, Element element, String imgPath, Integer attack, Integer defend, Integer power) {
        super(name, description, element, imgPath, power, "CHARACTER");
        this.attack = attack;
        this.defend = defend;
    }

    public Integer getAttack() {
        return attack;
    }

    public Integer getDefend() {
        return defend;
    }

    @Override
    public String getAttributeDescription() {
        return "ATK / " + this.getAttack().toString() +
                " | DEF / " + this.getDefend().toString() +
                " | POW / " + this.getPower().toString();
    }

    @Override
    public String getEffectDescription() {
        return "";
    }

}
