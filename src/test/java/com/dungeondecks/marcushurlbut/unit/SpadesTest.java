package com.dungeondecks.marcushurlbut.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.Hearts;
import com.dungeondecks.marcushurlbut.games.Spades;
import com.dungeondecks.marcushurlbut.util.TestUtils;
import com.dungeondecks.marcushurlbut.utils.CardID;

public class SpadesTest {
    private static UUID gameID;
    private static Spades spades;
    private static TestUtils testUtils;

    private static void setup() {
        gameID = UUID.randomUUID();
        spades = new Spades(gameID);
        testUtils = new TestUtils();
    }

    private void startGame(Spades spade, boolean skipBiddingPhase, boolean skipTeamSelectionPhase, boolean useFakeDeck) {
        spade.initializePlayers(testUtils.getPlayerArray());
        
        if (!useFakeDeck) {
            spade.initialize(spade.players);
        } else {
            spade.setFirstPlayerInitiative();
            spade.active = true;
            testUtils.populateFakeDeck(spade.players);
        }

        if (skipBiddingPhase) {
            spade.biddingPhaseComplete = true;
        }

        if (skipTeamSelectionPhase) {
            spades.teamSelectionPhaseComplete = true;
        }
    }

    @Test
    public void initializePlayersTest() {
        setup();

        Player[] players = testUtils.getPlayerArray();
        spades.initializePlayers(players);
        
        assertEquals(spades.players[0].getPlayerID(), testUtils.player_1_id);
        assertEquals(spades.players[1].getPlayerID(), testUtils.player_2_id);
        assertEquals(spades.players[2].getPlayerID(), testUtils.player_3_id);
        assertEquals(spades.players[3].getPlayerID(), testUtils.player_4_id);
    }
    
    @Test
    public void shuffleDeckTest() {
        setup();
        startGame(spades, false, false, false);

        for (Player player: spades.players) {
            assertFalse(player.getHand().isEmpty());
            assertNotNull(player.getHand());
        }
    }

    public void playerBidsOutOfPhase() {}
    public void playerSelectsTeammateOutOfPhase() {}

    @Test
    public void playerPlaysOutOfTurnTest() {
        setup();
        startGame(spades, true, true, true);
        spades.playerInTurn = 0;

        Player player = spades.players[1];
        boolean outOfTurn = spades.playTurn(player.getPlayerID(), CardID.DIAMOND_TEN.getOrdinal());

        assertFalse(outOfTurn);

        player = spades.players[0];
        boolean inTurn = spades.playTurn(player.getPlayerID(), CardID.CLUB_JACK.getOrdinal());

        assertTrue(inTurn);
    }

    // TODO: Finish tests
//     public void playerPlaysSpadesBeforeSuitBrokenTest() {}
//     public void playerBidsOutOfTurnTest() {};

//     public void hostTeammateSelectionTest() {};
//     public void playerReorientationTest() {};

//     public void isEndOfTrickTest() {};
//     public void calculateTrickWinnerTest() {};
//     public void trickAddedToWinnerTest() {};

//     public void calculateRoundScoreTest() {};
//     public void playingWildcardWhenNotVoidTest() {};

//     public void detectEndOfGameTest() {};
//     public void determineGameWinnerTest() {};
}
