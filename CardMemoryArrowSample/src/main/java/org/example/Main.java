package org.example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.util.List;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

/**
 * JavaFX App
 */
public class Main extends Application
{
    private GameState gameState;

    private ArrowDuelGame duelGame;
    private Stage primaryStage;

    private Board memoryBoard;
    private Scene memoryBoardScene;

    @Override
    public void start(Stage stage)
    {
        primaryStage = stage;
        gameState = new GameState();

        setBoard();

        duelGame = new ArrowDuelGame(gameState);

        stage.setScene(memoryBoardScene);
        stage.setTitle("Cardigan");
        stage.show();
    }

    private void setBoard()
    {
        List<String> values = List.of("Ace", "King", "Queen", "Jack", "Joker", "10", "9");
        memoryBoard = new Board(values.size(), values);
        memoryBoardScene = new Scene(memoryBoard, 1200, 800);
        memoryBoard.setMatchCallback(() -> startDuel());
    }

    private void startDuel()
    {
        duelGame.setDuelFinished(() -> duelResult());
        primaryStage.setScene(duelGame.getScene());
        duelGame.playerShoot();
    }

    private void duelResult()
    {
        int player1Life = duelGame.getPlayer1Obj().getPlayerLife();
        int player2Life = duelGame.getPlayer2Obj().getPlayerLife();

        // Updated: GameState.Scene -> GameState.GameScene
        GameState.GameScene result = gameState.checkForWinner(player1Life, player2Life);

        if (result == GameState.GameScene.BOARD)
        {
            primaryStage.setScene(memoryBoardScene);
        }
        else
        {
            Scene winnerScene = winnerScene(result);
            primaryStage.setScene(winnerScene);
        }
    }

    public static void main(String[] args)
    {
        launch();
    }

    private Scene winnerScene(GameState.GameScene winner)
    {
        Pane pane = new Pane();
        Label label = new Label(winner == GameState.GameScene.PLAYER1_WIN ? "Player 1 Wins" : "Player 2 Wins");
        pane.getChildren().add(label);
        return new Scene(pane, 1200, 800);
    }
}
