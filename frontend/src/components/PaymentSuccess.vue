<template>
  <div class="success-container">
    <header>
      <h3 class="success-title">Success.</h3>
      <p class="success-message">Payment approved by Mercado Pago!</p>
    </header>
  </div>
</template>

<script>

export default {
  name: "Home",
  data() {
    return {
      content: "",
    };
  },
  mounted() {

  }
};
</script>

<style scoped>
.success-container {
  background-color: #e6f9ec;
  border: 2px solid #27ae60;
  border-radius: 8px;
  padding: 32px;
  text-align: center;
}

.success-title {
  color: #27ae60;
}

.success-message {
  color: #27ae60;
  font-weight: bold;
}
</style>