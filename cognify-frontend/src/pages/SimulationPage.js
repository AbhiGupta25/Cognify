import React, { useMemo, useState } from "react";
import { analyzeSimulation } from "../api/assessmentApi";

const categories = [
  "Group Project Conflict",
  "Viva / Interview Pressure",
  "Career Confusion",
  "Procrastination",
  "Friendship Conflict",
  "Leadership Situation",
  "Custom Scenario",
];

function SimulationPage({ user, result, onBackToResult, onBackToDashboard }) {
  const [scenarioCategory, setScenarioCategory] = useState("Group Project Conflict");
  const [scenarioText, setScenarioText] = useState("");
  const [simulation, setSimulation] = useState(null);
  const [submittedScenario, setSubmittedScenario] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const promptPlaceholder = useMemo(() => {
    switch (scenarioCategory) {
      case "Group Project Conflict":
        return "Example: Two teammates are not contributing, deadlines are close, and I feel like I have to carry the project.";
      case "Viva / Interview Pressure":
        return "Example: I freeze when a panel asks unexpected follow-up questions and I start sounding less confident than I actually am.";
      case "Career Confusion":
        return "Example: I am torn between a stable placement path and a more creative path that feels meaningful but uncertain.";
      case "Procrastination":
        return "Example: I keep delaying a major submission even though I know exactly what needs to be done.";
      case "Friendship Conflict":
        return "Example: A close friend has become distant and I cannot tell whether I should confront it or give them space.";
      case "Leadership Situation":
        return "Example: I have been asked to lead a team, but I am unsure how to motivate people without becoming controlling.";
      default:
        return "Describe the exact real-life situation you want Cognify to simulate.";
    }
  }, [scenarioCategory]);

  const handleGenerate = async () => {
    setError("");

    if (!result?.attemptId) {
      setError("A saved assessment result is required before running a simulation.");
      return;
    }

    if (scenarioCategory === "Custom Scenario" && !scenarioText.trim()) {
      setError("Describe your custom scenario so Cognify can simulate it.");
      return;
    }

    try {
      setLoading(true);
      const data = await analyzeSimulation({
        userId: user.id,
        attemptId: result.attemptId,
        scenarioCategory,
        scenarioText: scenarioText.trim(),
      });
      setSimulation(data);
      setSubmittedScenario({
        category: scenarioCategory,
        text: scenarioText.trim(),
      });
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        "Simulation generation failed. Check the backend and try again.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="simulation-layout">
      <section className="hero-card simulation-hero">
        <div>
          <div className="eyebrow">Cognitive Simulation Mode</div>
          <div className="simulation-powered-badge">Powered by your latest Cognify assessment</div>
          <h2>Run your personality through a real-life pressure test.</h2>
          <p>
            Cognify uses your saved assessment attempt, MBTI alignment, archetype, trait scores, and behavioral report
            to simulate how you are most likely to react in the real world.
          </p>
        </div>

        <div className="simulation-context-card">
          <div className="eyebrow">Loaded Attempt</div>
          <h3>{result?.mbtiType || "Unavailable"}</h3>
          <p>{result?.expandedMbtiType || "No expanded type available."}</p>
          <div className="summary-metric-grid">
            <Metric label="Attempt" value={`#${result?.attemptId ?? "--"}`} />
            <Metric label="Archetype" value={result?.archetype || "Unavailable"} />
            <Metric label="Confidence" value={`${result?.confidenceScore ?? "--"}/100`} />
          </div>
        </div>
      </section>

      <section className="card simulation-builder-card">
        <div className="section-header">
          <div>
            <div className="eyebrow">Scenario Builder</div>
            <h3>Choose a category and add your real-life context.</h3>
          </div>
        </div>

        <div className="simulation-category-grid">
          {categories.map((category) => (
            <button
              key={category}
              className={`simulation-category-card ${scenarioCategory === category ? "active" : ""}`}
              onClick={() => setScenarioCategory(category)}
              type="button"
            >
              <span>{category}</span>
            </button>
          ))}
        </div>

        <div className="simulation-input-block">
          <label htmlFor="scenarioText">Scenario details</label>
          <textarea
            id="scenarioText"
            className="simulation-textarea"
            value={scenarioText}
            onChange={(event) => setScenarioText(event.target.value)}
            placeholder={promptPlaceholder}
            rows={6}
          />
          <p className="simulation-helper-text">
            Add real context for sharper simulations. If you leave this general, Cognify will still use category-based logic.
          </p>
        </div>

        <div className="actions-row left">
          <button className="primary-btn" onClick={handleGenerate} disabled={loading}>
            {loading ? "Running Simulation..." : "Generate Simulation"}
          </button>
          <button className="secondary-btn" onClick={onBackToResult}>
            Back to Result
          </button>
          <button className="secondary-btn" onClick={onBackToDashboard}>
            Dashboard
          </button>
        </div>

        {error && <div className="error-banner">{error}</div>}
      </section>

      {simulation && (
        <div className="simulation-results">
          <section className="hero-card simulation-result-hero">
            <div>
              <div className="eyebrow">Simulation Output</div>
              <h3 className="simulation-output-title">{submittedScenario?.category || simulation.scenarioTitle}</h3>
              <p className="simulation-output-supporting">
                {submittedScenario?.text || defaultScenarioSupport(simulation.scenarioTitle)}
              </p>
              <p className="simulation-output-note">
                Personalized simulation generated from your Cognify profile and current scenario.
              </p>
            </div>
          </section>

          <section className="content-grid simulation-insight-grid">
            <InsightCard title="Personality Snapshot" text={simulation.personalitySnapshot} accent />
            <InsightCard title="Likely Default Reaction" text={simulation.likelyDefaultReaction} />
          </section>

          <section className="content-grid simulation-insight-grid">
            <InsightCard title="Hidden Blind Spot" text={simulation.hiddenBlindSpot} />
            <InsightCard title="What Your Brain Is Optimizing For" text={simulation.whatYourBrainIsOptimizingFor} />
          </section>

          <section className="card premium-insight-card">
            <div className="eyebrow">Better Response Strategy</div>
            <p className="lead-text">{simulation.betterResponseStrategy}</p>
          </section>

          <section className="content-grid simulation-plan-grid">
            <PlanCard title="3-Step Action Plan" items={simulation.threeStepActionPlan} />
            <PlanCard title="7-Day Growth Plan" items={simulation.sevenDayGrowthPlan} />
          </section>

          <section className="card reflection-card">
            <div className="eyebrow">Reflection Prompt</div>
            <p className="lead-text">{simulation.reflectionPrompt}</p>
          </section>
        </div>
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

function InsightCard({ title, text, accent = false }) {
  return (
    <article className={`card simulation-insight-card ${accent ? "accent" : ""}`}>
      <div className="eyebrow">{title}</div>
      <p className="simulation-insight-text">{text}</p>
    </article>
  );
}

function PlanCard({ title, items = [] }) {
  return (
    <article className="card simulation-plan-card">
      <div className="eyebrow">{title}</div>
      <div className="simulation-plan-list">
        {items.map((item, index) => (
          <div className="simulation-plan-item" key={`${title}-${index}`}>
            <div className="simulation-step-index">{index + 1}</div>
            <p className="simulation-plan-copy">{item}</p>
          </div>
        ))}
      </div>
    </article>
  );
}

function defaultScenarioSupport(category) {
  switch (category) {
    case "Group Project Conflict":
      return "Uneven contribution, deadline pressure, and the need for clean accountability.";
    case "Viva / Interview Pressure":
      return "Pressure, follow-up questions, and recovering without sounding memorized.";
    case "Career Confusion":
      return "Too many options, fear of choosing wrong, and decision paralysis.";
    case "Procrastination":
      return "Avoidance loops, guilt at night, and reducing starting friction.";
    case "Friendship Conflict":
      return "Delayed replies, emotional ambiguity, and direct but low-pressure clarity.";
    case "Leadership Situation":
      return "Taking charge, delegating clearly, and holding emotional control.";
    default:
      return "A personalized real-life situation interpreted through your Cognify profile.";
  }
}

export default SimulationPage;
