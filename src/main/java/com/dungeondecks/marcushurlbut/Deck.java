package com.dungeondecks.marcushurlbut;

import java.util.*;

import com.dungeondecks.marcushurlbut.card.Name;

public class Deck {

    private Stack<Card> deck = new Stack<Card>();

    public List<Card> getDeck() {
        return deck;
    }

    public void shuffleDeck() {
        initializeSuit(Suit.HEART);
        initializeSuit(Suit.DIAMOND);
        initializeSuit(Suit.SPADE);
        initializeSuit(Suit.CLUB);
        Collections.shuffle(deck);
    }

    public Card popDeck() {
        return deck.pop();
    }

    public void dealDeck(Player[] players) {
        for (int n = 0; n < 52; n++) {
            int playerIndex = n % 4;
            Card topCard = deck.pop();
            players[playerIndex].addCardToHand(topCard);
        }
    }

    private void initializeSuit(Suit suit) {
        for (int index = 1; index < 14; index++) {
            Card card = new Card();
            card.suit = suit;
                
            String imgPathSuffix = "";
            switch (suit) {
                case HEART:
                    imgPathSuffix = "hearts.png";
                    break;

                case DIAMOND:
                    imgPathSuffix = "diamonds.png";
                    break;
                
                case SPADE:
                    imgPathSuffix = "spades.png";
                    break;

                case CLUB:
                    imgPathSuffix = "clubs.png";
                    break;
            
                default:
                    break;
            }
            
            switch(index) {
                case 1:
                    card.name = Name.ACE;
                    card.value = 1;
                    card.imgPath = "ace_of_" + imgPathSuffix;
                    break;
                case 2:
                    card.name = Name.TWO;
                    card.value = 2;
                    card.imgPath = "2_of_" + imgPathSuffix;
                    break;
                case 3:
                    card.name = Name.THREE;
                    card.value = 3;
                    card.imgPath = "3_of_" + imgPathSuffix;
                    break;
                case 4:
                    card.name = Name.FOUR;
                    card.value = 4;
                    card.imgPath =  "4_of_" + imgPathSuffix;
                    break;
                case 5:
                    card.name = Name.FIVE;
                    card.value = 5;
                    card.imgPath = "5_of_" + imgPathSuffix;
                    break;
                case 6:
                    card.name = Name.SIX;
                    card.value = 6;
                    card.imgPath = "6_of_" + imgPathSuffix;
                    break;
                case 7:
                    card.name = Name.SEVEN;
                    card.value = 7;
                    card.imgPath = "7_of_" + imgPathSuffix;
                    break;
                case 8:
                    card.name = Name.EIGHT;
                    card.value = 8;
                    card.imgPath = "8_of_" + imgPathSuffix;
                    break;
                case 9:
                    card.name = Name.NINE;
                    card.value = 9;
                    card.imgPath = "9_of_" + imgPathSuffix;
                    break;
                case 10:
                    card.name = Name.TEN;
                    card.value = 10;
                    card.imgPath = "10_of_" + imgPathSuffix;
                    break;
                case 11:
                    card.name = Name.JACK;
                    card.value = 10;
                    card.imgPath = "jack_of_" + imgPathSuffix;
                    break;
                case 12:
                    card.name = Name.QUEEN;
                    card.value = 10;
                    card.imgPath = "queen_of_" + imgPathSuffix;
                    break;
                case 13:
                    card.name = Name.KING;
                    card.value = 10;
                    card.imgPath = "king_of_" + imgPathSuffix;
                    break;
                default:
                    break;
            }
            deck.push(card);
        }
    }
}
