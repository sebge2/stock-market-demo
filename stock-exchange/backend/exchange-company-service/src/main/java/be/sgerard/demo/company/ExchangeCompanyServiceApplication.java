package be.sgerard.demo.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Entry point of the company service of the stock exchange.
 *
 * This application provides REST endpoints for managing companies and their
 * securities and their market prices. It is secured with OAuth2 resource server configuration
 * and documented with OpenAPI/Swagger.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ExchangeCompanyServiceApplication {

	/**
	 * Starts the application.
	 *
	 * @param args command line arguments
	 */
	public static void main(final String[] args) {
		SpringApplication.run(ExchangeCompanyServiceApplication.class, args);
	}
}
