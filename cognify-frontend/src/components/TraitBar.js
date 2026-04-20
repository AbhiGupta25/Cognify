import React from "react";

function TraitBar({ traitName, score }) {
  return (
    <div className="trait-row">
      <div className="trait-header">
        <span>{traitName}</span>
        <span>{score}</span>
      </div>
      <div className="trait-track">
        <div className="trait-fill" style={{ width: `${score}%` }} />
      </div>
    </div>
  );
}

export default TraitBar;