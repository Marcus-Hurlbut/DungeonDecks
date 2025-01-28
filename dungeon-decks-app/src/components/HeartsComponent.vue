<template>
  <div class="startHearts">
    <div class ="playerIDDisplay">
      <!-- Uncomment to show player's UUID -->
      <!-- <p>{{ playerID }}</p>  -->
    </div>
    <div v-if="gameStarted" class="gameArea">

      <Scoreboard :username="this.$store.getters.username" :otherPlayerNames="otherPlayerNames" :usernameToScore="usernameToScore" />
      <InvalidCardPrompt :invalidTurn="invalidTurn" :playersTurn="playersTurn" :passPhase="passPhase" />
      <PassingPhasePrompt :passPhase="passPhase" :playerPassedCards="playerPassedCards" />
      <YourTurnPrompt :playersTurn="playersTurn" :invalidTurn="invalidTurn" />
      <WinnerPrompt :trickWinnerName="trickWinnerName" :winnerName="winnerName" />
      <GameHUD
        :username="this.$store.getters.username"
        :score="usernameToScore[this.$store.getters.username]"
        :passPhase="passPhase"
        :handlePass="publishPassCards"
        :handlePlay="publishPlayTurn"
        :hand="this.$store.getters.hand"
        :selected="selectedCards"
      />


      <div class="player player-left">
        <h3>{{ otherPlayerNames[0] }}</h3>
          <Card :fileName="'back_dark.png'" />
          <div v-if="this.voidCardsInPlay[otherPlayerNames[0]] != null" class="stacked-card" :style="getCardPosition(1)">
            <Card :fileName="voidCardsInPlay[otherPlayerNames[0]].imgPath" />
        </div>
      </div>

      <div class="player player-top">
        <h3>{{ otherPlayerNames[1] }}</h3>
          <Card :fileName="'back_dark.png'" />
          <div v-if="this.voidCardsInPlay[otherPlayerNames[1]] != null" class="stacked-card" :style="getCardPosition(1)">
            <Card :fileName="voidCardsInPlay[otherPlayerNames[1]].imgPath" />
        </div>
      </div>

      <div class="player player-right">
        <h3>{{ otherPlayerNames[2] }}</h3>
          <Card :fileName="'back_dark.png'" />
          <div v-if="this.voidCardsInPlay[otherPlayerNames[2]] != null" class="stacked-card" :style="getCardPosition(1)">
            <Card :fileName="voidCardsInPlay[otherPlayerNames[2]].imgPath" />
          </div>
      </div>

      <div class="player main-player">
        <div class="main-player-hand">
          <div v-for="(card, index) in Object.values(this.$store.getters.hand).slice().sort((a, b) => a.order - b.order)" :key="index" class="hand">
            <Card
              :fileName="card.imgPath"
              :isSelected="this.selectedCards.includes(card.id)"
              @click="toggleCardSelection(card.id, passPhase)"
            />
          </div>
        </div>
      </div>

      <div class="player main-player-void-cards">
        <h3> {{ this.$store.getters.username  }} </h3>
          <Card :fileName="'back_light.png'" />
          <div v-if="this.voidCardsInPlay[this.$store.getters.username] != null" class="stacked-card" :style="getCardPosition(1)">
            <Card :fileName="voidCardsInPlay[this.$store.getters.username].imgPath"/>
        </div>
      </div>
      <SquaresBackground />
    </div>
  </div>
</template>
  
<script>
import { mapActions } from 'vuex';
import { mapState } from 'vuex';
import WinnerPrompt from './prompts/WinnerPrompt.vue';
import PassingPhasePrompt from './prompts/PassingPhasePrompt.vue';
import InvalidCardPrompt from './prompts/InvalidCardPrompt.vue';
import Scoreboard from './hud/Scoreboard.vue';
import YourTurnPrompt from './prompts/YourTurnPrompt.vue';
import Card from './objects/Card.vue'
import SquaresBackground from './animations/SquaresBackground.vue';
import GameHUD from './hud/GameHUD.vue';

