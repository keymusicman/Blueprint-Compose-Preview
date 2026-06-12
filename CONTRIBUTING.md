# Contributing to Blueprint Preview

First off, thank you for considering contributing to Blueprint Preview! 

To ensure the quality and stability of the library, we follow a specific, example-driven workflow for all bug fixes and new features.

## The Pull Request Workflow

When submitting a Pull Request, please structure your commits to clearly demonstrate the problem and then the solution. We require the following sequence:

### 1. The Failing State (Commit 1)
Your first commit should **only** contain a new example file (or an addition to an existing example) in the `example` module. 
- **For bugs:** This example must clearly reproduce the error, misalignment, or failure state.
- **For features:** This example should represent the UI component or layout that currently lacks the desired blueprint functionality.

*Why? This makes it incredibly easy for reviewers to check out your first commit, see the exact failure state for themselves, and understand exactly what you are trying to solve.*

### 2. The Fix / Feature (Commit 2+)
Your subsequent commit(s) should contain the actual changes to the core `blueprint-compose-preview` library that resolves the issue or adds the feature. 
- When the reviewer advances to this commit, the example you added in Commit 1 should now render the blueprint perfectly.

## Regression Rules

**All existing examples must continue to work.**

Blueprint Preview relies heavily on the exact visual output of the Compose tree. Before submitting your PR:
1. Open the `example` module.
2. Run the existing Compose Previews.
3. Verify that your changes haven't broken or shifted the blueprint rendering for any of the previously established components (like `ExampleButtonComponent`, `ExampleComplexDashboardComponent`, etc.).

Thank you for helping make Blueprint Preview better!
