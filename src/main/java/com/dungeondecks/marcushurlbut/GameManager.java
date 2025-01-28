package com.dungeondecks.marcushurlbut;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.dungeondecks.marcushurlbut.games.Hearts;

import java.security.SecureRandom;

public class GameManager {
    private static Map<UUID, Object> sessions = new ConcurrentHashMap<>();
    private static Map<Integer, ArrayList<Player>> waitingLobbies = new ConcurrentHashMap<>();
    private static Map<Integer, GameType> gameType = new ConcurrentHashMap<>();

    private static Map<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    private static Map<UUID, Game> gameSessions = new ConcurrentHashMap<>();
    
    public static int newLobby(Player player, GameType gameSelected) {
        int lobbyID = generateLobbyID();
        Lobby lobby = new Lobby();

        lobby.setGameObject(gameSelected);
        lobby.addPlayerToLobby(player);

        lobbies.put(lobbyID, lobby);

        return lobbyID;
    }

    public static boolean joinLobby(Player player, Integer lobbyID) {
        Lobby lobby = retreiveLobby(lobbyID);
        lobby.addPlayerToLobby(player);

        updateLobby(lobbyID, lobby);

        boolean full = lobby.isLobbyFull();

        if (full) {
            startGameSession(lobby);

            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                updateLobby(lobbyID, new Lobby());
            }, 5000, TimeUnit.MILLISECONDS);
        }

        return full;
    }

    public static Lobby retreiveLobby(Integer lobbyID) {
        return lobbies.get(lobbyID);
    }

    public static void updateLobby(Integer lobbyID, Lobby lobby) {
        lobbies.replace(lobbyID, lobby);
    }

    public static UUID startGameSession(Lobby lobby) {
        Game game = lobby.game;

        game.initialize(lobby.players);
        gameSessions.put(game.gameID, game);

        return game.gameID;
    }

    public static Game retreiveGame(UUID gameID) {
        return gameSessions.get(gameID);
    }

    public static void updateGame(UUID gameID, Game game) {
        gameSessions.replace(gameID, game);
    }









    // public static int newLobby(Player player, GameType game) {
    //     int roomID = generateLobbyID();
    //     ArrayList<Player> newLobby = new ArrayList<Player>();

    //     gameType.put(roomID, game);
    //     newLobby.add(player);

    //     waitingLobbies.putIfAbsent(roomID, newLobby);
    //     System.out.println("GameManager  - new Lobby: " + newLobby);
    //     return roomID;
    // }

    // public static boolean joinLobby(Player player, Integer roomID) {
    //     ArrayList<Player> lobby = retreiveLobby(roomID);
    //     lobby.add(player);
    //     waitingLobbies.replace(roomID, lobby);
    //     System.out.println("GameManager  - updated lobby: " + lobby);

    //     if (retreiveLobby(roomID).size() == 4) {
    //         return true;
    //     }
    //     return false;
    // }

    // public static ArrayList<Player> retreiveLobby(Integer roomID) {
    //     return waitingLobbies.get(roomID);
    // }

    // public static UUID startGame(List<Player> players) {
    //     UUID gameID = UUID.randomUUID();

    //     Hearts hearts = new Hearts(gameID);
    //     hearts.initializePlayers(players);
    //     hearts.shuffleAndDeal();
    //     hearts.active = true;
    //     sessions.put(gameID, hearts);

    //     return gameID;
    // }

    // public static Object retreiveGame(UUID gameID) {
    //     return sessions.get(gameID);
    // }

    // public static void updateGame(UUID gameID, Object heartsSession) {
    //     sessions.replace(gameID, heartsSession);
    // }

    public static int generateLobbyID() {
        SecureRandom secureRandom = new SecureRandom();
        Integer lobbyID = 100000 + secureRandom.nextInt(899999);

        while(lobbies.containsKey(lobbyID)) {
            lobbyID = 100000 + secureRandom.nextInt(899999);
        }

        return lobbyID;
    }
}
