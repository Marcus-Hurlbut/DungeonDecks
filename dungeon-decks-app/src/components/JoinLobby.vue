
<template>
  <div class="joinGame">
    <form class="joinGameForm" @submit.prevent="start(lobbyID, displayName)">
      <label for="lobbyID">Enter the Game Code</label><br>
      <input type="text" id="lobbyID" v-model="lobbyID" name="lobbyID"><br>
      <label for="displayName">Enter your Display Name</label><br>
      <input type="text" id="displayName" v-model="displayName" name="displayName"><br><br>
      <button type="submit">Join Lobby</button>
    </form>
    <BubbleBackground />
  </div>
</template>
  
<script>
  import { Stomp } from '@stomp/stompjs';
  import { mapActions } from 'vuex';
  import { mapState } from 'vuex';
  import BubbleBackground from './animations/BubbleBackground.vue';
  import { v4 as uuidv4 } from 'uuid';

  export default {
    name: 'JoinLobby',
    components: {
      BubbleBackground,
    },
    computed: {
      ...mapState(['playerID', 'username'])
    },
    data() {
      return {
        lobbyID: "",
        displayName: "",
        stompClient: null,
        connected: false,
        connecting: false,
      }
    },
    methods: {
      ...mapActions(['storeIsLobbyCreated', 'storeStompClient', 'storePlayerID', 'storeUsername', 'storeOtherPlayer', 'storeLobbyID', 'storePlayerIndex']),

      start(lobbyID, displayName) {
        if (this.connected || this.connecting) return; 
        this.connecting = true;
        this.lobbyID = lobbyID

        const socketUrl = process.env.NODE_ENV === 'development'
          ? 'ws://localhost:8080/dungeon-decks-websocket'
          : 'wss://dungeondecks.net/dungeon-decks-websocket'
        this.stompClient = Stomp.over(() => new WebSocket(socketUrl));
        
        this.stompClient.connect({}, frame => {
          console.log('Websocket Connected: ', frame);
          this.connected = true;
          this.connecting = false;

          // Set store values
          let playerID =  uuidv4().toString();
          this.storePlayerID(playerID);
          this.storeStompClient(this.stompClient);
          this.storeUsername(displayName);
          this.storeLobbyID(lobbyID);

          this.subscribeJoinLobby();
          this.publishJoinLobby();
        }, 
        error => {
          console.error("WebSocket connection error:", error);
          this.connecting = false;
        });
      },
      subscribeJoinLobby() {
        let subscription = '/topic/' + this.$route.query.game + '/joinLobby/' + this.$store.getters.playerID
        this.stompClient.subscribe(subscription, message => {
          let otherNames = JSON.parse(message.body);
          console.log('Joined lobby successfully - Other player names: ', otherNames)
          let index = 0;

          otherNames.forEach(playerName => {
            console.log("storing username: ", playerName)
            this.storeOtherPlayer(playerName)
            index += 1;
          });

          this.storePlayerIndex(index);
          this.storeIsLobbyCreated(true);
          this.$router.push({
            path: '/lobby',
            query: {
              ...this.$route.query
            }
          });
        });
      },
      publishJoinLobby() {
        if (this.connected) {
          let dest = "/app/" + this.$route.query.game + "/joinLobby"
          this.stompClient.publish({
            destination: dest,
            body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID': this.lobbyID, 'cardIDs':"", 'name': this.$store.getters.username})
          })
        }
      },
    }
  }
</script>
  
<style scoped>
.joinGameForm {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: inline-block;
    padding: 30px 50px;
    background: linear-gradient(135deg, #6a1b9a, #ff1744);
    border-radius: 15px;
    border: 3px solid #ffffff;
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
    text-align: center;
    color: #fff;
    width: 300px;
    transition: all 0.3s ease-in-out;
    z-index: 100;
  }
  
  .joinGameForm label {
    font-size: 1.1em;
    margin-bottom: 10px;
    display: block;
    font-weight: bold;
    color: #ffffff;
  }
  
  .joinGameForm input {
    width: 100%;
    padding: 12px;
    margin: 10px 0;
    border-radius: 8px;
    border: 2px solid #fff;
    font-size: 1em;
    background: rgba(255, 255, 255, 0.3);
    color: #fff;
    text-align: center;
    transition: background 0.3s ease;
  }
  
  .joinGameForm input::placeholder {
    color: #ccc;
  }
  
  .joinGameForm input:focus {
    outline: none;
    background: rgba(255, 255, 255, 0.5);
  }
  
  .joinGameForm button {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    background-color: rgba(120, 101, 233, 0.507);
    color: #fff;
    border: none;
    font-size: 1.1em;
    cursor: pointer;
    transition: background 0.3s ease, transform 0.2s ease;
  }
  
  .joinGameForm button:hover {
    background-color: rgba(107, 82, 250, 0.507);;
    transform: scale(1.05);
  }
  
  .joinGameForm button:active {
    background-color: rgba(120, 101, 233, 0.507);
  }

</style>
  