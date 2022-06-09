import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";
import "./index.css";
const app = createApp(App);
const pinia = createPinia();

pinia.use(() => ({ router }));
app.use(pinia);
app.use(router);

app.mount("#app");
