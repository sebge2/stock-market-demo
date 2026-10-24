package be.sgerard.demo.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Entry point of the company service of the stock exchange.
 *
 */
@SpringBootApplication
public class CompanyServiceApplication {

  /**
   *
   * Starts the application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(CompanyServiceApplication.class, args);
  }
}
