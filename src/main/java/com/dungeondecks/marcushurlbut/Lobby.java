package com.dungeondecks.marcushurlbut;

import java.util.ArrayList;
import java.util.UUID;

import com.dungeondecks.marcushurlbut.games.Hearts;
import com.dungeondecks.marcushurlbut.games.Spades;

public class Lobby {
    private int id;
    private GameType type;
    public Player[] players = new Player[4];
    public int playerIndex = 0;
    public int maxPlayers = Integer.MAX_VALUE;

    public Game game;
    public UUID gameID = UUID.randomUUID();

    public void setGameObject(GameType type) {
        switch (type) {
            case Hearts:
                game = (Hearts) new Hearts(gameID);
                maxPlayers = 4;
                break;

            case Spades:
                game = (Spades) new Spades(gameID);
                maxPlayers = 4;
                break;
        
            default:
                break;
        }
    }

    public void addPlayerToLobby(Player player) {
        if (playerIndex < maxPlayers) {
            players[playerIndex] = player;
            playerIndex++;
        }
    }

    public boolean isLobbyFull() {
        return playerIndex == maxPlayers;
    }
}
