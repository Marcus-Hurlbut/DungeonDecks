package com.dungeondecks.marcushurlbut.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.dungeondecks.marcushurlbut.Card;
import com.dungeondecks.marcushurlbut.PassingPhase;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.Hearts;
import com.dungeondecks.marcushurlbut.util.TestUtils;
import com.dungeondecks.marcushurlbut.utils.CardID;

public class HeartsTest {
    private static UUID gameID;
    private static Hearts hearts;
    private static TestUtils testUtils;

    private static void setup() {
        gameID = UUID.randomUUID();
        hearts = new Hearts(gameID);
        testUtils = new TestUtils();
    }

    private void startGame(Hearts heart, boolean skipPassingPhase, boolean useFakeDeck) {
        heart.initializePlayers(testUtils.getPlayerArray());
        if (!useFakeDeck) {
            heart.shuffleAndDeal();
            heart.active = true;
        } else {
            testUtils.populateFakeDeck(hearts.players);
            hearts.setPassingPhase();
        }

        if (skipPassingPhase) {
            hearts.onEndOfPassingPhase();
        }
    }

    private void simulatePassPhase(Player[] players,  List<Integer> player_1_passCards,  List<Integer> player_2_passCards,  List<Integer> player_3_passCards,  List<Integer> player_4_passCards) {
        hearts.passCards(players[0].getPlayerID(), player_1_passCards);
        hearts.passCards(players[1].getPlayerID(), player_2_passCards);
        hearts.passCards(players[2].getPlayerID(), player_3_passCards);
        hearts.passCards(players[3].getPlayerID(), player_4_passCards);
    }

    private void simulateTrick(Hearts hearts, List<Integer> cardIDList) {
        Player player1 = hearts.players[hearts.playerInTurn];
        hearts.playTurn(player1.getPlayerID(), cardIDList.get(0));

        Player player2 = hearts.players[hearts.playerInTurn];
        hearts.playTurn(player2.getPlayerID(), cardIDList.get(1));

        Player player3 = hearts.players[hearts.playerInTurn];
        hearts.playTurn(player3.getPlayerID(), cardIDList.get(2));

        Player player4 = hearts.players[hearts.playerInTurn];
        hearts.playTurn(player4.getPlayerID(), cardIDList.get(3));
    }

    @Test
    public void initializePlayersTest() {
        setup();

        Player[] players = testUtils.getPlayerArray();
        hearts.initializePlayers(players);
        
        assertEquals(hearts.players[0].getPlayerID(), testUtils.player_1_id);
        assertEquals(hearts.players[1].getPlayerID(), testUtils.player_2_id);
        assertEquals(hearts.players[2].getPlayerID(), testUtils.player_3_id);
        assertEquals(hearts.players[3].getPlayerID(), testUtils.player_4_id);
	}

    @Test
    public void shuffleDeckTest() {
        setup();
        startGame(hearts, false, false);

        for (Player player: hearts.players) {
            assertFalse(player.getHand().isEmpty());
            assertNotNull(player.getHand());
        }
    }

    @Test
    public void firstInitiativeStartsWithTwoOfClubsTest() {
        setup();
        startGame(hearts, true, false);

        Card twoOfClubs = testUtils.getCardFromPlayerHand(hearts.players[hearts.playerInTurn], CardID.CLUB_TWO);
        assertTrue(twoOfClubs != null);
    }

    @Test void playerDoesNotPlayTwoOfClubsTest() {
        setup();
        startGame(hearts, true, true);

        Player startingPlayer = hearts.players[hearts.playerInTurn];
        
        Card jackOfClubs = testUtils.getCardFromPlayerHand(startingPlayer, CardID.CLUB_JACK);
        assertTrue(jackOfClubs != null);

        boolean validTurn = hearts.playTurn(startingPlayer.getPlayerID(), CardID.CLUB_JACK.getOrdinal());
        assertFalse(validTurn);
    }

    @Test
    public void playerPlaysOutOfTurnTest() {
        setup();
        startGame(hearts, true, true);
        boolean validTurn;

        assertTrue(hearts.playerInTurn == 0);

        validTurn = hearts.playTurn(hearts.players[2].getPlayerID(), CardID.CLUB_QUEEN.getOrdinal());
        assertFalse(validTurn);
    }

