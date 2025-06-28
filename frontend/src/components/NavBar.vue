<template>
  <nav class="navbar navbar-expand navbar-dark bg-dark">
    <div class="logo-container">
      <img :src="logoURL" alt="Cake Ordering" class="logo" />
      <a href="/" class="navbar-brand">Checkout Pro</a>
    </div>

    <div class="navbar-nav mr-auto">
      <li class="nav-item">
        <router-link to="/" class="nav-link">
          <font-awesome-icon icon="home" /> Home
        </router-link>
      </li>
      <li class="nav-item">
        <router-link to="/checkout" class="nav-link">
          <font-awesome-icon icon="money-check-dollar" /> Checkout
        </router-link>
      </li>
    </div>

  </nav>
</template>


<script>
// eslint-disable vue/no-unused-components
import logoURL from "@/assets/logo.png";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";

export default {
  components: { FontAwesomeIcon},
  computed: {
    logoURL() {
      return logoURL
    },
  },
  methods: {
  }
};

</script>

<style scoped>
.logo-container {
  display: flex;
  align-items: center;
}

.logo {
  width: 1.5rem;
  margin-right: 0.5rem;
}

.navbar-brand {
  font-size: 1.2rem;
  white-space: nowrap;
}

.navbar {
  height: 3.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1rem;
}

.navbar-nav {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-item {
  list-style: none;
}

.nav-link {
  color: #fff;
  text-decoration: none;
  padding: 0.5rem;
}

.nav-link:hover {
  text-decoration: underline;
}

/* Media query for small screens */
@media (max-width: 768px) {
  .navbar-brand {
    display: none;
  }
}
</style>