export default {
  name: 'HeartsComponent',
  components:  { 
    WinnerPrompt,
    PassingPhasePrompt,
    InvalidCardPrompt,
    Scoreboard,
    YourTurnPrompt,
    Card,
    SquaresBackground,
    GameHUD
  },
  computed: {
    ...mapState(['isLobbyCreated', 'otherPlayers', 'username', 'stompClient', 'gameID', 'playerID', 'playerIndex', 'hand']),
  },
  props: {
    msg: String
  },
  data() {
    return {
      gameID: null,
      gameStarted: false,
      playerCards: {},
      playerPassCards: [],
      selectedCards: [],  // Cards highlighted for passing
      stompClient: null,
      passPhase: true,
      playerPassedCards: false,
      cardIDInPlay: null, // Tracks card ID player attempts to put in play
      otherPlayerNames: null,
      numOfSubscribtionNotifyVoidCards: 0,  // Used for sorting void cards in play
      voidCardsInPlay: {},  // Cards in middle that are in play
      playersTurn: false,  // Toggles when player is in turn
      usernameToScore: {},
      validTurn: true,
      invalidTurn: false,
      
      trickWinnerName: null,
      winnerName: null,
    };
  },
  mounted() {
    this.stompClient = this.$store.getters.stompClient;
    this.gameID = this.$store.getters.gameID;
    this.otherPlayerNames = this.$store.getters.otherPlayers;
    this.setPlayerOrientationRing();

    this.displayName - this.$store.getters.username;
    this.gameStarted = true;
    this.passPhase = true;
    this.playerPassedCards = false;

    this.subscribePlayTurn();
    this.subscribeGetHand();
    this.subscribePassCards();
    this.subscribeNotifyPassingPhase();
    this.subscribeNotifyVoidCards();
    this.subscribeNotifyPlayersTurn();
    this.subscribeNotifyEndOfTrick();
    this.subscribeNotifyEndOfRound();
    this.subscribeNotifyEndOfGame();

    this.subscribeUpdateScoreboard()
    this.publishGetHand();
  },
  methods: {
    ...mapActions(['storeGameID', 'storeOtherPlayers', 'storeHand']),

    setPlayerOrientationRing() {
      let otherPlayer_1 = this.otherPlayerNames[0]
      let otherPlayer_2 = this.otherPlayerNames[1]
      let otherPlayer_3 = this.otherPlayerNames[2]

      switch (this.$store.getters.playerIndex) {
        case 1:
          this.otherPlayerNames[0] = otherPlayer_2;
          this.otherPlayerNames[1] = otherPlayer_3;
          this.otherPlayerNames[2] = otherPlayer_1;
          break;
        case 2:
          this.otherPlayerNames[0] = otherPlayer_3;
          this.otherPlayerNames[1] = otherPlayer_1;
          this.otherPlayerNames[2] = otherPlayer_2;
          break;
        default:
          break;
      }

      this.storeOtherPlayers(this.otherPlayerNames);
    },

    getUsernameCardPlayed(username) {
      return this.voidCardsInPlay[username].imgPath;
    },

    getUsernameScore(username) {
      return this.usernameToScore[username] || 0;
    },

    announceTrickWinner(winnerName) {
      this.trickWinnerName = winnerName;
      setTimeout(() => {
        this.voidCardsInPlay = {}
        this.trickWinnerName = null;
      }, 4000);
    },
    getCardPosition(index) {
      const offset = 10;
      const xOffset = index * offset;
      const yOffset = index * offset;

      return {
        transform: `translate(${xOffset}px, ${yOffset}px)`
      };
    },
    subscribeGetHand() {
      let subscription = '/topic/hearts/getHand/' + this.$store.getters.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let hand = JSON.parse(message.body);
        this.playerCards = {}

        let index = 0;
        for (const [id, card] of Object.entries(hand)) {
          this.playerCards[id] = {
            id: id,
            name: card.name,
            suit: card.suit,
            value: card.value,
            imgPath: card.imgPath,
            order: index
          };
          index += 1;
        }

        this.storeHand(this.playerCards);
        console.log('Starting cards received: ', this.playerCards);
      });
    },
    subscribePassCards() {
      let subscription = '/topic/hearts/passCards/' + this.$store.getters.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let passedCards = JSON.parse(message.body);

        if (passedCards === false) {
          console.log("Still waiting on passed cards - subscribing to topic notifyPassCardsReceived");
          this.subscribeNotifyPassCardsReceived();
        } else {
          console.log('Passed Cards received: ', passedCards);

          for (const [id, card] of Object.entries(passedCards)) {
            this.playerCards[id] = {
              id: id,
              suit: card.suit,
              value: card.value,
              imgPath: card.imgPath,
            };
        }
        }
      });
    },
    subscribeNotifyPlayersTurn() {
      let subscription = '/topic/hearts/notifyPlayersTurn/' + this.gameID.toString() + '/' + this.$store.getters.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let isPlayersTurn = JSON.parse(message.body);
        console.log("isPlayersTurn: ", isPlayersTurn);
        this.playersTurn = true;
      });
    },
    subscribePlayTurn() {
      let subscription = '/topic/hearts/playTurn/' + this.$store.getters.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        this.validTurn = JSON.parse(message.body);
        console.log('Card placed was valid: ', this.validTurn);

        if (this.validTurn === true) {
          delete this.playerCards[this.cardIDInPlay];
          this.storeHand(this.playerCards);

          this.cardIDInPlay = null;
          this.playersTurn = false;
          this.invalidTurn = false;
        } else {
          console.log("Setting invalid turn to true - playerPassedCards: ", this.playerPassedCards);
          this.invalidTurn = true;
        }
      });
    },
    subscribeNotifyVoidCards() {
      let subscription = '/topic/hearts/notifyVoidCards/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let usernameToVoidCards = JSON.parse(message.body);
        console.log("Void cards in play updated: ", usernameToVoidCards);

        let playedByUsername = Object.keys(usernameToVoidCards)[0];
        let cardIDtoVoidCard = usernameToVoidCards[playedByUsername];

        for (const [id, voidCard] of Object.entries(cardIDtoVoidCard)) {
          this.voidCardsInPlay[playedByUsername] = {
            id: id,  
            suit: voidCard.suit,
            value: voidCard.value,
            imgPath: voidCard.imgPath,
            order: this.numOfSubscribtionNotifyVoidCards,
            playedBy: playedByUsername
          };
        }
        this.numOfSubscribtionNotifyVoidCards += 1;
      });
    },
    subscribeNotifyPassingPhase() {
      let subscription = '/topic/hearts/notifyPassingPhase/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let inPassPhase = JSON.parse(message.body);
        console.log('Notified of passing phase - In pass phase: ', inPassPhase);
        this.passPhase = Boolean(inPassPhase);

        if (this.passPhase === true) {
          this.playerPassedCards = false;
        }
      });
    },
    subscribeNotifyPassCardsReceived() {
      let subscription = '/topic/hearts/notifyPassCardsReceived/' + this.$store.getters.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let passedCards = JSON.parse(message.body);

        let index = 0;
        for (const [id, card] of Object.entries(passedCards)) {
          console.log(`Assigning imgPath for card ID ${id}:`, card.imgPath);
          this.playerCards[id] = {
            id: id,
            name: card.name,
            suit: card.suit,
            value: card.value,
            imgPath: card.imgPath,
            order: index
          };
          index += 1;
        }
      });
    },
    subscribeNotifyEndOfTrick() {
      let subscription = '/topic/hearts/notifyEndOfTrick/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let name = JSON.parse(message.body);
        console.log(`[topic/hearts/notifyEndOfTrick/${this.gameID.toString()}] - trick winner name received: `, name);
        
        // this.voidCardsInPlay = {}
        this.announceTrickWinner(name)
      })
    },
    subscribeNotifyEndOfRound() {
      let subscription = '/topic/hearts/notifyEndOfRound/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let isEndOfRound = JSON.parse(message.body);
        console.log(`[topic/hearts/notifyEndOfRound/${this.gameID.toString()}] - message received: `, isEndOfRound)
        
        this.publishGetHand();
      })
    },
    subscribeUpdateScoreboard() {
      let subscription = '/topic/hearts/updateScoreboard/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let scoreboardMap = JSON.parse(message.body);
        console.log(`[topic/hearts/updateScoreboard/${this.gameID.toString()}] - message received: `, scoreboardMap)
        this.usernameToScore = scoreboardMap;
      })
    },
    subscribeNotifyEndOfGame() {
      let subscription = '/topic/hearts/subscribeNotifyEndOfGame/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        this.winnerName = JSON.parse(message.body);
        console.log(`[topic/hearts/subscribeNotifyEndOfGame/${this.gameID.toString()}] - winner name: `, this.winnerName)
      })

    },
    toggleCardSelection(cardID) {
      if (!this.passPhase) {
        if (this.selectedCards.length < 1) {
          this.selectedCards.push(cardID);
        } else {
          this.selectedCards = []
        }

      }
      else if (this.selectedCards.includes(cardID)) {
        // If already selected, remove from selectedCards
        this.selectedCards = this.selectedCards.filter(id => id !== cardID);
        this.playerPassCards = this.playerPassCards.filter(id => id !== cardID);
      } else if (this.selectedCards.length < 3) {
        this.selectedCards.push(cardID);
        this.playerPassCards.push(cardID);
      }
    },
    publishPassCards() {
      if (this.passPhase) {
        const [card1, card2, card3] = this.selectedCards;
        console.log("Passing cards: ", this.selectedCards);
        delete this.playerCards[card1];
        delete this.playerCards[card2];
        delete this.playerCards[card3]; 

        this.storeHand(this.playerCards);

        this.stompClient.publish({
          destination: "/app/hearts/passCards",
          body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID':this.gameID, 'cardIDs': JSON.stringify([card1, card2, card3])})
        });

        this.selectedCards = [];
        this.playerPassCards = []; 
        this.playerPassedCards = true;
      }
    },
    publishGetHand() {
      this.stompClient.publish({
        destination: "/app/hearts/getHand",
        body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID': this.gameID})
      })
    },
    publishPlayTurn() {
      if (!this.passPhase && this.selectedCards != []) {
        const [card] = this.selectedCards
        this.selectedCards = [];
        this.cardIDInPlay = card

        // Dont allow user to start round until after announcment
        if (this.trickWinnerName == null) {
          this.stompClient.publish({
            destination: "/app/hearts/playTurn",
            body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID':this.gameID, 'cardIDs': JSON.stringify([card])})
          });
        }
      }
    },
  }
};
</script>

