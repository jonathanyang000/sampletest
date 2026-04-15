package org.example;
public class Player {
    /*
    This is the player class:
    the player class will have more than 1 life,
    there will only be 2 players
    players will have mouse control
    players will have control of weapons
    players can interact with scenes (the board, the field)
     */
    private int life;
    private String name;

    public Player(String newName) {
        this.name = newName;
        this.life = 3;
    }

    public int getPlayerLife() {
        return life;
    }

    public void loseLife() {
        life = life - 1;
    }
}
