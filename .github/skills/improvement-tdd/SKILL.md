---
name: improvement-tdd
description: "Find and implement one bounded Java or project improvement using the DevelopmentBooks TDD workflow. Use when asked to improve, modernize, fix, or strengthen this codebase with test-first Red-Green-Refactor pauses."
argument-hint: "Optionally describe an improvement area or concern."
user-invocable: true
---

# Find and implement an improvement with TDD

Help identify and implement one bounded improvement in this project, following the workflow in [copilot-instructions.md](../../copilot-instructions.md) and the backlog in [TDD_IMPROVEMENT_PLAN.md](../../../TDD_IMPROVEMENT_PLAN.md).

1. Start by asking the user to choose an improvement category in one multiple-choice question. Offer:
   - Java 25 feature or modernization of existing code
   - Improve existing Java code (for example, a controller or service)
   - Tests, correctness, or reliability
   - API behavior, validation, or error handling
   - Performance or maintainability
   - Build, dependencies, or developer tooling
   - Documentation
   - Other / describe an area
2. After the user chooses, inspect the relevant code, tests, and project guidance. Check the worktree first and preserve all unrelated user changes.
3. Identify one concrete, worthwhile, bounded improvement in that category. Use the TDD improvement plan and key files listed in the project instructions to guide the search, without repeating completed work. Explain the opportunity, expected benefit, likely files, and any important trade-off. Ask the user to confirm the proposal before editing if its scope or behavior is not already clear.
4. Follow the project's TDD checkpoints:
   - Add a focused test before production changes and run it.
   - Stop at the red phase. Explain what failed and why, and wait for the user's go-ahead.
   - After approval, make the smallest production change that satisfies the test and run the focused tests.
   - Stop at the green phase. Explain the implementation and test result, and wait for the user's go-ahead before refactoring or making further changes.
   - After approval, refactor only if useful, run the relevant tests, and update directly related documentation when needed.
5. Do not commit or push unless the user explicitly asks. Finish with a concise summary of the change, tests, and any remaining work.

If the chosen category is too broad to identify a safe, bounded improvement, ask one clarifying multiple-choice question before proposing changes.
