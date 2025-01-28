<template>
    <div v-if="!isLobbyCreated">
      <form class="createLobbyForm" @submit.prevent="handleSubmit">
        <label for="displayName">Enter Your Display Name</label><br>
        <input type="text" id="displayName" v-model="displayName" name="displayName" placeholder="Your Display Name"><br><br>
        <button type="submit">Create Lobby</button>
      </form>
      <BubbleBackground />
    </div>
  </template>
  
  <script>
    import { mapActions } from 'vuex';
    import BubbleBackground from '../animations/BubbleBackground.vue';
    export default {
      name: "CreateLobbyForm",
      components: {
        BubbleBackground,
      },
      data() {
        return {
          displayName: ''
        };
      },
      props: {
        isLobbyCreated: Boolean,
      },
      methods: {
        ...mapActions(['storeUsername', 'storePlayerIndex']),
        handleSubmit() {
          this.storeUsername(this.displayName);
          this.storePlayerIndex(0);
          this.$emit('submit', this.displayName); 

          this.$router.push({
            path: '/lobby',
            query: {
              ...this.$route.query
            }
          });
        }
      }
    };
  </script>
  
  <style scoped>
  .createLobbyForm {
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
  
  .createLobbyForm label {
    font-size: 1.1em;
    margin-bottom: 10px;
    display: block;
    font-weight: bold;
    color: #ffffff;
  }
  
  .createLobbyForm input {
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
  
  .createLobbyForm input::placeholder {
    color: #ccc;
  }
  
  .createLobbyForm input:focus {
    outline: none;
    background: rgba(255, 255, 255, 0.5);
  }
  
  .createLobbyForm button {
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
  
  .createLobbyForm button:hover {
    background-color: rgba(107, 82, 250, 0.507);;
    transform: scale(1.05);
  }
  
  .createLobbyForm button:active {
    background-color: rgba(120, 101, 233, 0.507);
  }
  
  </style>
  