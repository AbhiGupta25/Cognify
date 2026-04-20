import React, { useState } from "react";
import "./App.css";
import AssessmentPage from "./pages/AssessmentPage";
import ResultPage from "./pages/ResultPage";
import ComparePage from "./pages/ComparePage";
import SignupPage from "./pages/SignupPage";
import DashboardPage from "./pages/DashboardPage";

function App() {
  const [view, setView] = useState(() => (getStoredUser() ? "dashboard" : "auth"));
  const [result, setResult] = useState(null);
  const [user, setUser] = useState(() => getStoredUser());
  const [compareSelection, setCompareSelection] = useState({ oldAttemptId: "", newAttemptId: "" });

  const userName = user?.name?.split(" ")[0] || "User";

  const handleAuthSuccess = (nextUser) => {
    persistUser(nextUser);
    setUser(nextUser);
    setView("dashboard");
  };

  const handleAssessmentComplete = (assessmentResult) => {
    setResult(assessmentResult);
    setView("result");
  };

  const handleOpenAttempt = (attemptResult) => {
    setResult(attemptResult);
    setView("result");
  };

  const handleCompareSelection = ({ oldAttemptId, newAttemptId }) => {
    setCompareSelection({ oldAttemptId, newAttemptId });
    setView("compare");
  };

  const handleLogout = () => {
    localStorage.removeItem("userId");
    localStorage.removeItem("cognifyUser");
    setUser(null);
    setResult(null);
    setCompareSelection({ oldAttemptId: "", newAttemptId: "" });
    setView("auth");
  };

  if (!user) {
    return <SignupPage onAuthSuccess={handleAuthSuccess} />;
  }

  return (
    <div className="app-bg">
      <nav className="top-nav">
        <div>
          <div className="brand">Cognify</div>
          <div className="brand-subtitle">Behavioral Intelligence Platform</div>
        </div>

        <div className="nav-actions">
          <button
            className={`nav-btn ${view === "dashboard" ? "active" : ""}`}
            onClick={() => setView("dashboard")}
          >
            Dashboard
          </button>
          <button
            className={`nav-btn ${view === "assessment" ? "active" : ""}`}
            onClick={() => setView("assessment")}
          >
            Assessment
          </button>
          <button
            className={`nav-btn ${view === "compare" ? "active" : ""}`}
            onClick={() => setView("compare")}
          >
            Compare
          </button>
          <button className="nav-btn ghost" onClick={handleLogout}>
            Log Out
          </button>
        </div>
      </nav>

      <div className="page-shell page-shell-wide">
        <div className="user-banner">
          <div>
            <div className="eyebrow">Signed In</div>
            <h1>Welcome back, {userName}.</h1>
            <p>Your assessment history, live report, and retest comparisons all stay connected to this account.</p>
          </div>
        </div>

        {view === "dashboard" && (
          <DashboardPage
            user={user}
            onOpenAttempt={handleOpenAttempt}
            onStartAssessment={() => setView("assessment")}
            onCompareSelection={handleCompareSelection}
          />
        )}

        {view === "assessment" && (
          <AssessmentPage onAssessmentComplete={handleAssessmentComplete} />
        )}

        {view === "result" && result && (
          <ResultPage
            result={result}
            onRestart={() => setView("assessment")}
            onBackToDashboard={() => setView("dashboard")}
          />
        )}

        {view === "compare" && (
          <ComparePage
            user={user}
            initialSelection={compareSelection}
            onBack={() => setView("dashboard")}
          />
        )}
      </div>
    </div>
  );
}

function getStoredUser() {
  const rawUser = localStorage.getItem("cognifyUser");
  const userId = localStorage.getItem("userId");

  if (!userId) {
    return null;
  }

  if (!rawUser) {
    return { id: Number(userId) };
  }

  try {
    const parsed = JSON.parse(rawUser);
    return { ...parsed, id: Number(parsed.id || userId) };
  } catch (error) {
    return { id: Number(userId) };
  }
}

function persistUser(user) {
  localStorage.setItem("userId", String(user.id));
  localStorage.setItem("cognifyUser", JSON.stringify(user));
}

export default App;
