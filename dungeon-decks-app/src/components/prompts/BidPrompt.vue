<template>
    <div>
      <div v-if="playersTurn && bidPhase" class="bid-prompt">
        <div class="prompt-content">
          <h3>Place your bid</h3>
          <p>Place a bid on how many tricks you will win this round</p>
          <input
            type="range"
            min="0"
            max="13"
            v-model="bidValue"
            class="custom-slider"
          />
          <span class="slider-value">{{ bidValue }}</span> <!-- Display the current value -->
          <button @click="handleBid(bidValue)"> Place Bid</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "BidPrompt",
    props: {
      playersTurn: {
        type: Boolean,
        default: false
      },
      bidPhase: Boolean,
      bid: Number,
      handleBid: Function
    },
    data() {
      return {
        bidValue: 0 // Initialize the slider value to 0
      }
    }
  }
  </script>
  
  <style scoped>
  /* Bid Prompt Container */
  .bid-prompt {
    position: fixed;
    bottom: 25vh;
    background: linear-gradient(to bottom left, #6A4C9C, red);
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
  }
  
  .bid-prompt .prompt-content {
    animation: fadeInUp 0.7s ease-out;
  }
  
  .bid-prompt h3 {
    font-size: 26px;
    margin-bottom: 10px;
    font-weight: 700;
    letter-spacing: 1px;
  }
  
  .bid-prompt p {
    font-size: 18px;
    margin-top: 8px;
    opacity: 0.9;
  }
  
  .bid-prompt:hover {
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  }
  
  /* Custom Slider Styles */
  .custom-slider {
    -webkit-appearance: none;
    appearance: none;
    width: 80%;
    height: 10px;
    background: linear-gradient(to right, #00ff37, #f80000); /* Gradient background */
    border-radius: 5px;
    outline: none;
    transition: background 0.3s ease-in-out;
    margin-top: 20px;
  }
  
  .custom-slider::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 20px;
    height: 20px;
    background: #fff;
    border: 2px solid #ff7e5f; /* Thumb border color */
    border-radius: 50%;
    cursor: pointer;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
    transition: background 0.3s ease, transform 0.3s ease;
  }
  
  .custom-slider::-webkit-slider-thumb:hover {
    background: #feb47b; /* Thumb color change on hover */
    transform: scale(1.2); /* Slightly enlarge the thumb on hover */
  }
  
  .custom-slider::-webkit-slider-thumb:active {
    background: #ff7e5f; /* Thumb color when clicked */
    transform: scale(1.1); /* Thumb shrinks slightly when clicked */
  }
  
  .custom-slider::-moz-range-thumb {
    width: 20px;
    height: 20px;
    background: #fff;
    border: 2px solid #ff7e5f;
    border-radius: 50%;
    cursor: pointer;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
    transition: background 0.3s ease, transform 0.3s ease;
  }
  
  .custom-slider::-moz-range-thumb:hover {
    background: #feb47b;
    transform: scale(1.2);
  }
  
  .custom-slider::-moz-range-thumb:active {
    background: #ff7e5f;
    transform: scale(1.1);
  }
  
  /* Slider Value Display */
  .slider-value {
    font-size: 18px;
    font-weight: 600;
    color: white;
    margin-top: 10px;
  }

button {
  font-size: 15px;
  background: linear-gradient(to top left, #3742d3, red);
  font-weight: bold;
  color: white;
  border: 1px solid #ffffff;
  border-radius: 25px;
  padding: 10px 10px;
  margin: 15px 0;
  width: 30%;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}
  </style>
  