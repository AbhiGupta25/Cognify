import React, { useEffect, useState } from "react";
import { fetchQuestions, submitAssessment } from "../api/assessmentApi";
import QuestionCard from "../components/QuestionCard";

function AssessmentPage({ onAssessmentComplete }) {
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadQuestions = async () => {
      try {
        const data = await fetchQuestions();
        setQuestions(data);
      } catch (err) {
        setError("Failed to load questions from the backend.");
      } finally {
        setLoading(false);
      }
    };

    loadQuestions();
  }, []);

  const handleAnswerChange = (questionId, value) => {
    setAnswers((prev) => ({
      ...prev,
      [questionId]: value,
    }));
  };

  const handleSubmit = async () => {
    setError("");

    if (questions.length === 0) {
      return;
    }

    const unanswered = questions.filter((question) => !answers[question.id]);
    if (unanswered.length > 0) {
      setError(`Please answer all questions. ${unanswered.length} remaining.`);
      return;
    }

    const payload = {
      userId: Number(localStorage.getItem("userId")),
      answers: questions.map((question) => ({
        questionId: question.id,
        numericAnswer: answers[question.id],
        selectedOption: null,
      })),
    };

    try {
      setSubmitting(true);
      const result = await submitAssessment(payload);
      onAssessmentComplete(result);
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        "Assessment submission failed. Check the backend and try again.";
      setError(message);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="page-shell">
        <div className="card">Loading questions...</div>
      </div>
    );
  }

  return (
    <div className="page-shell">
      <div className="page-header">
        <div>
          <div className="eyebrow">Cognify Assessment</div>
          <h1>Behavioral Personality Index</h1>
          <p>
            Answer each question on a 1-5 scale. Cognify will turn your responses into trait scoring, MBTI alignment,
            contradiction analysis, confidence, and a structured behavioral report.
          </p>
        </div>

        <div className="progress-box">
          <div>Answered</div>
          <strong>
            {Object.keys(answers).length} / {questions.length}
          </strong>
        </div>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div className="questions-stack">
        {questions.map((question) => (
          <QuestionCard
            key={question.id}
            question={question}
            value={answers[question.id] || null}
            onChange={handleAnswerChange}
          />
        ))}
      </div>

      <div className="actions-row">
        <button className="primary-btn" onClick={handleSubmit} disabled={submitting}>
          {submitting ? "Submitting..." : "Generate My Profile"}
        </button>
      </div>
    </div>
  );
}

export default AssessmentPage;
