import { render, screen } from "@testing-library/react";
import App from "./App";

test("renders Cognify auth screen when signed out", () => {
  localStorage.clear();
  render(<App />);
  expect(screen.getByText(/behavioral intelligence, tracked across time/i)).toBeInTheDocument();
});
