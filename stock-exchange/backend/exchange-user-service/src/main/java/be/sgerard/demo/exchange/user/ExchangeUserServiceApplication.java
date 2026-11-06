package be.sgerard.demo.exchange.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Starts the exchange user profile service.
 *
 */
@SpringBootApplication
public class ExchangeUserServiceApplication {

  /**
   *
   * Starts the Spring Boot application.
   *
   * @param args startup arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(ExchangeUserServiceApplication.class, args);
  }
}
