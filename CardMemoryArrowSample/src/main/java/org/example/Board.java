package org.example;
import javafx.animation.PauseTransition;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board extends GridPane {
    // Inner class
    public static class Card extends javafx.scene.control.Button {
        private final String value;
        private boolean cardisFlipped = false;
        private boolean cardisMatched = false;

        public Card(String value) {
            this.value = value;
            setPrefSize(100, 100);
            setText("?"); // Face down
            setOnAction(event -> {
                if (!cardisMatched && !cardisFlipped) {
                    // This logic is handled by the board class' clicker
                }
            });
        }

        public void flipUp() {
            if (!cardisMatched) {
                setText(value);
                cardisFlipped = true;
            }
        }

        public void flipDown() {
            if (!cardisMatched) {
                setText("?");
                cardisFlipped = false;
            }
        }

        public String getValue() {
            return value;
        }

        public boolean cardisMatched() {
            return cardisMatched;
        }

        public void setMatched(boolean matched) {
            cardisMatched = matched;
        }
    }

    // Board class members
    private final List<Card> cards = new ArrayList<>();
    private Card card1 = null;
    private Card card2 = null;
    private int matchedPairs = 0;
    private final int numPairs;
    private boolean isProcessing = false; // Prevents clicking more than two cards

    // Initializes the callback
    private Runnable matchCallback;

    public Board(int numPairs, List<String> cardValues) {
        this.numPairs = numPairs;
        initializeBoard(cardValues);
    }

    private void initializeBoard(List<String> cardValues) {
        // Create pairs of cards
        for (String value : cardValues) {
            cards.add(new Card(value));
            cards.add(new Card(value));
        }

        // Shuffle cards
        Collections.shuffle(cards);

        // Adds cards to the GridPane
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            card.setOnAction(event -> handleCardClick(card));
            int columns = 15;
            int row = i / columns;
            int col = i % columns;
            add(card, col, row);
        }
    }

    private void handleCardClick(Card clickedCard) {
        if (isProcessing || clickedCard.cardisMatched || clickedCard.cardisFlipped) {
            return;
        }

        clickedCard.flipUp();

        if (card1 == null) {
            card1 = clickedCard;
        } else {
            card2 = clickedCard;
            isProcessing = true;
            checkForMatch();
        }
    }

    private void checkForMatch() {
        if (card1.getValue().equals(card2.getValue())) {
            card1.setMatched(true);
            card2.setMatched(true);
            card1.setDisable(true);
            card2.setDisable(true);
            matchedPairs++;

            if (matchCallback != null) {
                matchCallback.run();
            }

            resetTurn();
            checkWinCondition();
        } else {
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> {
                card1.flipDown();
                card2.flipDown();
                resetTurn();
            });
            pause.play();
        }
    }

    private void resetTurn() {
        card1 = null;
        card2 = null;
        isProcessing = false;
    }

    public boolean checkWinCondition() {
        if (matchedPairs == numPairs) {
            return true;
        } else {
            return false;
        }
    }

    public void setMatchCallback(Runnable callback) {
        this.matchCallback = callback;
    }
}