    @Test
    public void playerPlaysHeartBeforeSuitBrokenTest() {
        setup();
        startGame(hearts, true, true);
        boolean validTurn;

        simulateTrick(hearts, testUtils.getFirstSimulatedTrickCardIDs()); // Last player wins simulated trick
        validTurn = hearts.playTurn(hearts.players[3].getPlayerID(), CardID.HEART_THREE.getOrdinal());

        assertFalse(validTurn);
    }

    @Test
    public void passLeftTest() {
        setup();
        startGame(hearts, false, false);
        Player[] players = hearts.players;
    
        // Create mutable lists for pass cards
        List<Integer> player_1_passCards = new ArrayList<>(players[0].getHand().keySet()).subList(0, 3);
        List<Integer> player_2_passCards = new ArrayList<>(players[1].getHand().keySet()).subList(0, 3);
        List<Integer> player_3_passCards = new ArrayList<>(players[2].getHand().keySet()).subList(0, 3);
        List<Integer> player_4_passCards = new ArrayList<>(players[3].getHand().keySet()).subList(0, 3);

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        // Simulate the pass phase
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);
    
        List<Integer> player_1_receivedCards = new ArrayList<>(hearts.players[0].passedCards.keySet());
        List<Integer> player_2_receivedCards = new ArrayList<>(hearts.players[1].passedCards.keySet());
        List<Integer> player_3_receivedCards = new ArrayList<>(hearts.players[2].passedCards.keySet());
        List<Integer> player_4_receivedCards = new ArrayList<>(hearts.players[3].passedCards.keySet());

        Collections.sort(player_1_receivedCards);
        Collections.sort(player_2_receivedCards);
        Collections.sort(player_3_receivedCards);
        Collections.sort(player_4_receivedCards);
    
