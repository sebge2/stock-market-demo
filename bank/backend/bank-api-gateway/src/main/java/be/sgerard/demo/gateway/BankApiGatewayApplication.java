package be.sgerard.demo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Starts the bank API gateway and its discovery-based routes.
 *
 */
@SpringBootApplication
public class BankApiGatewayApplication {

  /**
   *
   * Starts the bank API gateway.
   *
   * @param args startup arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(BankApiGatewayApplication.class, args);
  }
}
