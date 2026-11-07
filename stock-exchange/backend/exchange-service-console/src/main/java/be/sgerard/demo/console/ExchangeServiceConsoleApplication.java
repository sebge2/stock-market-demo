package be.sgerard.demo.console;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * Starts the Spring Boot Admin console for exchange services.
 *
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
@EnableScheduling
public class ExchangeServiceConsoleApplication {

  /**
   *
   * Starts the exchange service console.
   *
   * @param args startup arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(ExchangeServiceConsoleApplication.class, args);
  }
}
