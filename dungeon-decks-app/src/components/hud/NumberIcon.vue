<template>
  <div>
    <div v-if="value > 0" class="number-icon">
      <p> {{ title }} {{ value }} </p>
    </div>
  </div>
</template>

<script>
export default {
    name: "NumberIcon",
    props: {
        value:  {
            type: Number,
            default: -1
        },
        title: {
            type: String,
            default: ''
        }
    }
}
</script>

<style scoped> 
.number-icon {
  position: fixed;
  color: white;
  background-color: rgb(214, 6, 103);
  text-shadow: 0px 0px 10px #00ffff, 0px 0px 10px #00b7ff;
  padding: 0px 8px;
  border-radius: 25px;
  opacity: .9;
}
</style>