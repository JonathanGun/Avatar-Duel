package com.avatarduel.model;

import com.avatarduel.Settings;
import com.avatarduel.model.card.*;
import com.avatarduel.model.phase.PhaseController;
import com.avatarduel.model.player.PlayerController;

import com.avatarduel.util.CardDAO;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import javafx.animation.TranslateTransition;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.util.Duration;

public class BoardController implements HasCardController {
    @FXML private CardController cardController;
    @FXML private PlayerController player1Controller;
    @FXML private PlayerController player2Controller;
    @FXML private CardDescController cardDescController;
    @FXML private PhaseController phaseController;
    @FXML private Text message;
    @FXML private Text boardStatus;

    private GameEventHandler gameEventHandler;

    public BoardController() throws IOException, URISyntaxException {
        CardDAO.init();
        this.gameEventHandler = GameEventHandler.getInstance();
    }

    public void init() {
        this.cardController.init(this);
        this.cardDescController.init(this);
        this.phaseController.init(this);
        this.player1Controller.init(this, 1);
        this.player2Controller.init(this, 2);
        this.boardStatus.setTranslateY(-400);
    }

    public CardDescController getCardDescController(){
        return this.cardDescController;
    }

    @Override
    public void setActiveCard(Card c){
        this.cardController.setCard(c, Location.GRAVEYARD);
        this.cardDescController.setAttributes(c);
    }

    @Override
    public CardController getCardController() {
        return this.cardController;
    }

    /**
     * Start the game
     * Two player automatically draw cards
     * Set the Player 1 to play first
     */
    public void startGame() {
        this.player1Controller.drawNCards(Settings.startingCardAmount);
        this.player2Controller.drawNCards(Settings.startingCardAmount);

        this.player2Controller.endTurn();
        this.player1Controller.startTurn();
    }

    /**
     * Change the player's turn (Player 1 to Player 2 and otherwise)
     * player1Controller is a player that is active (on its turn)
     */
    public void nextPlayer() {
        this.player1Controller.endTurn();
        PlayerController tmp = this.player1Controller;
        this.player1Controller = this.player2Controller;
        this.player2Controller = tmp;
        this.player1Controller.startTurn();
    }

    public PlayerController getActivePlayer() {
        return this.player1Controller;
    }

    public PlayerController getOtherPlayer() {
        return this.player2Controller;
    }

    public PhaseController getPhaseController(){
        return this.phaseController;
    }

    public void endPhase() {
        this.getActivePlayer().endPhase();
    }

    public GameEventHandler getGameEventHandler() {
        return gameEventHandler;
    }

    /**
     * Set the message for action that is mainly not allowed
     * @param message mostly exception messages
     */
    public void setMessage(String message) {
        this.message.setText(message);
    }

    /**
     * Set the layout to show boardStatus text when game is over
     * @param id number of player that wins
     */
    public void gameOver(Integer id) {
        String result = "Player " + id + " - " + GameInfo.getPlayer(id).getName() + " WIN";
        this.boardStatus.setText(result);
        TranslateTransition trans = new TranslateTransition(Duration.seconds(4), this.boardStatus);
        trans.setByY(400);
        trans.play();
    }
}
