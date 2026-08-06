# Commit Cadence Guide

This project follows a small, frequent, and meaningful commit style.

## Purpose

- Keep history easy to review.
- Show steady progress in implementation and documentation.
- Reduce risk during integration by limiting each commit scope.

## Recommended Commit Types (No Logic Change)

- `docs:` clarify README, user stories, test cases, or meeting notes.
- `chore:` project hygiene updates (comments, folder organization notes, metadata).
- `style:` formatting-only changes without behavioral impact.
- `test:` add or improve test descriptions or test documentation.

## Good Small Commit Examples

- `docs: add API usage note for alert filters`
- `docs: refine dashboard acceptance criteria wording`
- `chore: add commit cadence guide for contribution workflow`
- `style: normalize markdown heading spacing in docs`

## Guardrails

- Do not combine unrelated topics in one commit.
- Keep each commit focused on a single intention.
- Avoid changing business logic unless explicitly planned.
- Prefer files under `docs/`, `README.md`, and non-runtime project metadata for cadence commits.

## Suggested Rhythm

- 2 to 5 focused commits per working day.
- Each commit should be explainable in one sentence.
- Run tests before pushing if any code file is touched.
