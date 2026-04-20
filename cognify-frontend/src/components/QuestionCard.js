import React from "react";

const labels = [
  "Strongly Disagree",
  "Disagree",
  "Neutral",
  "Agree",
  "Strongly Agree",
];

function QuestionCard({ question, value, onChange }) {
  return (
    <div className="card question-card">
      <div className="question-order">Q{question.displayOrder}</div>
      <p className="question-text">{question.text}</p>

      <div className="scale-grid">
        {[1, 2, 3, 4, 5].map((num) => (
          <label
            key={num}
            className={`scale-option ${value === num ? "selected" : ""}`}
          >
            <input
              type="radio"
              name={`q-${question.id}`}
              value={num}
              checked={value === num}
              onChange={() => onChange(question.id, num)}
            />
            <div className="scale-number">{num}</div>
            <div className="scale-label">{labels[num - 1]}</div>
          </label>
        ))}
      </div>
    </div>
  );
}

export default QuestionCard;