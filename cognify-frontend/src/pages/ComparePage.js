import React, { useEffect, useState } from "react";
import { compareAttempts } from "../api/assessmentApi";
import { fetchUserAttempts } from "../api/userApi";

function ComparePage({ user, initialSelection, onBack }) {
  const [attempts, setAttempts] = useState([]);
  const [oldAttemptId, setOldAttemptId] = useState(initialSelection?.oldAttemptId || "");
  const [newAttemptId, setNewAttemptId] = useState(initialSelection?.newAttemptId || "");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadAttempts = async () => {
      try {
        const data = await fetchUserAttempts(user.id);
        setAttempts(data);

        setOldAttemptId((current) => current || (data[1]?.attemptId ? String(data[1].attemptId) : ""));
        setNewAttemptId((current) => current || (data[0]?.attemptId ? String(data[0].attemptId) : ""));
      } catch (err) {
        setError("We could not load your assessment history.");
      }
    };

    loadAttempts();
  }, [user.id]);

  const selectedOld = attempts.find((attempt) => String(attempt.attemptId) === String(oldAttemptId));
  const selectedNew = attempts.find((attempt) => String(attempt.attemptId) === String(newAttemptId));

  const handleCompare = async () => {
    setError("");

    if (!oldAttemptId || !newAttemptId) {
      setError("Choose two attempts to compare.");
      return;
    }

    if (oldAttemptId === newAttemptId) {
      setError("Select two different attempts.");
      return;
    }

    try {
      setLoading(true);
      const data = await compareAttempts(oldAttemptId, newAttemptId);
      setResult(data);
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        "Failed to compare attempts. Make sure both attempt IDs exist.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="compare-layout">
      <section className="hero-card compare-hero">
        <div>
          <div className="eyebrow">Retest Comparison</div>
          <h2>See what stayed stable and what evolved.</h2>
          <p>
            Compare any two attempts from the same account to understand whether the shift is structural, situational,
            or simply a refinement of the same personality core.
          </p>
        </div>
      </section>

      <section className="card compare-selector-card">
        <div className="compare-selector-grid">
          <AttemptSelect
            label="Earlier attempt"
            value={oldAttemptId}
            attempts={attempts}
            onChange={setOldAttemptId}
          />
          <AttemptSelect
            label="Later attempt"
            value={newAttemptId}
            attempts={attempts}
            onChange={setNewAttemptId}
          />
        </div>

        <div className="compare-preview-grid">
          <AttemptPreview title="Old profile" attempt={selectedOld} />
          <AttemptPreview title="New profile" attempt={selectedNew} />
        </div>

        <div className="actions-row left">
          <button className="primary-btn" onClick={handleCompare} disabled={loading}>
            {loading ? "Comparing..." : "Generate Comparison"}
          </button>
          <button className="secondary-btn" onClick={onBack}>
            Back
          </button>
        </div>

        {error && <div className="error-banner">{error}</div>}
      </section>

      {result && (
        <div className="compare-results">
          <section className="hero-card compare-result-hero">
            <div>
              <div className="eyebrow">Alignment Shift</div>
              <h2>
                {result.oldMbtiType} to {result.newMbtiType}
              </h2>
              <p>
                {result.oldExpandedMbtiType} to {result.newExpandedMbtiType}
              </p>
            </div>

            <div className="stability-ring">
              <span>Stability</span>
              <strong>{result.stabilityScore}</strong>
              <small>/100</small>
            </div>
          </section>

          <section className="content-grid compare-insight-grid">
            <article className="card">
              <div className="eyebrow">Narrative Summary</div>
              <p className="lead-text">{result.comparisonSummary}</p>
            </article>
            <article className="card">
              <div className="eyebrow">Psychological Reading</div>
              <p>{result.psychologicalInterpretation}</p>
            </article>
          </section>

          <section className="content-grid compare-insight-grid">
            <ListCard title="Stable Traits" items={result.stableTraits} emptyText="No especially stable traits were detected." />
            <ListCard title="Changed Traits" items={result.changedTraits} emptyText="No notable change traits were detected." />
          </section>

          <section className="content-grid compare-insight-grid">
            <DeltaCard title="Strongest Increase" item={result.strongestIncrease} positive />
            <DeltaCard title="Strongest Decrease" item={result.strongestDecrease} />
          </section>

          <section className="card trait-card">
            <div className="section-header">
              <div>
                <div className="eyebrow">Trait Comparison</div>
                <h3>Old versus new trait movement</h3>
              </div>
            </div>

            <div className="comparison-table-wrap">
              <table className="comparison-table">
                <thead>
                  <tr>
                    <th>Trait</th>
                    <th>Old</th>
                    <th>New</th>
                    <th>Difference</th>
                    <th>Change</th>
                  </tr>
                </thead>
                <tbody>
                  {result.traitComparisons.map((item) => (
                    <tr key={item.traitName}>
                      <td>{item.traitName}</td>
                      <td>{item.oldScore}</td>
                      <td>{item.newScore}</td>
                      <td className={item.difference >= 0 ? "delta-positive" : "delta-negative"}>
                        {item.difference > 0 ? `+${item.difference}` : item.difference}
                      </td>
                      <td>{item.changeType}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      )}
    </div>
  );
}

function AttemptSelect({ label, value, attempts, onChange }) {
  return (
    <div>
      <label>{label}</label>
      <select value={value} onChange={(e) => onChange(e.target.value)}>
        <option value="">Select an attempt</option>
        {attempts.map((attempt) => (
          <option key={attempt.attemptId} value={attempt.attemptId}>
            #{attempt.attemptId} - {attempt.mbtiType} - {formatDate(attempt.attemptDate)}
          </option>
        ))}
      </select>
    </div>
  );
}

function AttemptPreview({ title, attempt }) {
  return (
    <div className="compare-preview-card">
      <div className="eyebrow">{title}</div>
      {attempt ? (
        <>
          <h3>
            {attempt.mbtiType} <span>{attempt.expandedMbtiType}</span>
          </h3>
          <p>{attempt.archetype}</p>
          <div className="preview-metadata">
            <span>{formatDate(attempt.attemptDate)}</span>
            <span>Confidence {attempt.confidenceScore}/100</span>
          </div>
        </>
      ) : (
        <p>Select an attempt to preview it here.</p>
      )}
    </div>
  );
}

function ListCard({ title, items, emptyText }) {
  return (
    <article className="card">
      <div className="eyebrow">{title}</div>
      {items?.length ? (
        <div className="chip-row">
          {items.map((item) => (
            <span className="insight-chip" key={item}>
              {item}
            </span>
          ))}
        </div>
      ) : (
        <p>{emptyText}</p>
      )}
    </article>
  );
}

function DeltaCard({ title, item, positive = false }) {
  return (
    <article className="card delta-card">
      <div className="eyebrow">{title}</div>
      {item ? (
        <>
          <h3>{item.traitName}</h3>
          <p className={positive ? "delta-positive" : "delta-negative"}>
            {positive ? "+" : ""}
            {item.difference}
          </p>
          <span>{item.changeType}</span>
        </>
      ) : (
        <p>No strong directional signal appeared in this direction.</p>
      )}
    </article>
  );
}

function formatDate(value) {
  if (!value) {
    return "Unknown date";
  }

  return new Date(value).toLocaleString();
}

export default ComparePage;
