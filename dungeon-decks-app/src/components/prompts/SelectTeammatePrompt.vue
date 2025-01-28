<template>
  <div v-if="isSelectingTeammate" class="select-teammate-prompt">
    <h2> Select your Teammate</h2>
    <ul>
      <li><button @click="handleSelectTeammate(playerNames[0])">{{ playerNames[0] }}</button></li>
      <li><button @click="handleSelectTeammate(playerNames[1])">{{ playerNames[1] }}</button></li>
      <li><button @click="handleSelectTeammate(playerNames[2])">{{ playerNames[2] }}</button></li>
    </ul>
  </div>
</template>

<script>
export default {
    name: 'SelectTeamatePrompt',
    props: {
        playerNames: Object,
        isSelectingTeammate: Boolean,
        handleSelectTeammate: Function
    }
}
</script>

<style scoped>
.select-teammate-prompt {
    position: fixed;
    bottom: 35vh;
    background: linear-gradient(to bottom right, #6A4C9C, red);
    color: white;
    font-family: 'Roboto', sans-serif;
    font-size: 20px;
    font-weight: 600;
    padding: 5px;
    border-radius: 8px;
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
    text-align: center;
    width: 80%;
    max-width: 500px;
    min-width: 500px;
    box-sizing: border-box;
    margin: 0;
    z-index: 1000;
    transition: transform 0.3s ease-in-out, box-shadow 0.3s ease;
    left: 50%;
    transform: translateX(-50%);
    opacity: .95;
}

ul {
  list-style: none;
  padding: 0;
}

button {
  font-size: 18px;
  background-color: rgb(0, 0, 0);
  color: white;
  border: 4px solid #f801e4;
  border-radius: 25px;
  padding: 10px 10px;
  margin: 10px 0;
  width: 70%;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

button:hover {
  background-color: rgb(10, 10, 10);
  transform: scale(1.05);
}

button:active {
  background-color: #f801e4;
  transform: scale(1);
}

button:focus {
  outline: none;
}
</style>