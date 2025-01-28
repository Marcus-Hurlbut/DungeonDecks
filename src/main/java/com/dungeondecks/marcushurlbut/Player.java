package com.dungeondecks.marcushurlbut;
import java.util.*;

public class Player {
    public UUID ID;
    String username;
    public HashMap<Integer, Card> hand = new HashMap<Integer, Card>();
    public HashMap<Integer, Card> tricks = new HashMap<Integer, Card>();
    public HashMap<Integer, Card> passedCards = new HashMap<Integer, Card>();
    public boolean didPassCards = false;
    public boolean didReceiveCards = false;
    private int score = 0;
    public UUID teammate;
    public Integer bid = null;

    public Player(UUID ID) {
        this.ID = ID;
    }
    public Player(UUID ID, String username) {
        this.ID = ID;
        this.username = username;
    }

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

    public void addCardToHand(Card card) {
        int id = getCardID(card);
        hand.put(id, card);
    }

    public Card removeCardFromHand(Card card) {
        int id = getCardID(card);
        return hand.remove(id);
    }

    public HashMap<Integer, Card> getHand() {
        List<Map.Entry<Integer, Card>> sortedHand = new ArrayList<>(hand.entrySet());
        sortedHand.sort(Map.Entry.comparingByKey());

        HashMap<Integer, Card> cardMap = new HashMap<>();
        for (Map.Entry<Integer, Card> entry : sortedHand) {
            Card card = entry.getValue();
            int id = getCardID(card);
            cardMap.put(id, card);
        }

        return cardMap;
    }

    public HashMap<Integer, Card> getTricks() {
        List<Map.Entry<Integer, Card>> sortedTricks = new ArrayList<>(tricks.entrySet());
        sortedTricks.sort(Map.Entry.comparingByKey());

        HashMap<Integer, Card> trickMap = new HashMap<>();
        for (Map.Entry<Integer, Card> entry : sortedTricks) {
            Card card = entry.getValue();
            int id = getCardID(card);
            trickMap.put(id, card);
        }

        return trickMap;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getPlayerID() {
        return this.ID;
    }
}
