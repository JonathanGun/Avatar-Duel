package com.avatarduel.model.card.summonable;

import com.avatarduel.model.GameInfo;
import com.avatarduel.model.card.Card;
import com.avatarduel.model.card.CardType;

public abstract class SummonedCard<T extends Card> {
    T card;
    private CardType type;
    private Integer summonedOn;
    boolean isPortrait;

    SummonedCard(T card, CardType type) {
        this.card = card;
        this.type = type;
        this.summonedOn = GameInfo.getTurn();
        this.isPortrait = true;
    }

    public T getCard() {
        return this.card;
    }

    public CardType getType() {
        return this.type;
    }

    public Integer getAttackValue() {
        return 0;
    }

    public Integer getDefendValue() {
        return 0;
    }

    public boolean isSummonedThisTurn() {
        return this.summonedOn.equals(GameInfo.getTurn());
    }

    public boolean isPortrait() {
        return isPortrait;
    }
}
