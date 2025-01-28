<template>
    <div class="spades">
      <div class ="playerIDDisplay">
        <!-- Uncomment to show player's UUID -->
        <p>{{ playerID }}</p> 
      </div>
      <div v-if="gameStarted" class="gameArea">
  
        <Scoreboard :username="this.$store.getters.username" :otherPlayerNames="otherPlayerNames" :usernameToScore="usernameToScore" />
        <InvalidCardPrompt :invalidTurn="invalidTurn" :playersTurn="playersTurn" :passPhase="false" />
        <SelectTeammatePrompt :playerNames="otherPlayerNames" :isSelectingTeammate="needToSelectTeammate" :handleSelectTeammate="publishSelectTeammate"/>
        <BidPrompt :playersTurn="playersBidTurn" :bidPhase="bidPhase" :bidValue="bidValue" :handleBid="publishPlaceBid" />

        <YourTurnPrompt :playersTurn="playersTurn" :invalidTurn="invalidTurn" />
        <WinnerPrompt :trickWinnerName="trickWinnerName" :winnerName="winnerName" />
        <GameHUD
          :username="this.$store.getters.username"
          :score="usernameToScore[this.$store.getters.username]"
          :passPhase="false"
          :handlePlay="publishPlayTurn"
          :hand="this.$store.getters.hand"
          :selected="selectedCards"
        />
  
        <div class="player player-left">
          <NumberIcon class="bid-number-icon" :value="playerBidValues[0]" :title="'Bid'"/>
          <h3>{{ otherPlayerNames[0] }}</h3>
            <Card :fileName="'back_dark.png'" />
            <div v-if="this.voidCardsInPlay[otherPlayerNames[0]] != null" class="stacked-card" :style="getCardPosition(1)">
              <Card :fileName="voidCardsInPlay[otherPlayerNames[0]].imgPath" />
          </div>
        </div>
  
        <div class="player player-top">
          <NumberIcon class="bid-number-icon" :value="playerBidValues[1]" :title="'Bid'"/>
          <h3>{{ otherPlayerNames[1] }}</h3>
            <Card :fileName="'back_dark.png'" />
            <div v-if="this.voidCardsInPlay[otherPlayerNames[1]] != null" class="stacked-card" :style="getCardPosition(1)">
              <Card :fileName="voidCardsInPlay[otherPlayerNames[1]].imgPath" />
          </div>
        </div>
  
        <div class="player player-right">
          <NumberIcon class="bid-number-icon" :value="playerBidValues[2]" :title="'Bid'"/>
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
                @click="toggleCardSelection(card.id)"
              />
            </div>
          </div>
        </div>
  
        <div class="player main-player-void-cards">
          <NumberIcon class="bid-number-icon" :value="playerBidValues[3]" :title="'Bid'"/>
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
import WinnerPrompt from '../prompts/WinnerPrompt.vue';
import InvalidCardPrompt from '../prompts/InvalidCardPrompt.vue';
import Scoreboard from '../hud/Scoreboard.vue';
import YourTurnPrompt from '../prompts/YourTurnPrompt.vue';
import Card from '../objects/Card.vue'
import SquaresBackground from '../animations/SquaresBackground.vue';
import GameHUD from '../hud/GameHUD.vue';
import BidPrompt from '../prompts/BidPrompt.vue';
import SelectTeammatePrompt from '../prompts/SelectTeammatePrompt.vue';
import NumberIcon from '../hud/NumberIcon.vue';
  
  export default {
    name: 'SpadesComponent',
    components:  { 
      WinnerPrompt,
      InvalidCardPrompt,
      Scoreboard,
      YourTurnPrompt,
      Card,
      SquaresBackground,
      GameHUD,
      BidPrompt,
      SelectTeammatePrompt,
      NumberIcon
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
        selectedCards: [],
        stompClient: null,
        cardIDInPlay: null, // Tracks card ID player attempts to put in play
        otherPlayerNames: null,
        numOfSubscribtionNotifyVoidCards: 0,  // Used for sorting void cards in play
        voidCardsInPlay: {},  // Cards in middle that are in play
        
        playersTurn: false,  // Toggles when player is in turn
        playersBidTurn: false,
        
        bidValue: 0,
        playerBidValues: [],

        team: -1,
        team1: [],
        team2: [],
        needToSelectTeammate: false,
        teammateName: null,
        opponents: [],

        usernameToScore: {},
        validTurn: true,
        invalidTurn: false,
        bidPhase: false,
        
        trickWinnerName: null,
        winnerName: null,
      };
    },
    mounted() {
      this.stompClient = this.$store.getters.stompClient;
      this.gameID = this.$store.getters.gameID;
      this.otherPlayerNames = this.$store.getters.otherPlayers;
  
      this.displayName = this.$store.getters.username;
      this.gameStarted = true;
  
      this.subscribePlayTurn();
      this.subscribeGetHand();
      this.subscribeSelectTeammate();
      this.subscribeNotifySelectTeammate();
      this.subscribeNotifyTeams();

      this.subscribeBroadcastBid();
      this.subscribeNotifyBiddingPhase();
      this.subscribeNotifyPlayersBid();
      this.subscribePlaceBid();

      this.subscribeNotifyVoidCards();
      this.subscribeNotifyPlayersTurn();
      this.subscribeNotifyEndOfTrick();
      this.subscribeNotifyEndOfRound();
      this.subscribeNotifyEndOfGame();

      this.subscribeOrientationChange();
  
      this.subscribeUpdateScoreboard()
      this.publishGetHand();
    },
    methods: {
      ...mapActions(['storeGameID', 'storeOtherPlayers', 'storeHand', 'storePlayerIndex']),
  
      setPlayerOrientationRing() {     
        if (this.$store.getters.playerIndex == 0) {
            let opponents = [];
            let j = 0
            for (let tIndex = 0; tIndex < this.otherPlayerNames.length; tIndex++) {
                if (this.otherPlayerNames[tIndex] != this.teammateName) {
                    opponents[j] = this.otherPlayerNames[tIndex];
                    j += 1;
                }
            }
            
            this.otherPlayerNames[0] = opponents[0];
            this.otherPlayerNames[1] = this.teammateName;
            this.otherPlayerNames[2] = opponents[1];

            let orientationRing = [this.displayName, this.otherPlayerNames[0], this.otherPlayerNames[1], this.otherPlayerNames[2]];
            this.publishOrientationChange(orientationRing);
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
        let subscription = '/topic/spades/getHand/' + this.$store.getters.playerID.toString();
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
      subscribeSelectTeammate() {
        let subscription = '/topic/spades/selectTeammate/' + this.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
          let body = JSON.parse(message.body)
          console.log('Teammate selection On: ', body);
          this.needToSelectTeammate = Boolean(body);
        });
      },
      subscribeNotifySelectTeammate() {
        let subscription = '/topic/spades/notifySelectTeammate/' + this.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
          console.log('Notified of teammate selection: ', JSON.parse(message.body));
          this.needToSelectTeammate = true;
        });
      },
      subscribeNotifyTeams() {
        let subscription = "/topic/spades/notifyTeams/" + this.gameID.toString();
        this.stompClient.subscribe(subscription, message => {
          let teams = JSON.parse(message.body)
          console.log('Player Teams: ', teams);
          this.team1 = teams[0];
          this.team2 = teams[1];

          if (this.team1.includes(this.displayName)) {
            this.team = 0;
            // Loop through team1 and find the teammate by excluding the displayName
            this.teammateName = this.team1.find(e => e !== this.displayName);
          } 
          else if (this.team2.includes(this.displayName)) {
            this.team = 1;
            // Loop through team2 and find the teammate by excluding the displayName
            this.teammateName = this.team2.find(e => e !== this.displayName);
            console.log('Teammate found: ', this.teammateName);
          }

          console.log('Display Name:', this.displayName);
          console.log('Teammate found: ', this.teammateName);
          this.setPlayerOrientationRing();

        });
      },
      subscribeOrientationChange() {
        let subscription = '/topic/spades/orientationChange/' + this.gameID.toString();
        this.stompClient.subscribe(subscription, message => {
          let playerList = JSON.parse(message.body)
          console.log('Orientation change - sorted players:', playerList);
            
          for (let i = 0; i < playerList.length; i++) {
            let entry = playerList[i];

            if (entry == this.displayName) {
                this.storePlayerIndex(i);
                console.log("Setting new player index to: ", this.$store.getters.playerIndex)
    
                let j = 0;
                for (let i = 0; i < this.otherPlayerNames.length; i++) {
                    if (this.otherPlayerNames[i] != this.teammateName) {
                        this.opponents[j] = this.otherPlayerNames[i];
                        j += 1;
                    }
                }

                let playerIndex = this.$store.getters.playerIndex
                if (playerIndex == 0) {
                    this.otherPlayerNames[0] = this.opponents[0];
                    this.otherPlayerNames[1] = this.teammateName;
                    this.otherPlayerNames[2] = this.opponents[1];
                } 
                else if (playerIndex == 1) {
                    this.otherPlayerNames[0] = this.opponents[1];
                    this.otherPlayerNames[1] = this.teammateName;
                    this.otherPlayerNames[2] = this.opponents[0];
                }
                else if (playerIndex == 2) {
                    this.otherPlayerNames[0] = this.opponents[1];
                    this.otherPlayerNames[1] = this.teammateName;
                    this.otherPlayerNames[2] = this.opponents[0];
                }
                else if (playerIndex == 3) {
                    this.otherPlayerNames[0] = this.opponents[0];
                    this.otherPlayerNames[1] = this.teammateName;
                    this.otherPlayerNames[2] = this.opponents[1];
                }
            }
          }
        });
      },
      subscribePlaceBid() {
        let subscription = '/topic/spades/placeBid/' + this.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
          let validBid = JSON.parse(message.body)
          console.log('Bid was valid: ', validBid);
          let valid = Boolean(validBid);
          this.playersBidTurn = !valid;
        });
      },
      subscribeNotifyPlayersBid() {
        let subscription = '/topic/spades/notifyPlayersBid/' + this.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
          console.log('Notified of bid turn: ', JSON.parse(message.body));
          this.playersBidTurn = true;
        });
      },
      subscribeNotifyBiddingPhase() {
        let subscription = '/topic/spades/notifyBiddingPhase/' + this.gameID.toString();
        this.stompClient.subscribe(subscription, message => {
          let body = JSON.parse(message.body);
          console.log('Notified of bid phase: ', body);
          this.bidPhase = Boolean(body);
        });
      },
      subscribeNotifyPlayersTurn() {
        let subscription = '/topic/spades/notifyPlayersTurn/' + this.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
          let isPlayersTurn = JSON.parse(message.body);
          console.log("isPlayersTurn: ", isPlayersTurn);
          this.playersTurn = true;
        });
      },
      subscribeBroadcastBid() {
        let subscription = '/topic/spades/broadcastBid/' + this.gameID.toString();
        this.stompClient.subscribe(subscription, message => {
          let bidData = JSON.parse(message.body);
          for (let username in bidData) {
            if (Object.prototype.hasOwnProperty.call(bidData, username)) {
                let bidAmount = bidData[username]; // Get the bid amount associated with the username
                console.log('Username:', username);
                console.log('Bid Amount:', bidAmount);

                if (username === this.displayName) {
                    this.playerBidValues[3] = bidAmount;
                }
                let i = 0
                for (let name of this.otherPlayerNames) {
                    console.log('name in origin map:', name);
                    console.log('json username:', username);
                    if (username === name) {
                        console.log('Found! ', username);
                        this.playerBidValues[i] = bidAmount;
                        break;
                    }
                    i += 1;
                }
            }
        }
        });
      },
      subscribePlayTurn() {
        let subscription = '/topic/spades/playTurn/' + this.$store.getters.playerID.toString();
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
            this.invalidTurn = true;
          }
        });
      },
      subscribeNotifyVoidCards() {
        let subscription = '/topic/spades/notifyVoidCards/' + this.gameID.toString();
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
      subscribeNotifyEndOfTrick() {
        let subscription = '/topic/spades/notifyEndOfTrick/' + this.gameID.toString();
        this.stompClient.subscribe(subscription, message => {
          let name = JSON.parse(message.body);
          console.log(`[topic/notifyEndOfTrick/${this.gameID.toString()}] - trick winner name received: `, name);
          
          // this.voidCardsInPlay = {}
          this.announceTrickWinner(name)
        })
      },
      subscribeNotifyEndOfRound() {
        let subscription = '/topic/spades/notifyEndOfRound/' + this.gameID.toString();
        this.stompClient.subscribe(subscription, message => {
          let isEndOfRound = JSON.parse(message.body);
          console.log(`[topic/notifyEndOfRound/${this.gameID.toString()}] - message received: `, isEndOfRound)
          
          this.publishGetHand();
        })
      },
      subscribeUpdateScoreboard() {
        let subscription = '/topic/spades/updateScoreboard/' + this.gameID.toString();
        this.stompClient.subscribe(subscription, message => {
          let scoreboardMap = JSON.parse(message.body);
          console.log(`[topic/updateScoreboard/${this.gameID.toString()}] - message received: `, scoreboardMap)
          this.usernameToScore = scoreboardMap;
        })
      },
      subscribeNotifyEndOfGame() {
        let subscription = '/topic/spades/subscribeNotifyEndOfGame/' + this.gameID.toString();
        this.stompClient.subscribe(subscription, message => {
          this.winnerName = JSON.parse(message.body);
          console.log(`[topic/subscribeNotifyEndOfGame/${this.gameID.toString()}] - winner name: `, this.winnerName)
        })
      },
      toggleCardSelection(cardID) {
        if (this.selectedCards.includes(cardID)) {
          // If already selected, remove from selectedCards
          this.selectedCards = this.selectedCards.filter(id => id !== cardID);
        } else if (this.selectedCards.length < 1) {
          this.selectedCards.push(cardID);
        }
      },
      publishGetHand() {
        this.stompClient.publish({
          destination: "/app/spades/getHand",
          body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID': this.gameID})
        })
      },
      publishPlayTurn() {
        if (this.selectedCards != []) {
          const [card] = this.selectedCards
          this.selectedCards = [];
          this.cardIDInPlay = card
  
          // Dont allow user to start round until after announcment
          if (this.trickWinnerName == null) {
            this.stompClient.publish({
              destination: "/app/spades/playTurn",
              body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID':this.gameID, 'cardIDs': JSON.stringify([card])})
            });
          }
        }
      },
      publishSelectTeammate(teammateName) {
        console.log("User chose teammate with name: ", teammateName);
        this.teammateName = teammateName.trim();
        this.stompClient.publish({
          destination: "/app/spades/selectTeammate",
          body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID': this.gameID, 'cardIDs': JSON.stringify([teammateName])})
        })
      },
      publishOrientationChange(orientationRing) {
        this.stompClient.publish({
          destination: "/app/spades/orientationChange",
          body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID': this.gameID, 'cardIDs': JSON.stringify(orientationRing)})
        })
      },
      publishPlaceBid(bid) {
        console.log("Publishing bid: ", bid);
        console.log("bidPhase: ", this.bidPhase)
        console.log("playersBid: ", this.playersBidTurn)

        if (this.playersBidTurn && this.bidPhase) {
            console.log('placing bid with value: ', bid);
          this.bidValue = bid;
          this.stompClient.publish({
            destination: "/app/spades/placeBid",
            body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID': this.gameID, 'cardIDs': JSON.stringify([this.bidValue])})
          })
        }
      }
    }
  };
</script>
  
<style scoped>
  .spades {
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

  .bid-number-icon {
    position: absolute;
    left: 28%;
    bottom: 20%;
    z-index: 1000;
  }
  
  </style>
  