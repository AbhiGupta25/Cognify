import React from "react";
import TraitBar from "./TraitBar";

function ResultCard({ result }) {
  return (
    <div className="card result-card">
      <div className="result-top">
        <div>
          <div className="eyebrow">Closest Alignment</div>
          <h2>{result.mbtiType}</h2>
        </div>
        <div className="result-meta">
          <div>Confidence: {result.confidenceScore}</div>
          <div>Contradictions: {result.contradictionCount}</div>
          <div>Attempt ID: {result.attemptId}</div>
        </div>
      </div>

      <p className="summary">{result.summary}</p>

      <div className="traits-section">
        <h3>Trait Scores</h3>
        {result.traitScores.map((trait) => (
          <TraitBar
            key={trait.traitName}
            traitName={trait.traitName}
            score={trait.score}
          />
        ))}
      </div>
    </div>
  );
}

export default ResultCard;