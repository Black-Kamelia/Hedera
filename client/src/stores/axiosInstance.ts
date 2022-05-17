import axios from "axios";

const api = import.meta.env.DEV
  ? axios.create({ baseURL: "http://localhost:8080" })
  : axios.create();

export default api;
