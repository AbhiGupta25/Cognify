import React, { useEffect, useState } from "react";
import { fetchAssessmentResult } from "../api/assessmentApi";
import { fetchUserAttempts } from "../api/userApi";

function DashboardPage({ user, onOpenAttempt, onStartAssessment, onCompareSelection }) {
  const [attempts, setAttempts] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [openingId, setOpeningId] = useState(null);

  useEffect(() => {
    const loadAttempts = async () => {
      try {
        setLoading(true);
        const data = await fetchUserAttempts(user.id);
        setAttempts(data);
      } catch (err) {
        setError("We could not load your dashboard history.");
      } finally {
        setLoading(false);
      }
    };

    loadAttempts();
  }, [user.id]);

  const latestAttempt = attempts[0] || null;

  const toggleSelection = (attemptId) => {
    setSelectedIds((current) => {
      if (current.includes(attemptId)) {
        return current.filter((id) => id !== attemptId);
      }

      if (current.length === 2) {
        return [current[1], attemptId];
      }

      return [...current, attemptId];
    });
  };

  const handleOpenAttempt = async (attemptId) => {
    try {
      setOpeningId(attemptId);
      const result = await fetchAssessmentResult(attemptId, user.id);
      onOpenAttempt(result);
    } catch (err) {
      setError(err?.response?.data?.message || "We could not open that attempt.");
    } finally {
      setOpeningId(null);
    }
  };

  const handleCompare = () => {
    if (selectedIds.length !== 2) {
      setError("Select exactly two attempts to compare.");
      return;
    }

    const sorted = [...selectedIds].sort((a, b) => a - b);
    onCompareSelection({ oldAttemptId: String(sorted[0]), newAttemptId: String(sorted[1]) });
  };

  return (
    <div className="dashboard-layout">
      <section className="content-grid dashboard-hero-grid">
        <div className="hero-card dashboard-hero-card">
          <div className="eyebrow">History Dashboard</div>
          <h2>Your behavioral record across attempts.</h2>
          <p>
            Open any prior report, start a fresh assessment, or select two attempts to compare how your profile has
            evolved.
          </p>
          <div className="actions-row left">
            <button className="primary-btn" onClick={onStartAssessment}>
              Start New Assessment
            </button>
            <button className="secondary-btn" onClick={handleCompare}>
              Compare Selected
            </button>
          </div>
        </div>

        <div className="card dashboard-summary-card">
          <div className="eyebrow">Latest Signal</div>
          {latestAttempt ? (
            <>
              <h3>{latestAttempt.mbtiType}</h3>
              <p>{latestAttempt.expandedMbtiType}</p>
              <div className="summary-metric-grid">
                <Metric label="Archetype" value={latestAttempt.archetype} />
                <Metric label="Confidence" value={`${latestAttempt.confidenceScore}/100`} />
                <Metric label="Attempt" value={`#${latestAttempt.attemptId}`} />
              </div>
            </>
          ) : (
            <p>No attempts yet. Take your first assessment to build the dashboard.</p>
          )}
        </div>
      </section>

      {error && <div className="error-banner">{error}</div>}

      {loading ? (
        <div className="card">Loading your assessment history...</div>
      ) : attempts.length === 0 ? (
        <div className="card empty-state-card">
          <div className="eyebrow">No History Yet</div>
          <h3>Your dashboard is ready.</h3>
          <p>Once you complete your first assessment, Cognify will keep each report here for review and comparison.</p>
        </div>
      ) : (
        <section className="attempt-grid">
          {attempts.map((attempt) => {
            const selected = selectedIds.includes(attempt.attemptId);

            return (
              <article className={`card attempt-card ${selected ? "selected" : ""}`} key={attempt.attemptId}>
                <div className="attempt-card-header">
                  <div>
                    <div className="eyebrow">Attempt #{attempt.attemptId}</div>
                    <h3>{attempt.mbtiType}</h3>
                    <p>{attempt.expandedMbtiType}</p>
                  </div>
                  <button className={`selection-toggle ${selected ? "active" : ""}`} onClick={() => toggleSelection(attempt.attemptId)}>
                    {selected ? "Selected" : "Select"}
                  </button>
                </div>

                <div className="attempt-meta-grid">
                  <Metric label="Date" value={formatDate(attempt.attemptDate)} />
                  <Metric label="Archetype" value={attempt.archetype || "Unavailable"} />
                  <Metric label="Confidence" value={`${attempt.confidenceScore ?? "--"}/100`} />
                  <Metric label="Contradictions" value={String(attempt.contradictionCount ?? "--")} />
                </div>

                <p className="attempt-summary">{attempt.summary}</p>

                <div className="actions-row left">
                  <button
                    className="primary-btn"
                    onClick={() => handleOpenAttempt(attempt.attemptId)}
                    disabled={openingId === attempt.attemptId}
                  >
                    {openingId === attempt.attemptId ? "Opening..." : "Open Full Report"}
                  </button>
                </div>
              </article>
            );
          })}
        </section>
      )}
    </div>
  );
}

function Metric({ label, value }) {
  return (
    <div className="summary-metric">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function formatDate(value) {
  if (!value) {
    return "Unknown";
  }

  return new Date(value).toLocaleString();
}

export default DashboardPage;
