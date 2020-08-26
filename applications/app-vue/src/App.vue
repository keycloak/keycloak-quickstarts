<template>
  <div id="app">
    <img src="./assets/logo.png">
    <h1>{{ msg }}</h1>
    <h2>User: {{keycloak.idTokenParsed.preferred_username}}</h2>
    <div>
      <button class="btn" @click="keycloak.logout()">Logout</button>
    </div>
    <div id="wrapper">
      <div class="jwt-token">
        <h3 style="color: black;">JWT Token</h3>
        {{keycloak.idToken}}
      </div>
       <div class="jwt-token">
        <h3 style="color: black;">Info</h3>
        <ul>
          <li>clientId: {{keycloak.clientId}}</li>
          <li>Auth Server Url: {{keycloak.authServerUrl}}</li>
        </ul>
      </div>
    </div>
    <h2>Essential Links</h2>
    <ul>
      <li><a href="https://keycloak.org" target="_blank">Keycloak</a></li>
      <li><a href="https://github.com/keycloak/keycloak-quickstarts" target="_blank">Code Repo</a></li>
      <li><a href="https://twitter.com/keycloak" target="_blank">Twitter</a></li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'app',
  props: ['keycloak'],
  data () {
    return {
      msg: 'Welcome to Your Secured Vue.js App with Keycloak'
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: block;
  margin: 0 10px;
  color: #333;
  font-size: 20px;
}

a {
  color: #42b983;
}

#wrapper {
  display: flex;
  margin-top: 100px;
}

.jwt-token {
  width: 50%;
  display: block;
  padding: 20px;
  margin: 10 0 10px;
  font-size: 13px;
  line-height: 1.42857143;
  color: #333;
  word-break: break-all;
  word-wrap: break-word;
  background-color: #f5f5f5;
  border: 1px solid #ccc;
  border-radius: 4px;
  color: #d63aff;
  font-weight: bolder;
}

.btn {
    color: #fff;
    background-color: #0088ce;
    border-color: #00659c;
    padding: 6px 10px;
    font-size: 14px;
    line-height: 1.3333333;
    border-radius: 1px;
}
</style>
