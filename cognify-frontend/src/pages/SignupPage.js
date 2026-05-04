import React, { useState } from "react";
import { login, signup, startDemo } from "../api/userApi";

function SignupPage({ onAuthSuccess, onDemoStart }) {
  const [mode, setMode] = useState("signup");
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [demoLoading, setDemoLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (!form.email.trim() || !form.password.trim() || (mode === "signup" && !form.name.trim())) {
      setError("Please complete all required fields.");
      return;
    }

    try {
      setLoading(true);
      const payload = {
        email: form.email.trim(),
        password: form.password,
        ...(mode === "signup" ? { name: form.name.trim() } : {}),
      };

      const user = mode === "signup" ? await signup(payload) : await login(payload);
      onAuthSuccess(user);
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.message ||
        "Authentication failed.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  const handleDemoStart = async () => {
    setError("");

    try {
      setDemoLoading(true);
      const demoData = await startDemo();
      onDemoStart(demoData);
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.message ||
        "Demo mode could not be started.";
      setError(message);
    } finally {
      setDemoLoading(false);
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-hero">
        <div className="auth-copy">
          <div className="eyebrow">Cognify Access</div>
          <h1>Behavioral intelligence, tracked across time.</h1>
          <p>
            Create an account to generate premium personality reports, revisit older attempts, and compare how your
            profile shifts across life stages and contexts.
          </p>

          <div className="auth-feature-grid">
            <Feature title="Insightful reports" text="Structured narrative sections instead of flat MBTI labels." />
            <Feature title="History dashboard" text="Review past attempts, confidence, archetype, and change over time." />
            <Feature title="Retest comparison" text="See what stayed stable, what changed, and why it matters psychologically." />
          </div>
        </div>

        <div className="auth-card">
          <div className="auth-tabs">
            <button
              className={`auth-tab ${mode === "signup" ? "active" : ""}`}
              onClick={() => {
                setMode("signup");
                setError("");
              }}
              type="button"
            >
              Create account
            </button>
            <button
              className={`auth-tab ${mode === "login" ? "active" : ""}`}
              onClick={() => {
                setMode("login");
                setError("");
              }}
              type="button"
            >
              Log in
            </button>
          </div>

          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="auth-header">
              <div className="eyebrow auth-eyebrow">{mode === "signup" ? "Start Here" : "Welcome Back"}</div>
              <h2>{mode === "signup" ? "Create your Cognify account" : "Continue your Cognify journey"}</h2>
              <p className="auth-subtext">
                {mode === "signup"
                  ? "Your account keeps every attempt tied to one profile so results and comparisons stay coherent."
                  : "Sign in with your email and password to resume assessments and open past reports."}
              </p>
            </div>

            {mode === "signup" && (
              <input
                className="auth-input"
                type="text"
                placeholder="Full name"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
              />
            )}

            <input
              className="auth-input"
              type="email"
              placeholder="Email address"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
            />

            <input
              className="auth-input"
              type="password"
              placeholder={mode === "signup" ? "Create password" : "Password"}
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
            />

            <button className="primary-btn auth-btn" type="submit" disabled={loading}>
              {loading ? "Please wait..." : mode === "signup" ? "Create Account" : "Log In"}
            </button>

            <div className="demo-divider">
              <span>or</span>
            </div>

            <button className="secondary-btn demo-btn" type="button" onClick={handleDemoStart} disabled={demoLoading}>
              {demoLoading ? "Loading Demo..." : "Explore Prebuilt Demo Profile"}
            </button>

            <p className="demo-microcopy">
              Loads a sample Cognify assessment so you can test the behavioral simulation engine instantly.
            </p>

            {error && <div className="error-banner auth-error">{error}</div>}
          </form>
        </div>
      </div>
    </div>
  );
}

function Feature({ title, text }) {
  return (
    <div className="auth-feature">
      <h3>{title}</h3>
      <p>{text}</p>
    </div>
  );
}

export default SignupPage;
