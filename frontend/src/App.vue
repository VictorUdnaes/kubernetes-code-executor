<script setup>

import { ref } from 'vue';
import axiosInstance from "./util/axiosInstance.js";

let exampleCode = String.raw`class HelloWorld {
  public static void main(String[] args) {
    System.out.println(\"Hello from pod!\");
  }
}`;

const code = ref(exampleCode);
const output = ref('');


function submitCode() {
  output.value = 'Executing...';
  axiosInstance({
    method: 'post',
    url: '/run-code',
    data: code.value
  }).then((response) => {
    console.log(response.data);
    output.value = response.data;
  }).catch((error) => {
    console.error(error);
  });
}
</script>

<template>
  <div class="container">
    <h1>Kubernetes Code Runner</h1>
    <div class="input">
      <textarea v-model="code"></textarea>
    </div>
    <button @click="submitCode">Run</button>
    <h3>Output:</h3>
    <div class="output">
      <p>{{output}}</p>
    </div>
  </div>
</template>

<style scoped>

  .container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
    width: 100%;
  }

  .output {
    white-space: pre-wrap;
    height: 300px;
  }

  p {
    margin-left: 20px;
    font-weight: bold;
  }

  textarea {
    height: 250px;
  }

  textarea, .output {
    width: 500px;
    font-size: 16px;
    padding: 10px;
    border: 1px solid #888888;
    color: black;
    border-radius: 5px;
    margin-top: 20px;
    background-color: rgba(239, 239, 239, 0.9);
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  }

  button {
    padding: 10px 20px;
    font-size: 16px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    margin-top: 20px;
    cursor: pointer;
  }

  button:hover {
    background-color: #0056b3;
  }

  h1 {
    color: #007bff;
    margin-top: -50px;
  }

  h3 {
    color: black;
    margin-bottom: 0;
  }
</style>
