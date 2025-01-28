<template>
  <div class="dungeon-decks">
    <div class="dashboard">
      <h1 class="app-title">
        Dungeon Decks
        <img alt="Crazy Cards logo" src="@/assets/card_icon.png" class="banner-logo" />
      </h1>
      <div class="dropdown-container">
        <button class="dropdown-button">
          <img src="@/assets/burger-menu.png" alt="Menu" class="menu-icon" />
        </button>
        <ul class="options-menu">
          <router-link to="/">
            <li @click="disconnectSocket()">Main Menu</li>
          </router-link>
          <li class="has-sublist">Game Vault
            <ul class="sublist">
              <li class="has-sublist">Hearts
                <ul class="second-sublist">
                  <router-link :to="{ path: '/createLobby', query: { game: 'hearts'} }">
                    <li>Create Lobby</li>
                  </router-link>
                  <router-link :to="{ path: '/joinLobby', query: { game: 'hearts'} }">
                    <li>Join Lobby</li>
                  </router-link>
                </ul>
              </li>
              <li class="has-sublist">Spades
                <ul class="second-sublist">
                  <router-link :to="{ path: '/createLobby', query: { game: 'spades'} }">
                    <li>Create Lobby</li>
                  </router-link>
                  <router-link :to="{ path: '/joinLobby', query: { game: 'spades'} }">
                    <li>Join Lobby</li>
                  </router-link>
                </ul>
              </li>
            </ul>
          </li>
          <li>About</li>
          <router-link to="/bugReport">
            <li>Report a Bug</li>
          </router-link>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import { mapActions } from 'vuex';

export default {
  name: 'DungeonDecks',
  computed: {
    ...mapState(['stompClient', 'isLobbyCreated'])
  },
  components: {},
  props: {
    msg: String
  },
  methods: {
    ...mapActions(['storeStompClient', 'storeIsLobbyCreated']),

    disconnectSocket() {
      console.log('Disconnecting websocket: ', this.$store.getters.stompClient);
      if (this.$store.getters.stompClient && this.$store.getters.stompClient.connected){
        let socket = this.$store.getters.stompClient;
        socket.disconnect(() => {
          console.log('Websocket connection closed.');
        });
        this.storeStompClient(null);
        this.storeIsLobbyCreated(false);
      }
    }
  }
}
</script>

<style scoped>
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}

.dungeon-decks {
  background: linear-gradient(to bottom right, #b40e40, #9C27B0);
  animation: gradientAnimation 10s ease-in-out infinite;
  color: white;
  text-align: center;
  text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00ffff;
}

.dashboard {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  justify-content: center;
}

.app-title {
  color: white;
  padding: 5px 0;
  font-size: 3em;
  font-weight: bold;
  font-family:'Courier New', Courier, monospace;
  text-align: center;
  display: flex;
  align-items: center;
  gap: 10px;
}

.banner-logo {
  height: 60px;
  width: auto;
}

/* Dropdown styles */
.dropdown-container {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1000;
}

.dropdown-button {
  background-color: transparent;
  border: none;
  cursor: pointer;
}

.menu-icon {
  height: 70px;
}

.options-menu {
  display: none;
  position: absolute;
  left: 0;
  width: 200px;
  background-color: #9C27B0;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  list-style-type: none;
  margin: 0;
  padding: 4px;
}

.dropdown-container:hover .options-menu {
  display: block;
}

.options-menu li {
  padding: 10px;
  cursor: pointer;
  color: white;
}

.options-menu li:hover {
  background: linear-gradient(to right, #9C27B0, #D50032, #3D5AFE);
  border-radius: 10px;
  transform: translateY(-3px);
}

/* Sublist for Game Vault */
.has-sublist {
  position: relative; /* Ensures sublist is positioned correctly */
}

.has-sublist:hover .sublist {
  display: block;
}

.sublist {
  display: none;
  position: absolute;
  top: 0;
  left: 100%;
  background-color: #7b1fa2;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  list-style-type: none;
  margin: 0;
  padding: 4px;
  width: 180px;
}

.sublist li {
  padding: 8px;
  cursor: pointer;
  color: white;
}

.sublist li:hover {
  background-color: #b40e40;
}

/* Second-level sublist under "Hearts" */
.has-sublist > .second-sublist {
  display: none;
}

.has-sublist:hover > .second-sublist {
  display: block;
}

.second-sublist {
  display: none;
  position: absolute;
  top: 0;
  left: 100%;
  background-color: #8e24aa;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  list-style-type: none;
  margin: 0;
  padding: 4px;
  width: 180px;
}

.second-sublist li {
  padding: 8px;
  cursor: pointer;
  color: white;
}

.second-sublist li:hover {
  background-color: #b40e40;
}
</style>
