package com.avatarduel.model.player.field;

import com.avatarduel.model.GameEventHandler;
import com.avatarduel.event.*;
import com.avatarduel.exception.*;

import com.avatarduel.model.GameInfo;
import com.avatarduel.model.Location;
import com.avatarduel.model.card.*;
import com.avatarduel.model.card.land.Land;
import com.avatarduel.model.card.summonable.*;
import com.avatarduel.model.card.summonable.character.Character;
import com.avatarduel.model.card.summonable.skill.Skill;

import com.avatarduel.model.player.PlayerController;
import com.avatarduel.util.CSSLoader;
import com.avatarduel.util.Pair;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class FieldController implements Subscriber {

    private PlayerController parent;

    @FXML private GridPane root;

    private static final Integer nrow = 2;
    private static final Integer ncol = 6;
    private SummonedCard[][] cards;
    private CardController[][] cardControllers;
    private boolean summonedLandThisTurn;

    @FXML
    public void init(PlayerController playerController) {
        this.parent = playerController;
        this.cards = new SummonedCard[nrow][ncol];
        this.cardControllers = new CardController[nrow][ncol];
        for (Integer i = 0; i < FieldController.nrow; i++) {
            for (Integer j = 0; j < FieldController.ncol; j++) {
                this.cardControllers[i][j] = new CardController();
                this.cardControllers[i][j].setRoot((StackPane) ((Pane) this.root.getChildren().get(i * ncol + j)).getChildren().get(0));
                this.cards[i][j] = SummonedEmptyCard.getInstance();
            }
        }
        this.summonedLandThisTurn = false;
        this.update();

        this.getGameEventHandler().subscribe(this, EventType.DISCARDFIELD);
        this.getGameEventHandler().subscribe(this, EventType.SUMMON);
        this.getGameEventHandler().subscribe(this, EventType.ATTACKENEMY);
        this.getGameEventHandler().subscribe(this, EventType.ATTACKCARD);
        this.getGameEventHandler().subscribe(this, EventType.CHANGESTANCE);
    }

    public void update() {
        for (int i = 0; i < FieldController.nrow; i++) {
            for (int j = 0; j < FieldController.ncol; j++) {
                this.getCardController(i, j).show(Location.FIELD);
            }
        }
    }

    @FXML
    public void onHoverEnter(MouseEvent event) {
        Card c = this.cursorAtCard(event);
        if (c != null) {
            this.getParent().getParent().setActiveCard(c);
        }
    }

    @FXML
    public void onClick(MouseEvent event) {
        if (this.isActivePlayer() && !GameInfo.isEndPhase()) {
            this.getGameEventHandler().selectCard(event, this.cursorAtCard(event), this.getParent().getId(), Location.FIELD);

            //            if (GameInfo.isMainPhase()){
            //                try {
            //                    this.summonCard(event, selectedCard);
            //                    this.getParent().getHandController().removeCard(selectedCard);
            //                    this.show();
            //                    System.out.println("Success summon card" + selectedCard);
            //
            //                } catch (FieldCellIsOccupiedException e) {
            //                    try {
            //                        SummonableCard summonableCard = (SummonableCard) selectedCard;
            //                        this.getParent().getInventory().addCurrentPower(summonableCard.getElement(), summonableCard.getPower());
            //                    } catch (Exception a) {
            //                        //do nothing
            //                    }
            //                    this.changeStance(event);
            //                    System.out.println("Success to change card stance");
            //
            //                } catch (AlreadySummonedLand | NotImplementedException | NotEnoughPowerException | NullPointerException e){
            //                    System.out.println(e.getLocalizedMessage());
            //
            //                } catch (CannotSummonCardException e) {
            //                    SummonableCard summonableCard = (SummonableCard) selectedCard;
            //                    this.getParent().getInventory().addCurrentPower(summonableCard.getElement(), summonableCard.getPower());
            //                    System.out.println(e.getLocalizedMessage());
            //
            //                }
            //            } else if (GameInfo.isBattlePhase()) {
            //                this.selectedCard = (SummonableCard) selectedCard;
            //                if(selectedCard instanceof Character) {
            //                    System.out.println("Card selected for attacking");
            //                }
            //            }
        }
    }

    private void changeStance(SelectedCard firstCard) throws ClassCastException {
        ((SummonedCharacterCard)this.getSummonedCard(firstCard.getCard())).changeStance();
        this.update();
    }

    private void summonCard(MouseEvent event, Card selectedCard) throws NotEnoughPowerException, NotImplementedException, CannotSummonCardException {
        if (selectedCard instanceof Land) {
            if (!this.summonedLandThisTurn) {
                this.getParent().getInventory().addPowerCapacity(selectedCard.getElement());
                this.summonedLandThisTurn = true;
            } else {
                throw new AlreadySummonedLand();
            }
        } else {
            SummonableCard summonCard = (SummonableCard) selectedCard;
            this.getParent().getInventory().usePower(summonCard);

            Integer i = this.correctRow(GridPane.getRowIndex(this.cursorAtNode(event)));
            Integer j = (GridPane.getColumnIndex(this.cursorAtNode(event)));

            SummonedCard summonedCard = new CardSummoner<>(summonCard).summon(this, i, j);
            if (this.isEmpty(i, j)) {
                if (this.isRightRow(i, summonedCard)) {
                    this.cards[i][j] = summonedCard;
                    this.getCardController(i, j).setCard(summonedCard.getCard(), Location.FIELD);
                } else {
                    throw new WrongRowException();
                }
            } else {
                throw new FieldCellIsOccupiedException();
            }
        }
    }

    private void attachSkill(SelectedCard firstCard, SelectedCard secondCard){
        ((SummonedCharacterCard)this.getSummonedCard(firstCard.getCard())).attachSkill(this.getSummonedCard(secondCard.getCard()));
    }

    private SummonedCard getSummonedCard(Card card){
        for (SummonedCard[] summonedCards : this.cards) {
            for (SummonedCard summonedCard : summonedCards) {
                if(summonedCard.getCard().equals(card)){
                    return summonedCard;
                }
            }
        }
        assert false;
        return null;
    }

    private boolean isRightRow(Integer i, SummonedCard card) {
        Card c = card.getCard();
        if (c instanceof Character) {
            return i == 0;
        } else if (c instanceof Skill) {
            return i == 1;
        } else {
            return true;
        }
    }

    private Integer correctRow(Integer i) {
        i += this.getParent().isPlayerOne ? 0 : 1;
        return i % 2;
    }


    private boolean isEmpty(Integer i, Integer j) {
        return this.getCardController(i, j).isEmpty();
    }

    private boolean isActivePlayer() {
        return this.getParent().isActivePlayer();
    }

    private CardController getControllerAtCursor(MouseEvent event) {
        Pair<Integer, Integer> idx = this.getIndexFromCursor(event);
        return this.getCardController(idx.first(), idx.second());
    }

    private SummonedCard getCardAtCursor(MouseEvent event) {
        Pair<Integer, Integer> idx = this.getIndexFromCursor(event);
        return this.cards[idx.first()][idx.second()];
    }

    private Pair<Integer, Integer> getIndexFromCursor(MouseEvent event) {
        Integer i = this.correctRow(GridPane.getRowIndex(this.cursorAtNode(event)));
        Integer j = GridPane.getColumnIndex(this.cursorAtNode(event));
        return new Pair<>(i, j);
    }

    private CardController getCardController(Integer i, Integer j) {
        return this.cardControllers[this.correctRow(i)][j];
    }

    private Node cursorAtNode(MouseEvent event) {
        return (Node) event.getSource();
    }

    private Card cursorAtCard(MouseEvent event) {
        int i = GridPane.getRowIndex(this.cursorAtNode(event));
        int j = GridPane.getColumnIndex(this.cursorAtNode(event));
        i = correctRow(i);
        return this.getCardController(i, j).getCard();
    }

    public boolean thereIsCharacter() {
        int i = 0;
        for (int j = 0; j < FieldController.ncol; j++) {
            if (!this.getCardController(i, j).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void endTurn() {
        for (int i = 0; i < ncol; i++) {
            this.summonedLandThisTurn = false;
        }
    }

    public void setColor(String color) {
        root.getChildren().forEach(node -> CSSLoader.addClass(node, color + "-field-cell"));
    }

    public PlayerController getParent() {
        return parent;
    }

    private GameEventHandler getGameEventHandler() {
        return this.getParent().getGameEventHandler();
    }

    private void removeCard(Card card) {
        for (int i = 0; i < this.cardControllers.length; i++) {
            CardController[] ccs = this.cardControllers[i];
            for (int j = 0; j < ccs.length; j++) {
                if (ccs[j].getCard() == card) {
                    ccs[j].setEmpty(Location.FIELD);
                    this.cards[i][j] = SummonedEmptyCard.getInstance();
                }
            }
        }
    }

    @Override
    public void onEvent(MouseEvent event, EventType type, SelectedCard firstCard, SelectedCard secondCard) {
        if (this.isActivePlayer()) {
            if (type.equals(EventType.SUMMON)) {
                this.onSummonEvent(event, firstCard, secondCard);
            } else if (type.equals(EventType.DISCARDHAND)) {
                this.onDiscardFieldEvent(firstCard, secondCard);
            } else if (type.equals(EventType.CHANGESTANCE)) {
                this.onChangeStanceEvent(firstCard);
            }
        } else {
            if (type.equals((EventType.ATTACKCARD))) {
                this.onAttackCardEvent(firstCard, secondCard);
            } else if (type.equals(EventType.ATTACKENEMY)) {
                this.onAttackEnemyEvent(firstCard, secondCard);
            }
        }
    }

    private void onSummonEvent(MouseEvent event, SelectedCard firstCard, SelectedCard secondCard) {
        try {
            this.summonCard(event, firstCard.getCard());
            this.getGameEventHandler().publish(event, EventType.SUMMONSUCCESS);

        } catch (NotEnoughPowerException e) {
            this.getGameEventHandler().publish(event, EventType.SUMMONFAIL);
            System.out.println(e.getMessage());

        } catch (AlreadySummonedLand e){
            firstCard.getCard().setNotSelected();
            this.getGameEventHandler().publish(event, EventType.ALREADYSUMMONEDLAND);
            System.out.println(e.getMessage());

        } catch (NotImplementedException | CannotSummonCardException e) {
            firstCard.getCard().setNotSelected();
            this.getGameEventHandler().publish(event, EventType.SUMMONFAILBUTENOUGHPOWER);
            System.out.println(e.getMessage());
        }
        this.update();
    }

    private void onChangeStanceEvent(SelectedCard firstCard) {
        this.changeStance(firstCard);
    }

    private void onDiscardFieldEvent(SelectedCard firstCard, SelectedCard secondCard) {
        this.removeCard(firstCard.getCard());
    }

    public void onAttachSkillEvent(SelectedCard firstCard, SelectedCard secondCard) {
        this.attachSkill(firstCard, secondCard);
    }

    private void onAttackCardEvent(SelectedCard firstCard, SelectedCard secondCard) {
        // TODO
    }

    private void onAttackEnemyEvent(SelectedCard firstCard, SelectedCard secondCard) {
        // TODO
    }
}