<style scoped>
.startHearts {
  height: 100vh;
  color: white;
  text-align: center;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center; 
  align-items: center; 
}

.playerIDDisplay {
  position: absolute;
  top: 18%;
  left: 1%;
  color: wheat;
  background-color: black;
}

.gameArea {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.player {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
}
/* Main player positioning */
.main-player {
  bottom: 0%;
  position: absolute;
}

.main-player-hand {
  position: relative;
  bottom: 0%;
  transform: translate(-5%, -100%);
}

/* Players hand stacking formatting */
.hand {
  width: 50px;
  height: 70px;
  border-radius: 5px;
  margin: 5px;
  display: inline-block;
  z-index: 0;
}

.main-player-void-cards {
  position: absolute;
  top: 25%;
}

.stacked-card {
  position: absolute;
  top: 0;
  left: 0;
  transition: transform 0.3s ease-in-out;
}

/* Other players cards position */
.player-top {
  top: 2%;
}

.player-right {
  right: 30%;
  bottom: 70%;
}

.player-left {
  left: 30%;
  bottom: 70%;
}

/* Players name tags */
.player-left h3, .player-right h3, .player-top h3, .main-player-void-cards h3 {
  position: absolute;
  color: #f9e3fc;
  font-size: 20px;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  z-index: 10;
  font-family: 'Lobster', sans-serif;
  text-align: center;
  padding: 6px 10px;
  border-radius: 12px;
  background: linear-gradient(45deg, #5d5bee, #fa00d0);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00b7ff;
}
</style>
