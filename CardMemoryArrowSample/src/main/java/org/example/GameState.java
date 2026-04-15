package org.example;
public class GameState
{
    private GameScene currentScene;
    private Turn currentTurn;

    // Renamed from Scene to GameScene to avoid shadowing javafx.scene.Scene
    public enum GameScene
    {
        MENU, BOARD, FIELD, PLAYER1_WIN, PLAYER2_WIN;
    }

    public enum Turn
    {
        PLAYER1_TURN, PLAYER2_TURN;
    }

    public GameState()
    {
        currentScene = GameScene.MENU;
        currentTurn = Turn.PLAYER1_TURN;
    }

    public void changeCurrentScene(GameScene changeSceneToThis)
    {
        currentScene = changeSceneToThis;
    }

    public void changeCurrentTurn()
    {
        if (currentTurn == Turn.PLAYER1_TURN)
        {
            currentTurn = Turn.PLAYER2_TURN;
        }
        else
        {
            currentTurn = Turn.PLAYER1_TURN;
        }
    }

    public GameScene checkForWinner(int player1_Life, int player2_Life)
    {
        if (player1_Life == 0)
        {
            return GameScene.PLAYER2_WIN;
        }
        else if (player2_Life == 0)
        {
            return GameScene.PLAYER1_WIN;
        }
        else
        {
            return GameScene.BOARD;
        }
    }

    public Turn getCurrentTurn()
    {
        return currentTurn;
    }

    public GameScene getCurrentScene()
    {
        return currentScene;
    }
}
