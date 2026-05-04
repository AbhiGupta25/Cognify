import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/users",
});

export const signup = async (data) => {
  const res = await api.post("/signup", data);
  return res.data;
};

export const login = async (data) => {
  const res = await api.post("/login", data);
  return res.data;
};

export const fetchUserAttempts = async (userId) => {
  const res = await api.get(`/${userId}/attempts`);
  return res.data;
};

export const startDemo = async () => {
  const res = await axios.post("http://localhost:8080/api/demo/start");
  return res.data;
};
