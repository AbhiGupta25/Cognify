import React from "react";

function ResultPage({ user, result, onRestart, onBackToDashboard, onOpenSimulation }) {
  const isDemoProfile = user?.email === "judge.demo@cognify.app";
  const sections = [
    { title: "Core Profile", text: result.coreProfile },
    { title: "Decision Pattern", text: result.decisionPattern },
    { title: "Social Pattern", text: result.socialPattern },
    { title: "Stress Pattern", text: result.stressPattern },
    { title: "Behavioral Pattern", text: result.behavioralPattern },
    { title: "Cognitive Pattern", text: result.cognitivePattern },
    { title: "Adaptive Pattern", text: result.adaptivePattern },
    { title: "Contradiction Analysis", text: result.contradictionAnalysis },
  ];

  return (
    <div className="report-layout">
      <section className="hero-card report-hero">
        <div>
          <div className="eyebrow">Behavioral Intelligence Report</div>
          {isDemoProfile && <div className="demo-profile-badge">Demo Profile</div>}
          <h2>{result.mbtiType}</h2>
          <p className="hero-subtitle">{result.expandedMbtiType}</p>
        </div>

        <div className="hero-badges">
          <MetricPill label="Archetype" value={result.archetype} />
          <MetricPill label="Confidence" value={`${result.confidenceScore}/100`} />
          <MetricPill label="Contradictions" value={String(result.contradictionCount)} />
          <MetricPill label="Attempt ID" value={String(result.attemptId)} />
        </div>
      </section>

      <section className="content-grid report-grid">
        <div className="card spotlight-card">
          <div className="eyebrow">Executive Summary</div>
          <h3>{result.archetype}</h3>
          <p className="lead-text">{result.summary}</p>
        </div>

        <div className="card glossary-card">
          <div className="eyebrow">MBTI Decode</div>
          <h3>
            {result.mbtiType} means {result.expandedMbtiType}
          </h3>
          <p>{result.mbtiExplanation}</p>
        </div>
      </section>

      <section className="section-grid">
        {sections.map((section) => (
          <article className="card section-card" key={section.title}>
            <div className="eyebrow">{section.title}</div>
            <p>{section.text}</p>
          </article>
        ))}
      </section>

      <section className="card trait-card">
        <div className="section-header">
          <div>
            <div className="eyebrow">Trait Scores</div>
            <h3>Behavioral signal map</h3>
          </div>
        </div>

        <div className="trait-stack">
          {(result.traitScores || []).map((trait) => (
            <div className="trait-row premium" key={trait.traitName}>
              <div className="trait-header">
                <span>{trait.traitName}</span>
                <span>{trait.score}/100</span>
              </div>
              <div className="trait-track">
                <div className="trait-fill" style={{ width: `${trait.score}%` }} />
              </div>
            </div>
          ))}
        </div>
      </section>

      <div className="actions-row report-actions">
        <button className="secondary-btn" onClick={onBackToDashboard}>
          Back to Dashboard
        </button>
        <button className="secondary-btn" onClick={onOpenSimulation}>
          Simulate a Real-Life Situation
        </button>
        <button className="primary-btn" onClick={onRestart}>
          Retake Assessment
        </button>
      </div>
    </div>
  );
}

function MetricPill({ label, value }) {
  return (
    <div className="metric-pill">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

export default ResultPage;
