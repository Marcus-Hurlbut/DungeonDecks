package com.dungeondecks.marcushurlbut;

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

import com.dungeondecks.marcushurlbut.games.Hearts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class HeartsController {
    public final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public HeartsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/hearts/newLobby")
    public void newLobby(Player player) throws Exception {
        String playerID = player.getPlayerID().toString();
        Player newPlayer = new Player(player.getPlayerID(), player.getUsername());
        Integer roomID = GameManager.newLobby(newPlayer, GameType.Hearts);

        String destination = "/topic/hearts/newLobby/" + playerID;
        messagingTemplate.convertAndSend(destination, toJSON(roomID));
    }

    @MessageMapping("/hearts/joinLobby")
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

        // ArrayList<Player> currentPlayers = GameManager.retreiveLobby(roomID);
        ArrayList<String> otherPlayerNames = new ArrayList<>();

        // Return current player list to this player
        for (Player existingPlayer: currentPlayers) {
            if (existingPlayer != null && player.ID != existingPlayer.ID) {
                otherPlayerNames.add(existingPlayer.getUsername());
            }
        }
        
        System.out.println("otherPlayerNames: " + otherPlayerNames.toString());
        String json = toJSON(otherPlayerNames);

        String destination = "/topic/hearts/joinLobby/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, json);
        
        // Start Game & notify game start
        if (gameFull) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                notifyGameStart(lobby.gameID, roomID);
            }, 1000, TimeUnit.MILLISECONDS);
        }
    }

    @MessageMapping("/hearts/updateScoreboard")
    public void updateScoreboard(UUID gameID, Player[] players) throws Exception {
        HashMap<String, String> userScores = new HashMap<String, String>();
        for (Player player: players) {
            userScores.put(player.username, String.valueOf(player.getScore()));
        }
        String destination = "/topic/hearts/updateScoreboard/" + gameID.toString();
        messagingTemplate.convertAndSend(destination, toJSON(userScores));
    }

    @SuppressWarnings("unchecked")
    @MessageMapping("/hearts/playTurn")
    public void playTurn(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());
        String strCardIDs = message.getCardIDs();

        // Format from JSON to to Int
        // TODO: Cleanup/simplify handling
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> cardsList = objectMapper.readValue(strCardIDs, List.class);
        List<Integer> cardIDs = cardsList.stream().map(Integer::parseInt).collect(Collectors.toList());
        Integer cardID = cardIDs.get(0);

        System.out.println("[/topic/hearts/playTurn] - playerID: " + message.getPlayerID() + " CardID: " + cardID);
        
        Hearts hearts = (Hearts) GameManager.retreiveGame(gameID);
        int playerIDindex = hearts.playerIDtoInt.get(playerID);
        Card cardToBePlayed = hearts.players[playerIDindex].hand.get(cardID);

        Boolean validTurn = hearts.playTurn(playerID, cardID);
        GameManager.updateGame(gameID, hearts);

        String destination = "/topic/hearts/playTurn/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, toJSON(validTurn));

        if (validTurn) {
            notifyVoidCards(gameID, cardToBePlayed, hearts.players[playerIDindex].getUsername());

            // Need to re-shuffle first & pass
            if (!hearts.endOfRound) {
                notifyPlayersTurn(gameID, hearts.players[hearts.playerInTurn].ID);
            }

            if (hearts.endOfTrick) {
                notifyEndOfTrick(gameID, hearts.players[hearts.playerInTurn].getUsername());
            }

            if (hearts.endOfRound && (!hearts.passingPhaseComplete || hearts.roundPassingType == PassingPhase.KEEP)) {
                notifyEndOfRound(gameID);
                updateScoreboard(gameID, hearts.players);
                if (hearts.roundPassingType != PassingPhase.KEEP) {
                    notifyPassingPhase(gameID, true);
                } else {
                    hearts = (Hearts) GameManager.retreiveGame(gameID);
                    hearts.onEndOfPassingPhase();
                    GameManager.updateGame(gameID, hearts);
                    notifyPassingPhase(gameID, false);
                    notifyPlayersTurn(gameID, hearts.players[hearts.playerInTurn].ID);
                }
            }

            if (hearts.isGameEnded()) {
                notifyGameEnded(gameID, hearts.getGameWinner().username);
            }
        }
    }

    @MessageMapping("/hearts/getHand")
    public void getHand(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());

        Hearts hearts = (Hearts) GameManager.retreiveGame(gameID);
        int index = hearts.playerIDtoInt.get(playerID);
        
        HashMap<Integer, Card> playerHand = hearts.players[index].getHand();
        String json = toJSON(playerHand);

        String destination = "/topic/hearts/getHand/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, json);
    }

    @SuppressWarnings("unchecked")
    @MessageMapping("/hearts/passCards")
    public void passCards(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());
        String strCardIDs = message.getCardIDs();
        System.out.println("Passing cards for player ID: " + playerID.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> cardsList = objectMapper.readValue(strCardIDs, List.class);
        List<Integer> cardIDs = cardsList.stream().map(Integer::parseInt).collect(Collectors.toList());

        Hearts hearts = (Hearts) GameManager.retreiveGame(gameID);

        int playerIDInt = hearts.playerIDtoInt.get(playerID);
        if (hearts.players[playerIDInt].didPassCards) {
            return;
        }

        UUID sentToID = hearts.passCards(playerID, cardIDs);
        GameManager.updateGame(gameID, hearts);

        // Notify other player of passed cards
        int sentToIDIndex = hearts.playerIDtoInt.get(sentToID);
        HashMap<Integer, Card> sentToPassedCards = hearts.players[sentToIDIndex].passedCards;

        if (hearts.players[sentToIDIndex].didPassCards) {
            notifyPassCardsReceived(sentToID, sentToPassedCards);
        }

        int playerIDIndex = hearts.playerIDtoInt.get(playerID);
        String destination = "/topic/hearts/passCards/" + playerID.toString();

        // Return this player's passed cards if they've been received else
        if (hearts.players[playerIDIndex].didReceiveCards) {
            HashMap<Integer, Card> playerPassedCards = hearts.players[playerIDIndex].passedCards;
            String json = toJSON(playerPassedCards);

            System.out.println("Sending player's received passed cards for playerID: " + playerID.toString());
            messagingTemplate.convertAndSend(destination, json);
        }
        // send false to notify them to listen on the notifyPassCardsReceived event
        else {
            System.out.println("Returning False for " + playerID.toString() + " - must receive cards on notifyPassCardsReceived event");
            messagingTemplate.convertAndSend(destination, toJSON(false));
        }

        Boolean passingPhaseOver = true;
        for (int index = 0; index < hearts.players.length; index++) {
            if (hearts.players[index].didPassCards == false) {
                passingPhaseOver = false;
                break;
            }
        }

        if (passingPhaseOver) {
            System.out.println("\nPassing phase has ended.\n");
            hearts = (Hearts) GameManager.retreiveGame(gameID);
            hearts.onEndOfPassingPhase();
            GameManager.updateGame(gameID, hearts);
            notifyPassingPhase(gameID, false);
            notifyPlayersTurn(gameID, hearts.players[hearts.playerInTurn].ID);
        }
    }

    public void notifyPlayersTurn(UUID gameID, UUID playerID) {
        try {
            String destination = "/topic/hearts/notifyPlayersTurn/" + gameID.toString() + "/" + playerID.toString();
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

            String destination = "/topic/hearts/notifyVoidCards/" + gameID.toString();

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
            String destination = "/topic/hearts/notifyGameEnded/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(winnerName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyEndOfRound(UUID gameID) {
        try {
            System.out.println("Notifying end of round to players");
            String destination = "/topic/hearts/notifyEndOfRound/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public void notifyEndOfTrick(UUID gameID, String trickWinnerUsername) {
        try {
            System.out.println("Notifying end of trick to players");
            String destination = "/topic/hearts/notifyEndOfTrick/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(trickWinnerUsername));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyPassingPhase(UUID gameID, boolean inPassPhase) {
        try {
            String destination = "/topic/hearts/notifyPassingPhase/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, toJSON(inPassPhase));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyPassCardsReceived(UUID playerID, HashMap<Integer, Card> passedCards) {
        try {
            System.out.println("Notifying Player " + playerID + " of received passed cards in callback");
            String destination = "/topic/hearts/notifyPassCardsReceived/" + playerID.toString();
            String json = toJSON(passedCards);
            messagingTemplate.convertAndSend(destination, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyPlayerJoined(Player joinedPlayer, Integer lobbyID) {
        try {
            String destination = "/topic/hearts/notifyPlayerJoined/" + lobbyID.toString();
            String json = toJSON(joinedPlayer);
    
            System.out.println("Player joined: " + json);
            messagingTemplate.convertAndSend(destination, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyGameStart(UUID gameID, Integer roomID) {
        try {
            String destination = "/topic/hearts/notifyGameStart/" + roomID.toString();
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