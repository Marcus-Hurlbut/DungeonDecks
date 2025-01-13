package com.crazycards.marcushurlbut;

public class Message {
    private String roomID;
    private String playerID;
    private String name;
    private String content;
    private String cardIDs;

    public Message() {}

    public Message(String content) {
        this.content = content;
    }

    public Message(String playerID, String roomID) {
        this.playerID = playerID;
        this.roomID = roomID;
    }

    public Message(String playerID, String roomID, String cardIDs) {
        this.playerID = playerID;
        this.roomID = roomID;
        this.cardIDs = cardIDs;
    }

    public Message(String playerID, String roomID, String cardIDs, String name) {
        this.playerID = playerID;
        this.roomID = roomID;
        this.cardIDs = cardIDs;
        this.name = name;
    }

    // public Message(String playerID, String name, String object) {
    //     this.playerID = playerID;
    //     this.name = name;
    // }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getCardIDs() {
        return cardIDs;
    }

    public String getRoomID() {
        return roomID;
    }
}
