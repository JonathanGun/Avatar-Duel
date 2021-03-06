package com.avatarduel.model.card.summonable.skill;

import com.avatarduel.model.card.CardType;
import com.avatarduel.model.element.Element;

public class Aura extends Skill {
    private static final String TYPE_NAME = "AURA";
    private Integer attack;
    private Integer defend;

    public Aura(String name, String description, Element element, String imgPath, Integer power, Integer attack, Integer defend) {
        super(name, description, element, imgPath, power, Aura.TYPE_NAME);
        this.attack = attack;
        this.defend = defend;
        this.cardType = CardType.AURA;
    }

    public Integer getAttack() {
        return attack;
    }

    @Override
    public String getAttributeDescription() {
        return "ATK / " + this.attack.toString() +
                " | DEF / " + this.defend.toString() +
                " | " + super.getAttributeDescription();
    }

    public Integer getDefend() {
        return defend;
    }
}
