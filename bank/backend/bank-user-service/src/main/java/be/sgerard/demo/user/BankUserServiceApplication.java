package be.sgerard.demo.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Starts the bank user profile service.
 *
 */
@SpringBootApplication
public class BankUserServiceApplication {

  /**
   *
   * Starts the Spring Boot application.
   *
   * @param args startup arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(BankUserServiceApplication.class, args);
  }
}