        assertTrue(player_1_passCards.equals(player_2_receivedCards));
        assertTrue(player_2_passCards.equals(player_3_receivedCards));
        assertTrue(player_3_passCards.equals(player_4_receivedCards));
        assertTrue(player_4_passCards.equals(player_1_receivedCards));
    }

    @Test
    public void passRightTest() {
        setup();
        startGame(hearts, false, false);
        Player[] players = hearts.players;

        hearts.roundPassingType = PassingPhase.RIGHT;
    
        List<Integer> player_1_passCards = new ArrayList<>(players[0].getHand().keySet()).subList(0, 3);
        List<Integer> player_2_passCards = new ArrayList<>(players[1].getHand().keySet()).subList(0, 3);
        List<Integer> player_3_passCards = new ArrayList<>(players[2].getHand().keySet()).subList(0, 3);
        List<Integer> player_4_passCards = new ArrayList<>(players[3].getHand().keySet()).subList(0, 3);

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);
    
        List<Integer> player_1_receivedCards = new ArrayList<>(hearts.players[0].passedCards.keySet());
        List<Integer> player_2_receivedCards = new ArrayList<>(hearts.players[1].passedCards.keySet());
        List<Integer> player_3_receivedCards = new ArrayList<>(hearts.players[2].passedCards.keySet());
        List<Integer> player_4_receivedCards = new ArrayList<>(hearts.players[3].passedCards.keySet());

        Collections.sort(player_1_receivedCards);
        Collections.sort(player_2_receivedCards);
        Collections.sort(player_3_receivedCards);
        Collections.sort(player_4_receivedCards);
    
        assertTrue(player_1_passCards.equals(player_4_receivedCards));
        assertTrue(player_2_passCards.equals(player_1_receivedCards));
        assertTrue(player_3_passCards.equals(player_2_receivedCards));
        assertTrue(player_4_passCards.equals(player_3_receivedCards));
    }

    @Test
    public void passAcrossTest() {
        setup();
        startGame(hearts, false, false);
        Player[] players = hearts.players;

        hearts.roundPassingType = PassingPhase.ACROSS;
    
        List<Integer> player_1_passCards = new ArrayList<>(players[0].getHand().keySet()).subList(0, 3);
        List<Integer> player_2_passCards = new ArrayList<>(players[1].getHand().keySet()).subList(0, 3);
        List<Integer> player_3_passCards = new ArrayList<>(players[2].getHand().keySet()).subList(0, 3);
        List<Integer> player_4_passCards = new ArrayList<>(players[3].getHand().keySet()).subList(0, 3);

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);
    
        List<Integer> player_1_receivedCards = new ArrayList<>(hearts.players[0].passedCards.keySet());
        List<Integer> player_2_receivedCards = new ArrayList<>(hearts.players[1].passedCards.keySet());
        List<Integer> player_3_receivedCards = new ArrayList<>(hearts.players[2].passedCards.keySet());
        List<Integer> player_4_receivedCards = new ArrayList<>(hearts.players[3].passedCards.keySet());

        Collections.sort(player_1_receivedCards);
        Collections.sort(player_2_receivedCards);
        Collections.sort(player_3_receivedCards);
        Collections.sort(player_4_receivedCards);
    
        assertTrue(player_1_passCards.equals(player_3_receivedCards));
        assertTrue(player_2_passCards.equals(player_4_receivedCards));
        assertTrue(player_3_passCards.equals(player_1_receivedCards));
        assertTrue(player_4_passCards.equals(player_2_receivedCards));
    }

    public void passKeepTest() {
        setup();
        startGame(hearts, false, false);
        Player[] players = hearts.players;

        hearts.roundPassingType = PassingPhase.KEEP;
    
        List<Integer> player_1_passCards = new ArrayList<>(players[0].getHand().keySet()).subList(0, 3);
        List<Integer> player_2_passCards = new ArrayList<>(players[1].getHand().keySet()).subList(0, 3);
        List<Integer> player_3_passCards = new ArrayList<>(players[2].getHand().keySet()).subList(0, 3);
        List<Integer> player_4_passCards = new ArrayList<>(players[3].getHand().keySet()).subList(0, 3);

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);
    
        List<Integer> player_1_receivedCards = new ArrayList<>(hearts.players[0].passedCards.keySet());
        List<Integer> player_2_receivedCards = new ArrayList<>(hearts.players[1].passedCards.keySet());
        List<Integer> player_3_receivedCards = new ArrayList<>(hearts.players[2].passedCards.keySet());
        List<Integer> player_4_receivedCards = new ArrayList<>(hearts.players[3].passedCards.keySet());

        Collections.sort(player_1_receivedCards);
        Collections.sort(player_2_receivedCards);
        Collections.sort(player_3_receivedCards);
        Collections.sort(player_4_receivedCards);
    
        assertTrue(player_1_passCards.equals(player_3_receivedCards));
        assertTrue(player_2_passCards.equals(player_4_receivedCards));
        assertTrue(player_3_passCards.equals(player_1_receivedCards));
        assertTrue(player_4_passCards.equals(player_2_receivedCards));
    }

    // TODO
    @Test 
    public void playerPassesOutOfPhase() {
        setup();
        startGame(hearts, false, true);

        List<Integer> player_1_passCards = new ArrayList<>(hearts.players[0].getHand().keySet()).subList(0, 3);
        List<Integer> player_2_passCards = new ArrayList<>(hearts.players[1].getHand().keySet()).subList(0, 3);
        List<Integer> player_3_passCards = new ArrayList<>(hearts.players[2].getHand().keySet()).subList(0, 3);
        List<Integer> player_4_passCards = new ArrayList<>(hearts.players[3].getHand().keySet()).subList(0, 3);

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);

        List<Integer> fakePassCards = new ArrayList<>(hearts.players[0].getHand().keySet()).subList(0, 3);
        hearts.passCards(hearts.players[0].getPlayerID(), fakePassCards);

        assertTrue(hearts.players[0].getHand().keySet().contains(fakePassCards.get(0)));
    }

    @Test 
    public void playerPlaysTurnOutOfPhase() {
        setup();
        startGame(hearts, false, true);

        boolean TurnDuringPassPhase = hearts.playTurn(hearts.players[0].getPlayerID(), CardID.CLUB_TWO.getOrdinal());
        assertFalse(TurnDuringPassPhase);
    }

    @Test
    public void isEndOfTrickTest() {
        setup();
        startGame(hearts, true, true);

        simulateTrick(hearts, testUtils.getFirstSimulatedTrickCardIDs());
        assertTrue(hearts.endOfTrick);
    }

    @Test
    public void calculateTrickWinnerTest() {
        setup();
        startGame(hearts, true, true);
        int player4 = 3;

        assertFalse(hearts.playerInTurn == player4);
        simulateTrick(hearts, testUtils.getFirstSimulatedTrickCardIDs());

        assertTrue(hearts.playerInTurn == player4);
    }

    @Test
    public void trickAddedToWinnerTest() {
        setup();
        startGame(hearts, true, true);

        assertTrue(hearts.players[3].getTricks().isEmpty());
        simulateTrick(hearts, testUtils.getFirstSimulatedTrickCardIDs());

        assertTrue(hearts.playerInTurn == 3);
        assertFalse(hearts.players[hearts.playerInTurn].getTricks().isEmpty());
    }

    @Test
    public void calculateRoundScoreTest() {
        setup();
        startGame(hearts, true, true);

        hearts.players[0].tricks = hearts.players[0].getHand();
        hearts.players[1].tricks = hearts.players[1].getHand();
        hearts.players[2].tricks = hearts.players[2].getHand();
        hearts.players[3].tricks = hearts.players[3].getHand();

        hearts.calculateScore();

        assertTrue(hearts.players[0].getScore() == 3);
        assertTrue(hearts.players[1].getScore() == 17);
        assertTrue(hearts.players[2].getScore() == 3);
        assertTrue(hearts.players[3].getScore() == 3);
    }

    @Test
    public void shootingTheMoonTest() {
        setup();
        startGame(hearts, true, true);
        
        testUtils.populateSimulatedShootingTheMoonHand(hearts.players[2]);

        hearts.calculateScore();
        assertTrue(hearts.players[2].getScore() == -26);
    }
    
    @Test
    public void queenOfSpadesPlayedFirstTrickTest() {
        setup();
        startGame(hearts, true, true);
        hearts.playTurn(hearts.players[0].getPlayerID(), CardID.CLUB_TWO.getOrdinal());
        
        hearts.players[1].hand.remove(CardID.CLUB_THREE.getOrdinal());

        boolean validTurn = hearts.playTurn(hearts.players[1].getPlayerID(), CardID.SPADE_QUEEN.getOrdinal());
        assertFalse(validTurn);
    }

    @Test
    public void playingWildcardWhenNotVoidTest() {
        setup();
        startGame(hearts, true, true);
        hearts.playTurn(hearts.players[0].getPlayerID(), CardID.CLUB_TWO.getOrdinal());

        boolean validTurn = hearts.playTurn(hearts.players[1].getPlayerID(), CardID.DIAMOND_SIX.getOrdinal());
        assertFalse(validTurn);
    }

    @Test
    public void detectEndOfGameTest() {
        setup();
        startGame(hearts, true, true);

        hearts.players[0].setScore(90);
        hearts.players[1].setScore(101);
        hearts.players[2].setScore(25);
        hearts.players[3].setScore(75);

        // play a valid turn to trigger the end game logic
        hearts.playTurn(hearts.players[0].getPlayerID(), CardID.CLUB_TWO.getOrdinal());

        assertTrue(hearts.isGameEnded());
        assertTrue(hearts.isGameOver());
    };

    @Test
    public void determineGameWinnerTest() {
        setup();
        startGame(hearts, true, true);

        hearts.players[0].setScore(90);
        hearts.players[1].setScore(101);
        hearts.players[2].setScore(25);
        hearts.players[3].setScore(75);

        // play a valid turn to trigger the end game logic
        hearts.playTurn(hearts.players[0].getPlayerID(), CardID.CLUB_TWO.getOrdinal());


        hearts.setGameWinner();
        assertTrue(hearts.getGameWinner().ID == hearts.players[2].getPlayerID());
    };

    @Test
    public void simulateFirstTrickTest() {
        setup();
        startGame(hearts, true, true);
        boolean validTurn;

        // Player 0 starts with 2-club
        Player player0 = hearts.players[hearts.playerInTurn];
        assertTrue(hearts.playerInTurn == 0);
        validTurn = hearts.playTurn(player0.getPlayerID(), CardID.CLUB_TWO.getOrdinal());
        assertTrue(validTurn);

        // Player 1 
        Player player1 = hearts.players[hearts.playerInTurn];
        assertTrue(hearts.playerInTurn == 1);
        validTurn = hearts.playTurn(player1.getPlayerID(), CardID.CLUB_THREE.getOrdinal());
        assertTrue(validTurn);

        // Player 2
        Player player2 = hearts.players[hearts.playerInTurn];
        assertTrue(hearts.playerInTurn == 2);
        validTurn = hearts.playTurn(player2.getPlayerID(), CardID.CLUB_QUEEN.getOrdinal());
        assertTrue(validTurn);

        // Player 3
        Player player3 = hearts.players[hearts.playerInTurn];
        assertTrue(hearts.playerInTurn == 3);
        validTurn = hearts.playTurn(player3.getPlayerID(), CardID.CLUB_ACE.getOrdinal());
        assertTrue(validTurn);

        // Player 3 won hand
        assertTrue(hearts.playerInTurn == 3);
    }

    // TODO: add a way to simulate a round & add test for round resetting, etc.
}
