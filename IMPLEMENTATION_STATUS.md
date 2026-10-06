# Implementation Status

Based on the codebase (as of 2026-10-05), the application described in [README.md](README.md) is already fully implemented end-to-end.

## Backend (`developmentbooks/src/main/java/com/bnpp/katas/developmentbooks`)

**Domain/store**
- `DevelopmentBooksEnum.java` — the 5 fixed books (id, title, author, year, 50 EUR price, image URL) from the README.
- `DiscountProviderEnum.java` — the discount table: 2→5%, 3→10%, 4→20%, 5→25%.

**DTOs**
- `Book`, `BookDto` (request item with id + quantity), `BookGroup` (a priced set of distinct books), `PriceSummaryDto` (final response: groups, actual price, total discount, final price).

**Services**
- `CalculatePriceService.java` — the core pricing algorithm. Implements exactly the optimization described in the README's functional case:
  - Validates requested books exist (throws `BookNotFoundException` otherwise).
  - Tries every applicable discount-group size (from largest down to 2), greedily forming groups of distinct titles and recursively re-grouping leftovers.
  - Keeps whichever grouping yields the maximum total discount (`updateBestDiscount`), which is how it correctly prefers `[4,4]` over `[5,3]` for the 8-book basket example.
  - Falls back to a no-discount group if basket has only 1 distinct title.
- `DevelopmentBooksService.java` — exposes the book catalog and discount-rules map.

**Controller**
- `DevelopmentBooksController.java` — REST endpoints (paths externalized to `application.properties`):
  - `GET` books list
  - `POST` basket → price summary
  - `GET` discount details

**Tests** — unit tests exist for the controller, both services, the `Book` DTO, and the `DevelopmentBooksEnum`, plus an application context load test (per `target/surefire-reports`), consistent with the TDD approach called for in the README.

## Frontend (`src/main/frontend`)
- React app scaffolded with a `Dashboard` screen and a `Product` component directory, presumably rendering the book catalog and basket/price summary against the backend API.

## Summary
The README's core requirement — calculating the optimal discounted price for any basket, including picking `[4,4]` over `[5,3]` — is already implemented end-to-end (enum data → pricing algorithm → REST API → React UI), with accompanying unit tests, JaCoCo coverage, and PIT mutation test reports already generated under `target/`.
