package com.avatarduel.model.card.summonable;

import com.avatarduel.exception.NoCharaterOnFieldException;
import com.avatarduel.model.card.Card;
import com.avatarduel.model.card.summonable.character.Character;
import com.avatarduel.model.card.summonable.skill.Skill;
import com.avatarduel.model.player.field.FieldController;

public class CardSummoner<T extends Card> {
    private T card;

    public CardSummoner(T card) {
        this.card = card;
    }

    /**
     * Used to get summon card that its type according to its attribute
     * @param field Field controller of one player
     * @throws NoCharaterOnFieldException exception when the card is skill and there are no characters in arena.
     * @return SummonedCard that its type according to its attribute
     */
    public SummonedCard summon(FieldController field) throws NoCharaterOnFieldException {
        if (card instanceof EmptyCard) {
            return SummonedEmptyCard.getInstance();

        } else if (card instanceof Character) {
            return new SummonedCharacterCard((Character) card);

        } else if (card instanceof Skill) {
            // no character on field (own and enemy's)
            if (!field.thereIsCharacter() && !field.getParent().getParent().getOtherPlayer().getFieldController().thereIsCharacter()) {
                throw new NoCharaterOnFieldException();
            }

            return new SummonedSkillCard((Skill) card, card.getCardType());
        }
        assert false;
        return null;
    }
}
