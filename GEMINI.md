# Blueprint Preview

This project uses **RTK** for token optimization and **code-review-graph** for codebase analysis.

## Tools

### RTK - Rust Token Killer
Optimizes developer operations by proxying CLI commands to save tokens (60-90% savings).

- `rtk gain`: View savings analytics.
- `rtk gain --history`: Show command history with savings.
- `rtk discover`: Analyze recent history for optimization opportunities.
- `rtk proxy <cmd>`: Run a command without RTK filtering.

### code-review-graph
Persistent incremental knowledge graph for semantic code analysis and review.

- `code-review-graph status`: Show graph statistics (nodes, edges, files).
- `code-review-graph update`: Incremental update for changed files.
- `code-review-graph build`: Full rebuild of the knowledge graph.
- `code-review-graph visualize`: Generate an interactive HTML graph.
- `code-review-graph wiki`: Generate a markdown wiki from the codebase structure.
