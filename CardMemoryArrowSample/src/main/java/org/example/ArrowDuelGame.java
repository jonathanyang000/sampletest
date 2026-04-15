/**
 * Lucas Severin
 */
package org.example;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;

public class ArrowDuelGame
{

    private Pane root = new Pane();

    private ImageView player1;   // ✅ changed
    private Rectangle player2;
    private Rectangle bar;
    private Label turnLabel;

    private GameState gameState;
    private Scene scene;

    private Player player1Obj;
    private Player player2Obj;

    private double barSpeed = 2;
    private boolean movingDown = true;

    private boolean arrowInFlight = false;

    private boolean shootingEnabled = false;

    private Runnable duelFinished;

    public ArrowDuelGame(GameState gameState)
    {
        this.gameState = gameState;
        scene = new Scene(root, 1200, 800);
        setupGame();
    }

    public Scene getScene()
    {
        return scene;
    }

    public void setupGame()
    {
        double centerX = scene.getWidth() / 2;
        double centerY = scene.getHeight() / 2;

        turnLabel = new Label("");
        turnLabel.setTextFill(Color.BLACK);
        root.getChildren().add(turnLabel);
        updateTurnLabel();

        turnLabel.layoutXProperty().bind(scene.widthProperty().subtract(turnLabel.widthProperty()).divide(2));
        turnLabel.setLayoutY(50);

        root.requestFocus();

        scene.setOnMouseClicked(e ->
        {
            root.requestFocus();

            if (!shootingEnabled || arrowInFlight) return;

            double targetX = e.getX();
            double targetY = e.getY();

            arrowInFlight = true;

            if (gameState.getCurrentTurn() == GameState.Turn.PLAYER1_TURN)
            {
                shootArrow(player1, player2, player2Obj, targetX, targetY);
            }
            else
            {
                shootArrow(player2, player1, player1Obj, targetX, targetY);
            }
        });

        // Bar (unchanged)
        bar = new Rectangle(50, 200, Color.BLACK);
        bar.setLayoutX(centerX - bar.getWidth() / 2);
        bar.setLayoutY(centerY - bar.getHeight() / 2);

        AnimationTimer barTimer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                double centerBarY = bar.getLayoutY() + bar.getHeight() / 2;
                double barHalfHeight = bar.getHeight() / 2;
                double nextBarY = centerBarY + (movingDown ? barSpeed : -barSpeed);

                if (nextBarY - barHalfHeight <= 0)
                {
                    nextBarY = barHalfHeight;
                    movingDown = true;
                }
                else if (nextBarY + barHalfHeight >= scene.getHeight())
                {
                    nextBarY = scene.getHeight() - barHalfHeight;
                    movingDown = false;
                }
                bar.setLayoutY(nextBarY - bar.getHeight() / 2);
            }
        };
        barTimer.start();

        // ✅ PLAYER 1 (IMAGE)
        Image p1Img = new Image(getClass().getResource("/player1.png").toExternalForm());
        player1 = new ImageView(p1Img);
        player1.setFitWidth(40);
        player1.setFitHeight(40);
        player1.setLayoutX(100);
        player1.setLayoutY(600);

        // PLAYER 2 (unchanged)
        player2 = new Rectangle(30, 30, Color.RED);
        player2.setLayoutX(1100);
        player2.setLayoutY(600);

        player1Obj = new Player("Player 1");
        player2Obj = new Player("Player 2");

        root.getChildren().addAll(player1, player2, bar);

        scene.setOnKeyPressed(event ->
        {
            double step = 15;
            Node currentPlayer;

            if (gameState.getCurrentTurn() == GameState.Turn.PLAYER1_TURN)
            {
                currentPlayer = player1;
            }
            else
            {
                currentPlayer = player2;
            }

            switch (event.getCode())
            {
                case W:
                case UP:
                    currentPlayer.setLayoutY(Math.max(currentPlayer.getLayoutY() - step, 0));
                    break;

                case S:
                case DOWN:
                    currentPlayer.setLayoutY(
                            Math.min(
                                    currentPlayer.getLayoutY() + step,
                                    scene.getHeight() - currentPlayer.getBoundsInParent().getHeight()
                            )
                    );
                    break;
            }
        });
    }

    public void updateTurnLabel()
    {
        if (gameState.getCurrentTurn() == GameState.Turn.PLAYER1_TURN)
        {
            turnLabel.setText("Player1's Turn ->");
        }
        else
        {
            turnLabel.setText("<- Player 2's Turn");
        }
    }

    public void playerShoot()
    {
        shootingEnabled = true;
    }

    private void stopArrow(Polygon arrow, AnimationTimer timer)
    {
        root.getChildren().remove(arrow);
        arrowInFlight = false;
        timer.stop();
        gameState.changeCurrentTurn();
        updateTurnLabel();
        if (duelFinished != null)
        {
            duelFinished.run();
        }
    }

    private void shootArrow(Node shooter, Node target, Player targetObj, double targetX, double targetY)
    {
        Polygon arrow = new Polygon(0.0, 0.0, -20.0, -5.0, -20.0, 5.0);
        arrow.setFill(Color.BLACK);

        double startX = shooter.getLayoutX() + shooter.getBoundsInParent().getWidth() / 2;
        double startY = shooter.getLayoutY() + shooter.getBoundsInParent().getHeight() / 2;
        double speed = 6;

        arrow.setLayoutX(startX);
        arrow.setLayoutY(startY);

        root.getChildren().add(arrow);

        double distanceX = targetX - startX;
        double distanceY = targetY - startY;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (distance == 0)
        {
            arrowInFlight = false;
            return;
        }

        double dx = distanceX / distance;
        double dy = distanceY / distance;

        double angle = Math.toDegrees(Math.atan2(dy, dx));
        arrow.setRotate(angle);

        AnimationTimer arrowTimer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                arrow.setLayoutX(arrow.getLayoutX() + dx * speed);
                arrow.setLayoutY(arrow.getLayoutY() + dy * speed);

                if (arrow.getBoundsInParent().intersects(target.getBoundsInParent()))
                {
                    targetObj.loseLife();
                    stopArrow(arrow, this);
                }
                else if (arrow.getBoundsInParent().intersects(bar.getBoundsInParent()))
                {
                    stopArrow(arrow, this);
                }
                else if (arrow.getLayoutX() < 0 || arrow.getLayoutX() > scene.getWidth())
                {
                    stopArrow(arrow, this);
                }
                else if (arrow.getLayoutY() < 0 || arrow.getLayoutY() > scene.getHeight())
                {
                    stopArrow(arrow, this);
                }
            }
        };
        arrowTimer.start();
    }

    public Player getPlayer1Obj()
    {
        return player1Obj;
    }

    public Player getPlayer2Obj()
    {
        return player2Obj;
    }

    public void setDuelFinished(Runnable callback)
    {
        this.duelFinished = callback;
    }
}
