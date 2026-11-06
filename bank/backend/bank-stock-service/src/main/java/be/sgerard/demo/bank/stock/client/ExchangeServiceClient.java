package be.sgerard.demo.bank.stock.client;

import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.QuotationNotFoundException;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;

/**
 *
 * Client of the stock exchange company service, which publishes the stock quotations.
 *
 */
public interface ExchangeServiceClient {

  /**
   *
   * Retrieves the latest quotation of a stock on behalf of the current caller (the bearer token of the
   * current request is forwarded). Transient failures are retried.
   *
   * @param stockSymbol symbol of the stock
   * @return the latest quotation
   * @throws QuotationNotFoundException if the exchange has no quotation for the stock
   * @throws DependencyUnavailableException if the exchange fails or is unreachable
   *
   */
  QuotationDto findLatestQuotation(String stockSymbol);
}
