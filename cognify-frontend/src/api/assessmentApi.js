import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/assessments",
});

export const fetchQuestions = async () => {
  const res = await api.get("/questions");
  return res.data;
};

export const submitAssessment = async (payload) => {
  const res = await api.post("/submit", payload);
  return res.data;
};

export const fetchAssessmentResult = async (attemptId, userId) => {
  const res = await api.get(`/${attemptId}?userId=${userId}`);
  return res.data;
};

export const compareAttempts = async (oldId, newId) => {
  const res = await api.get(
    `/compare?oldAttemptId=${oldId}&newAttemptId=${newId}`
  );
  return res.data;
};

export const analyzeSimulation = async (payload) => {
  const res = await axios.post("http://localhost:8080/api/simulations/analyze", payload);
  return res.data;
};
