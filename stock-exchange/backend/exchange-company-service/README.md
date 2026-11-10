

# Exchange Company Service

Swagger UI is available at <http://localhost:8087/api/company/swagger-ui.html>.

## Share counts

The security fields distinguish the following concepts:

- `issuedShares`: all shares created by the company, including shares held by the company itself.
- `outstandingShares`: issued shares held by third parties; it excludes treasury shares.
- `freeFloatShares`: outstanding shares that are freely available for trading. It excludes strategic, controlling or otherwise restricted holdings.
- `volume`: shares actually traded during a given period. It is recorded on `StockPrice` and changes throughout the day; it is not the free float.

Example: a company has created 1,000,000 shares, repurchased 100,000 of them, and its founder holds 500,000 shares. It has 1,000,000 issued shares, 900,000 outstanding shares and 400,000 free-float shares. If 25,000 shares are traded today, the daily volume is 25,000.
