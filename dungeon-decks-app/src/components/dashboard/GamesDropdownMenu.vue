<template>
  <div class="dungeon-decks">
    <ul class="games-list">
      <!-- Hearts Section -->
      <li class="game-item" @click="toggleGameDropdown('hearts')">
        Hearts
        <div v-if="activeDropdown === 'hearts'" class="submenu-dropdown">
          <router-link :to="{ path: '/createLobby', query: { game: 'hearts' } }">
            <button class="menu-button">Create Lobby</button>
          </router-link>
          <router-link :to="{ path: '/joinLobby', query: { game: 'hearts' } }">
            <button class="menu-button">Join Lobby</button>
          </router-link>
        </div>
      </li>

      <!-- Spades Section -->
      <li class="game-item" @click="toggleGameDropdown('spades')">
        Spades
        <div v-if="activeDropdown === 'spades'" class="submenu-dropdown">
          <router-link :to="{ path: '/createLobby', query: { game: 'spades' } }">
            <button class="menu-button">Create Lobby</button>
          </router-link>
          <router-link :to="{ path: '/joinLobby', query: { game: 'spades' } }">
            <button class="menu-button">Join Lobby</button>
          </router-link>
        </div>
      </li>

      <!-- <li class="game-item" @click="toggleGameDropdown('spirder_solitare')"> -->
        <li class="game-item">
        Spider Solitare
        <div v-if="activeDropdown === 'spider-solitare'" class="submenu-dropdown">
          <router-link :to="{ path: '/createLobby', query: { game: 'spades' } }">
            <button class="menu-button">Create Lobby</button>
          </router-link>
          <router-link :to="{ path: '/joinLobby', query: { game: 'spades' } }">
            <button class="menu-button">Join Lobby</button>
          </router-link>
        </div>
      </li>
    </ul>
    <BubbleBackground />
  </div>
</template>

<script>
export default {
  name: "GamesDropdownMenu",
  data() {
    return {
      activeDropdown: null, // Tracks which game dropdown is active (Hearts or Spades)
    };
  },
  methods: {
    // Toggle visibility of game-specific dropdown (Hearts or Spades)
    toggleGameDropdown(game) {
      this.activeDropdown = this.activeDropdown === game ? null : game;
    },
  },
};
</script>

<style scoped>
/* List of games */
.games-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
  text-align: center;
  font-family: 'Orbitron', sans-serif;
}

.game-item {
  font-size: 1.4em;
  color: #e0e0e0;
  padding: 8px 30px;
  cursor: pointer;
  border-radius: 15px;
  font-family: 'Orbitron', sans-serif;
  letter-spacing: 2px;
  /* text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00ffff; */
  transition: 0.3s ease, transform 0.3s ease;
}

.game-item:hover {
  padding-bottom: 2em;
}

/* Submenu Dropdown for Create/Join Lobby */

.menu-button {
  background: #0c0c0c;
  color: #ffffff;
  border: 2px solid #ffffff;
  padding: 10px 30px;
  border-radius: 30px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s, box-shadow 0.3s;
  margin-bottom: 14px;
  font-family: 'Orbitron', sans-serif;
  font-size: 1.2em;
  letter-spacing: 1px;
  text-shadow: 0px 0px 10px #e97dff, 0px 0px 20px #e97dff;
  box-shadow: 0 0 10px rgba(173, 1, 253, 0.6), 0 0 20px rgba(255, 0, 34, 0.8);
}

.menu-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 0 10px rgba(255, 0, 34, 0.8), 0 0 20px rgba(173, 1, 253, 0.6);
}
</style>
