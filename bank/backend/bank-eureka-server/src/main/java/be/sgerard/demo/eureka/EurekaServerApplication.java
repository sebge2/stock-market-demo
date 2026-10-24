package be.sgerard.demo.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 *
 * Starts the service registry used by the bank's microservices.
 *
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

  /**
   *
   * Starts the Eureka server.
   *
   * @param args startup arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(EurekaServerApplication.class, args);
  }
}
