package com.dungeondecks.marcushurlbut.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dungeondecks.marcushurlbut.Card;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.Suit;
import com.dungeondecks.marcushurlbut.card.Name;
import com.dungeondecks.marcushurlbut.utils.CardID;

public class TestUtils {
    public UUID player_1_id = UUID.randomUUID();
    public UUID player_2_id = UUID.randomUUID();
    public UUID player_3_id = UUID.randomUUID();
    public UUID player_4_id = UUID.randomUUID();

    public Player player_1 = new Player(player_1_id, "Professor X");
    public Player player_2 = new Player(player_2_id, "Cyclops");
    public Player player_3 = new Player(player_3_id, "Mystique");
    public Player player_4 = new Player(player_4_id, "Magneto");

    public Player[] getPlayerArray() {
        Player[] players = new Player[4];
        players[0] = player_1;
        players[1] = player_2;
        players[2] = player_3;
        players[3] = player_4;
        return players;
    }

    public Card getCardFromPlayerHand(Player player, CardID cardID) {
        return player.getHand().get(cardID.getOrdinal());
    }

    public void populateFakeDeck(Player[] players) {
        players[0].addCardToHand(new Card(Suit.HEART, Name.SEVEN, 7, "7_of_hearts.png"));
        players[0].addCardToHand(new Card(Suit.HEART, Name.KING, 10, "king_of_hearts.png"));
        players[0].addCardToHand(new Card(Suit.HEART, Name.ACE, 1, "ace_of_hearts.png"));
        players[0].addCardToHand(new Card(Suit.DIAMOND, Name.EIGHT, 8, "8_of_diamonds.png"));
        players[0].addCardToHand(new Card(Suit.DIAMOND, Name.NINE, 9, "9_of_diamonds.png"));
        players[0].addCardToHand(new Card(Suit.SPADE, Name.THREE, 3, "3_of_spades.png"));
        players[0].addCardToHand(new Card(Suit.SPADE, Name.FIVE, 5, "5_of_spades.png"));
        players[0].addCardToHand(new Card(Suit.SPADE, Name.SIX, 6, "6_of_spades.png"));
        players[0].addCardToHand(new Card(Suit.SPADE, Name.TEN, 10, "10_of_spades.png"));
        players[0].addCardToHand(new Card(Suit.CLUB, Name.NINE, 9, "9_of_clubs.png"));
        players[0].addCardToHand(new Card(Suit.CLUB, Name.TEN, 10, "10_of_clubs.png"));
        players[0].addCardToHand(new Card(Suit.CLUB, Name.JACK, 10, "jack_of_clubs.png"));
        players[0].addCardToHand(new Card(Suit.CLUB, Name.TWO, 2, "2_of_clubs.png"));


        players[1].addCardToHand(new Card(Suit.HEART, Name.TWO, 2, "2_of_hearts.png"));
        players[1].addCardToHand(new Card(Suit.HEART, Name.EIGHT, 8, "8_of_hearts.png"));
        players[1].addCardToHand(new Card(Suit.HEART, Name.NINE, 9, "9_of_hearts.png"));
        players[1].addCardToHand(new Card(Suit.HEART, Name.TEN, 10, "10_of_hearts.png"));
        players[1].addCardToHand(new Card(Suit.DIAMOND, Name.SIX, 6, "6_of_diamonds.png"));
        players[1].addCardToHand(new Card(Suit.DIAMOND, Name.TEN, 10, "10_of_diamonds.png"));
        players[1].addCardToHand(new Card(Suit.DIAMOND, Name.JACK, 10, "jack_of_diamonds.png"));
        players[1].addCardToHand(new Card(Suit.DIAMOND, Name.QUEEN, 10, "queen_of_diamonds.png"));
        players[1].addCardToHand(new Card(Suit.SPADE, Name.FOUR, 4, "4_of_spades.png"));
        players[1].addCardToHand(new Card(Suit.SPADE, Name.QUEEN, 10, "queen_of_spades.png"));
        players[1].addCardToHand(new Card(Suit.SPADE, Name.KING, 10, "king_of_spades.png"));
        players[1].addCardToHand(new Card(Suit.SPADE, Name.ACE, 1, "ace_of_spades.png"));
        players[1].addCardToHand(new Card(Suit.CLUB, Name.THREE, 3, "3_of_clubs.png"));


        players[2].addCardToHand(new Card(Suit.HEART, Name.FIVE, 5, "5_of_hearts.png"));
        players[2].addCardToHand(new Card(Suit.HEART, Name.SIX, 6, "6_of_hearts.png"));
        players[2].addCardToHand(new Card(Suit.HEART, Name.QUEEN, 10, "queen_of_hearts.png"));
        players[2].addCardToHand(new Card(Suit.DIAMOND, Name.TWO, 2, "2_of_diamonds.png"));
        players[2].addCardToHand(new Card(Suit.DIAMOND, Name.THREE, 3, "3_of_diamonds.png"));
        players[2].addCardToHand(new Card(Suit.DIAMOND, Name.FOUR, 4, "4_of_diamonds.png"));
        players[2].addCardToHand(new Card(Suit.DIAMOND, Name.KING, 10, "king_of_diamonds.png"));
        players[2].addCardToHand(new Card(Suit.DIAMOND, Name.ACE, 1, "ace_of_diamonds.png"));
        players[2].addCardToHand(new Card(Suit.SPADE, Name.TWO, 2, "2_of_spades.png"));
        players[2].addCardToHand(new Card(Suit.SPADE, Name.JACK, 10, "jack_of_spades.png"));
        players[2].addCardToHand(new Card(Suit.CLUB, Name.SEVEN, 7, "7_of_clubs.png"));
        players[2].addCardToHand(new Card(Suit.CLUB, Name.FIVE, 5, "5_of_clubs.png"));
        players[2].addCardToHand(new Card(Suit.CLUB, Name.QUEEN, 10, "queen_of_clubs.png"));

        
        players[3].addCardToHand(new Card(Suit.HEART, Name.THREE, 3, "3_of_hearts.png"));
        players[3].addCardToHand(new Card(Suit.HEART, Name.FOUR, 4, "4_of_hearts.png"));
        players[3].addCardToHand(new Card(Suit.HEART, Name.JACK, 10, "jack_of_hearts.png"));
        players[3].addCardToHand(new Card(Suit.DIAMOND, Name.FIVE, 5, "5_of_diamonds.png"));
        players[3].addCardToHand(new Card(Suit.DIAMOND, Name.SEVEN, 7, "7_of_diamonds.png"));
        players[3].addCardToHand(new Card(Suit.SPADE, Name.SEVEN, 7, "7_of_spades.png"));
        players[3].addCardToHand(new Card(Suit.SPADE, Name.EIGHT, 8, "8_of_spades.png"));
        players[3].addCardToHand(new Card(Suit.SPADE, Name.NINE, 9, "9_of_spades.png"));
        players[3].addCardToHand(new Card(Suit.CLUB, Name.FOUR, 4, "4_of_clubs.png"));
        players[3].addCardToHand(new Card(Suit.CLUB, Name.SIX, 6, "6_of_clubs.png"));
        players[3].addCardToHand(new Card(Suit.CLUB, Name.EIGHT, 8, "8_of_clubs.png"));
        players[3].addCardToHand(new Card(Suit.CLUB, Name.KING, 10, "king_of_clubs.png"));
        players[3].addCardToHand(new Card(Suit.CLUB, Name.ACE, 1, "ace_of_clubs.png"));
    }

