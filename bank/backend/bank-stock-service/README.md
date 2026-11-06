# Bank Stock Service

Microservice computing the current value of a portfolio and exposing stock quotations.

## Run

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
# Port: 8088 (8086 is used by exchange-staff-service)
# Swagger UI: http://localhost:8088/api/valuation/swagger-ui.html
```

Requires `bank-user-service` (JWT issuer) and the exchange `exchange-company-service`.

## Endpoints

| Endpoint | Permission |
|----------|-----------|
| `POST /portfolios/value` (portfolio with its positions in the body) | `PORTFOLIO_VALUATION_READ` |
| `GET /quotations/{stockSymbol}` | `QUOTATION_READ` |

Errors: 404 (quotation not found), 422 (quotation missing or older than
`bank.stock-service.quotation-max-age`, default 24h), 500 (dependency unavailable).

## Design

- The caller's JWT is forwarded to the exchange service, so the caller must also hold the permissions it requires.
- The portfolio is provided by the caller; this service does not call the portfolio service.
- Amounts are `BigDecimal`, scale 4, half-even rounding, currency EUR. No caching, no database.
- Remote calls: 3 attempts, exponential backoff from 100ms, retrying 5xx and I/O errors only.
- The exchange is called on `GET {exchange-base-url}/listings/{symbol}/latest-price`. This endpoint does not
  exist yet in `exchange-company-service` (it has no REST controller) and must be added separately (tracked in #24).
  Until then both endpoints fail against a real exchange; the service is only verified with mocked clients.
