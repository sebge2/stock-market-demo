package be.sgerard.demo.company.repository;

import be.sgerard.demo.company.model.PriceInterval;
import be.sgerard.demo.company.model.StockPrice;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link StockPrice} entities.
 *
 */
public interface StockPriceRepository extends JpaRepository<StockPrice, UUID> {

  /**
   *
   * Finds the prices of a security for an interval within a period, oldest first.
   *
   * @param securityId the security identifier
   * @param interval the time interval
   * @param from start of the period (inclusive)
   * @param to end of the period (inclusive)
   * @return the prices
   */
  List<StockPrice> findBySecurityIdAndIntervalAndTimestampBetweenOrderByTimestampAsc(
      UUID securityId, PriceInterval interval, LocalDateTime from, LocalDateTime to);
}