    public List<Integer> getFirstSimulatedTrickCardIDs() {
        List<Integer> cardIDList = new ArrayList<Integer>();
        cardIDList.add(CardID.CLUB_TWO.getOrdinal());
        cardIDList.add(CardID.CLUB_THREE.getOrdinal());
        cardIDList.add(CardID.CLUB_QUEEN.getOrdinal());
        cardIDList.add(CardID.CLUB_ACE.getOrdinal());

        return cardIDList;
    }

    public void populateSimulatedShootingTheMoonHand(Player player) {
        player.tricks.put(CardID.HEART_SEVEN.getOrdinal(), new Card(Suit.HEART, Name.SEVEN, 7, "7_of_hearts.png"));
        player.tricks.put(CardID.HEART_KING.getOrdinal(), new Card(Suit.HEART, Name.KING, 10, "king_of_hearts.png"));
        player.tricks.put(CardID.HEART_ACE.getOrdinal(), new Card(Suit.HEART, Name.ACE, 1, "ace_of_hearts.png"));
        player.tricks.put(CardID.HEART_TWO.getOrdinal(), new Card(Suit.HEART, Name.TWO, 2, "2_of_hearts.png"));
        player.tricks.put(CardID.HEART_EIGHT.getOrdinal(), new Card(Suit.HEART, Name.EIGHT, 8, "8_of_hearts.png"));
        player.tricks.put(CardID.HEART_NINE.getOrdinal(), new Card(Suit.HEART, Name.NINE, 9, "9_of_hearts.png"));
        player.tricks.put(CardID.HEART_TEN.getOrdinal(), new Card(Suit.HEART, Name.TEN, 10, "10_of_hearts.png"));
        player.tricks.put(CardID.HEART_FIVE.getOrdinal(), new Card(Suit.HEART, Name.FIVE, 5, "5_of_hearts.png"));
        player.tricks.put(CardID.HEART_SIX.getOrdinal(), new Card(Suit.HEART, Name.SIX, 6, "6_of_hearts.png"));
        player.tricks.put(CardID.HEART_QUEEN.getOrdinal(), new Card(Suit.HEART, Name.QUEEN, 10, "queen_of_hearts.png"));
        player.tricks.put(CardID.HEART_THREE.getOrdinal(), new Card(Suit.HEART, Name.THREE, 3, "3_of_hearts.png"));
        player.tricks.put(CardID.HEART_FOUR.getOrdinal(), new Card(Suit.HEART, Name.FOUR, 4, "4_of_hearts.png"));
        player.tricks.put(CardID.HEART_JACK.getOrdinal(), new Card(Suit.HEART, Name.JACK, 10, "jack_of_hearts.png"));
        player.tricks.put(CardID.SPADE_QUEEN.getOrdinal(), new Card(Suit.SPADE, Name.QUEEN, 10, "queen_of_spades.png"));
        player.tricks.put(CardID.DIAMOND_EIGHT.getOrdinal(), new Card(Suit.DIAMOND, Name.EIGHT, 8, "8_of_diamonds.png"));
        player.tricks.put(CardID.DIAMOND_NINE.getOrdinal(), new Card(Suit.DIAMOND, Name.NINE, 9, "9_of_diamonds.png"));
        player.tricks.put(CardID.SPADE_THREE.getOrdinal(), new Card(Suit.SPADE, Name.THREE, 3, "3_of_spades.png"));
        player.tricks.put(CardID.SPADE_FIVE.getOrdinal(), new Card(Suit.SPADE, Name.FIVE, 5, "5_of_spades.png"));
        player.tricks.put(CardID.SPADE_SIX.getOrdinal(), new Card(Suit.SPADE, Name.SIX, 6, "6_of_spades.png"));
        player.tricks.put(CardID.SPADE_TEN.getOrdinal(), new Card(Suit.SPADE, Name.TEN, 10, "10_of_spades.png"));
        player.tricks.put(CardID.CLUB_NINE.getOrdinal(), new Card(Suit.CLUB, Name.NINE, 9, "9_of_clubs.png"));
        player.tricks.put(CardID.CLUB_TEN.getOrdinal(), new Card(Suit.CLUB, Name.TEN, 10, "10_of_clubs.png"));
        player.tricks.put(CardID.CLUB_JACK.getOrdinal(), new Card(Suit.CLUB, Name.JACK, 10, "jack_of_clubs.png"));
        player.tricks.put(CardID.CLUB_TWO.getOrdinal(), new Card(Suit.CLUB, Name.TWO, 2, "2_of_clubs.png"));
    }
    
}
