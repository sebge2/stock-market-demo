package be.sgerard.demo.bank.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Entry point of the bank stock service, which values portfolios and exposes stock quotations.
 *
 */
@SpringBootApplication
public class BankStockServiceApplication {

  /**
   *
   * Starts the application.
   *
   * @param args command line arguments
   *
   */
  public static void main(String[] args) {
    SpringApplication.run(BankStockServiceApplication.class, args);
  }
}
