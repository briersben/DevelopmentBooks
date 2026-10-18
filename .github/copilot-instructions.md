# Copilot Instructions — DevelopmentBooks

## Project purpose
Calculate the best price of any shopping basket of the 5 "Development Books" using TDD, applying these discount rules:
- 1 book: 0 EUR off (50 EUR each)
- 2 different books: 5% discount
- 3 different books: 10% discount
- 4 different books: 20% discount
- 5 different books: 25% discount
- Duplicate copies within a group don't count toward the discount set; the optimal solution groups books to minimize total price (e.g. [4,4] beats [5,3] for 8 books: 2 different titles duplicated).

## Stack
- Backend: Java 25, Spring Boot 4.1.1, Maven 3.9.12
- Frontend: ReactJS 17.0.2 (under `developmentbooks/src/main/frontend`)
- Base package: `com.bnpp.katas.developmentbooks`

## Conventions
- Write tests first (TDD) — add/update a test under `developmentbooks/src/test/java/com/bnpp/...` before changing production code.
- For improvement work using TDD, follow the Red-Green-Refactor cycle in explicit steps: add a focused failing test and run it, then pause and explain the failure before implementing; after the smallest implementation makes it pass, pause and explain the result before proceeding to refactoring or other changes. Wait for the user's go-ahead at each pause.
- Keep pricing/discount logic in the `service` package (e.g. `CalculatePriceService`, `DevelopmentBooksService`); keep HTTP concerns in the `controller` package.
- Favor immutable DTOs and Lombok annotations already used in the codebase over boilerplate getters/setters.

## TDD improvement focus
- Use [TDD_IMPROVEMENT_PLAN.md](../TDD_IMPROVEMENT_PLAN.md) for the improvement backlog and its priority/order.
- Focus relevant improvements on these key files:
  - Pricing service: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/service/CalculatePriceService.java`
  - API controller: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/controller/DevelopmentBooksController.java`
  - Domain exception: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/exceptions/BookNotFoundException.java`
  - DTO: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/dto/BookDto.java`
  - Group DTO: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/dto/BookGroup.java`
  - Price summary DTO: `developmentbooks/src/main/java/com/bnpp/katas/developmentbooks/dto/PriceSummaryDto.java`
- Inspect and update the corresponding tests under `developmentbooks/src/test/java/com/bnpp/` when changing these areas.

## Build & test
- Full build + tests: `mvn clean install` (run from `developmentbooks/`)
- Reports after build: JaCoCo (`target/site/jacoco/index.html`), PIT mutation testing (`target/pit-reports/<timestamp>/index.html`)
- Frontend tests/coverage: from `developmentbooks/src/main/frontend`, run `npm test -- --coverage --watchAll`

## Run
- Default port 8080: `java -jar target\developmentbooks-1.0.0-SNAPSHOT.jar`
- Custom port: add `--server.port=<PORT>` or set `server.port` in `application.properties`
