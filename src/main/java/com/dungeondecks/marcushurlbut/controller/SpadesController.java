package com.dungeondecks.marcushurlbut.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.dungeondecks.marcushurlbut.Card;
import com.dungeondecks.marcushurlbut.GameManager;
import com.dungeondecks.marcushurlbut.GameType;
import com.dungeondecks.marcushurlbut.Lobby;
import com.dungeondecks.marcushurlbut.Message;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.Spades;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SpadesController {
    public final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SpadesController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/spades/newLobby")
    public void newLobby(Player player) throws Exception {
        String playerID = player.getPlayerID().toString();
        Player newPlayer = new Player(player.getPlayerID(), player.getUsername());
        Integer roomID = GameManager.newLobby(newPlayer, GameType.Spades);

        String destination = "/topic/spades/newLobby/" + playerID;
        messagingTemplate.convertAndSend(destination, toJSON(roomID));
    }

    @MessageMapping("/spades/joinLobby")
    public void joinLobby(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        int roomID = Integer.parseInt(message.getRoomID());
        String username = message.getName();

        // Add to lobby
        Player player = new Player(playerID, username);
        boolean gameFull = GameManager.joinLobby(player, roomID);
        Lobby lobby = GameManager.retreiveLobby(roomID);

        // Notify others in lobby
        notifyPlayerJoined(player, roomID);

        Player[] currentPlayers = lobby.players;
        ArrayList<String> otherPlayerNames = new ArrayList<>();

        // Return current player list to this player
        for (Player existingPlayer: currentPlayers) {
            if (existingPlayer != null && player.ID != existingPlayer.ID) {
                otherPlayerNames.add(existingPlayer.getUsername());
            }
        }
        
        System.out.println("otherPlayerNames: " + otherPlayerNames.toString());
        String json = toJSON(otherPlayerNames);

        String destination = "/topic/spades/joinLobby/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, json);

        
        // Start Game & notify game start
        if (gameFull) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                notifyGameStart(lobby.gameID, roomID);
            }, 1000, TimeUnit.MILLISECONDS);

            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                Spades spades = (Spades) GameManager.retreiveGame(lobby.gameID);
                notifySelectTeammate(lobby.gameID, spades.players[0].ID);
            }, 3000, TimeUnit.MILLISECONDS);
        }
    }

    public void notifySelectTeammate(UUID gameID, UUID playerID) {
        try {
            System.out.println("Notifying host to select their teammate");
            String destination = "/topic/spades/notifySelectTeammate/" + gameID.toString() + "/" + playerID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/spades/updateScoreboard")
    public void updateScoreboard(UUID gameID, Player[] players) throws Exception {
        HashMap<String, String> userScores = new HashMap<String, String>();
        for (Player player: players) {
            userScores.put(player.getUsername(), String.valueOf(player.getScore()));
        }
        String destination = "/topic/spades/updateScoreboard/" + gameID.toString();
        messagingTemplate.convertAndSend(destination, toJSON(userScores));
    }

    @MessageMapping("/spades/selectTeammate")
    public void selectTeammate(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());
        String teammateNameObj = message.getCardIDs();

        Spades spades = (Spades) GameManager.retreiveGame(gameID);
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> cardsList = objectMapper.readValue(teammateNameObj, List.class);
        List<String> cardIDs = cardsList.stream().collect(Collectors.toList());
        String teammateName = cardIDs.get(0);

        int index = spades.playerIDtoInt.get(playerID);

        boolean success = spades.setTeams(spades.players[index], teammateName);
        GameManager.updateGame(gameID, spades);

        String destination = "/topic/spades/selectTeammate/" + gameID.toString() + "/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, toJSON(!success));

        if (spades.teamSelectionPhaseComplete) {
            List<List<String>> teams = new ArrayList<List<String>>() {
                {
                    add(spades.team1Names);
                    add(spades.team2Names); 
                }
            };
            
            notifyTeams(gameID, teams);
            // notifyBiddingPhase(gameID, true);
            // notifyPlayersBid(gameID, spades.players[spades.playerInTurn].ID);
        }
    }

    public void notifyTeams(UUID gameID, List<List<String>> teams) {
        try {
            String destination = "/topic/spades/notifyTeams/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(teams));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @MessageMapping("/spades/orientationChange")
    public void orientationChange(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());
        String playersObj = message.getCardIDs();

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> ringList = objectMapper.readValue(playersObj, List.class);
        // List<String> playerList = ringList.stream().collect(Collectors.toList());

        Spades spades = (Spades) GameManager.retreiveGame(gameID);

        Player[] sortedPlayers = spades.updateOrientationRing(ringList);
        GameManager.updateGame(gameID, spades);

        ArrayList<String> sortedPlayerNames = new ArrayList<>();

        for (Player player: sortedPlayers) {
            sortedPlayerNames.add(player.getUsername());
        }


        String destination = "/topic/spades/orientationChange/" + gameID.toString();
        messagingTemplate.convertAndSend(destination, toJSON(sortedPlayerNames));

        // Wait for player array to be recallibrated with teams set before notifying turn
        if (spades.teamSelectionPhaseComplete) {
            notifyBiddingPhase(gameID, true);
            notifyPlayersBid(gameID, spades.players[spades.playerInTurn].ID);
        }
    }

    @MessageMapping("/spades/placeBid")
    public void placeBid(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());
        String strBid = message.getCardIDs();
        
        Spades spades = (Spades) GameManager.retreiveGame(gameID);
        if (spades.biddingPhaseComplete) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> cardsList = objectMapper.readValue(strBid, List.class);
        List<Integer> cardIDs = cardsList.stream().map(Integer::parseInt).collect(Collectors.toList());
        Integer bid = cardIDs.get(0);

        Boolean validBid = spades.setBid(playerID, bid);
        GameManager.updateGame(gameID, spades);
        int id = spades.playerIDtoInt.get(playerID);

        System.out.println("Bid received from player:" + playerID.toString() + " - bid: " + bid + " - valid: " + validBid);

        if (validBid && !spades.biddingPhaseComplete) {
            notifyPlayersBid(gameID, spades.players[spades.playerInTurn].ID);
        }

        if (validBid) {
            broadcastBid(gameID, bid, spades.players[id]);
        }

        if (spades.biddingPhaseComplete) {
            notifyBiddingPhase(gameID, false);
            notifyPlayersTurn(gameID, spades.players[spades.playerInTurn].getPlayerID());
        }

        String destination = "/topic/spades/placeBid/" + gameID.toString() + "/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, toJSON(validBid));
    }

    public void broadcastBid(UUID gameID, int bidAmount, Player userWithBid) {
        String destination = "/topic/spades/broadcastBid/" + gameID.toString();
        HashMap<String, Integer> playerToBidAmount = new HashMap<String, Integer>();

        playerToBidAmount.put(userWithBid.getUsername(), bidAmount);
        messagingTemplate.convertAndSend(destination, toJSON(playerToBidAmount));
    }

    @MessageMapping("/spades/playTurn")
    public void playTurn(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());
        String strCardIDs = message.getCardIDs();

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> cardsList = objectMapper.readValue(strCardIDs, List.class);
        List<Integer> cardIDs = cardsList.stream().map(Integer::parseInt).collect(Collectors.toList());
        Integer cardID = cardIDs.get(0);

        Spades spades = (Spades) GameManager.retreiveGame(gameID);
        int playerIDindex = spades.playerIDtoInt.get(playerID);
        Card cardToBePlayed = spades.players[playerIDindex].hand.get(cardID);

        Boolean validTurn = spades.playTurn(playerID, cardID);
        GameManager.updateGame(gameID, spades);

        String destination = "/topic/spades/playTurn/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, toJSON(validTurn));

        if (validTurn) {
            notifyVoidCards(gameID, cardToBePlayed, spades.players[playerIDindex].getUsername());

            if (!spades.endOfRound) {
                notifyPlayersTurn(gameID, spades.players[spades.playerInTurn].ID);
            }

            if (spades.endOfTrick) {
                notifyEndOfTrick(gameID, spades.players[spades.playerInTurn].getUsername());
            }

            if (spades.isEndOfRound()) {
                notifyEndOfRound(gameID);
                updateScoreboard(gameID, spades.players);
                notifyBiddingPhase(gameID, true);
                notifyPlayersBid(gameID, spades.players[spades.playerInTurn].ID);
            }

            if (spades.gameEnded) {
                notifyGameEnded(gameID, spades.getGameWinner().getUsername());
            }
        }
    }

    @MessageMapping("/spades/getHand")
    public void getHand(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());

        Spades spades = (Spades) GameManager.retreiveGame(gameID);
        int index = spades.playerIDtoInt.get(playerID);
        
        HashMap<Integer, Card> playerHand = spades.players[index].getHand();
        String json = toJSON(playerHand);

        String destination = "/topic/spades/getHand/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, json);
    }

    public void notifyPlayersBid(UUID gameID, UUID playerID) {
        try {
            String destination = "/topic/spades/notifyPlayersBid/" + gameID.toString() + "/" + playerID.toString();
            System.out.println("\nNotifying playerID " + playerID + " it's their bid\n - destination: "  + destination);
            messagingTemplate.convertAndSend(destination, toJSON(true));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyPlayersTurn(UUID gameID, UUID playerID) {
        try {
            String destination = "/topic/spades/notifyPlayersTurn/" + gameID.toString() + "/" + playerID.toString();
            System.out.println("\nNotifying playerID " + playerID + " it's their turn\n - destination: "  + destination);
            messagingTemplate.convertAndSend(destination, toJSON(true));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyVoidCards(UUID gameID,  Card voidCard, String playedByName) {
        try {
            System.out.println("Void Card for notifying: " + voidCard);
            HashMap<Integer, Card> idToCard = new HashMap<Integer, Card>();
            HashMap<String, Object> nameToContent = new HashMap<String, Object>();

            String destination = "/topic/spades/notifyVoidCards/" + gameID.toString();

            int cardID = voidCard.getCardID(voidCard);
            idToCard.put(cardID, voidCard);
            nameToContent.put(playedByName, idToCard);

            messagingTemplate.convertAndSend(destination, toJSON(nameToContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyGameEnded(UUID gameID, String winnerName) {
        try {
            String destination = "/topic/spades/notifyGameEnded/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(winnerName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyEndOfRound(UUID gameID) {
        try {
            System.out.println("Notifying end of round to players");
            String destination = "/topic/spades/notifyEndOfRound/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyEndOfTrick(UUID gameID, String trickWinnerUsername) {
        try {
            System.out.println("Notifying end of trick to players");
            String destination = "/topic/spades/notifyEndOfTrick/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(trickWinnerUsername));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyBiddingPhase(UUID gameID, boolean inBidPhase) {
        try {
            String destination = "/topic/spades/notifyBiddingPhase/" + gameID.toString();

            System.out.println(("Notifying players its bidding phase - " + destination));
            messagingTemplate.convertAndSend(destination, toJSON(inBidPhase));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void notifyPlayerJoined(Player joinedPlayer, Integer lobbyID) {
        try {
            String destination = "/topic/spades/notifyPlayerJoined/" + lobbyID.toString();
            String json = toJSON(joinedPlayer);
    
            System.out.println("Player joined: " + json);
            messagingTemplate.convertAndSend(destination, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyGameStart(UUID gameID, Integer roomID) {
        try {
            String destination = "/topic/spades/notifyGameStart/" + roomID.toString();
            String json = toJSON(gameID);

            System.out.println("destination: " + destination);
            messagingTemplate.convertAndSend(destination, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toJSON(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonArray = objectMapper.writeValueAsString(obj);
            return jsonArray;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
