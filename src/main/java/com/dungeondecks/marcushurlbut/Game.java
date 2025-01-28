package com.dungeondecks.marcushurlbut;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Game {
    public Player[] players = new Player[4];
    public HashMap<UUID, Integer> playerIDtoInt = new HashMap<UUID, Integer>();
    
    public UUID gameID;
    public boolean active = false;

    public boolean gameEnded = false;
    public Player gameWinner = null;
    public boolean endOfRound = true;
    int roundNumber = 0;

    public int playerInTurn = -1;

    public void initialize(Player[] players) {}

    public int getCardID(Card card) {
        int offset = 0;
        switch (card.suit) {
            case HEART:
                offset = 0;
                break;

            case DIAMOND:
                offset = 13;
                break;

            case SPADE:
                offset = 26;
                break;

            case CLUB:
                offset = 39;
                break;

            default:
                break;
        }

        if (card.name.ordinal() == 0) {
            return offset;
        }
        return (card.name.ordinal() + offset);
    }
    
}
