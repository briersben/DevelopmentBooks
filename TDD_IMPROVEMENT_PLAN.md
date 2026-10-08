# DevelopmentBooks — TDD Improvement Plan

Created 2026-10-05. Tracks follow-up improvements identified beyond the baseline implementation (see [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md)).

Status key: `[ ]` not started, `[~]` in progress, `[x]` completed

- [x] **1. Fix duplicate book-id crash** — `CalculatePriceService.getPriceSummary` uses
      `Collectors.toMap(BookDto::getId, BookDto::getQuantity)` with no merge function —
      duplicate ids in the POST body throw an unhandled `IllegalStateException` (500).
      Fix: add a merge function (`Integer::sum`). Test first in `CalculatePriceServiceTest`.
- [x] **2. Add global exception handling** — added `GlobalExceptionHandler` mapping
      `BookNotFoundException` to HTTP 404 with a structured JSON message body. Verified by
      `DevelopmentBooksControllerTest`.
- [x] **3. Add input validation on `BookDto`** — added Bean Validation constraints requiring
      `id` and `quantity` to be at least 1, cascaded validation for POST basket items, and
      structured HTTP 400 responses for validation failures. Verified with controller tests
      covering zero/negative quantities and a zero book id.
- [x] **4. Explicit empty/null basket tests** — verified the service returns a zero-price
      summary for an empty list, `POST []` returns zero price fields, and a JSON `null` body
      is rejected with HTTP 400.
- [ ] **5. Migrate price math from `double` to `BigDecimal`** — in `CalculatePriceService`,
      `BookGroup`, `PriceSummaryDto` — to avoid floating-point rounding drift in currency math.
      Test first: assert exact BigDecimal scale-2 values for tricky discount splits.
- [ ] **6. Add frontend error-state test + handling** — `Product.test.js`/`Dashboard.test.js`
      likely only cover the happy path. Add an RTL test simulating a failed fetch (404/400 from
      steps 1-3), assert an error message renders, then implement the UI error state.
- [x] **7. Add OpenAPI/Swagger docs** via `springdoc-openapi`; smoke test `/v3/api-docs` returns 200.
      Done 2026-10-07: added `springdoc-openapi-starter-webmvc-ui`, an `OpenApiConfig` info bean,
      `@Tag`/`@Operation` annotations on the controller, and `OpenApiDocumentationTest` covering
      `/v3/api-docs` and `/swagger-ui/index.html`. Verified manually in-browser.

## Context / key files
- Service: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/service/CalculatePriceService.java`
- Controller: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/controller/DevelopmentBooksController.java`
- Exception: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/exceptions/BookNotFoundException.java`
- DTOs: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/dto/{BookDto,BookGroup,PriceSummaryDto}.java`
- Frontend tests: `developmentbooks/src/main/frontend/src/test/{components/Product.test.js,screen/Dashboard.test.js}`

## Order of attack
Tackle 1-4 first (real unhandled-error/correctness bugs), then 5 (precision hardening), then 6-7 (rounding out the stack).
