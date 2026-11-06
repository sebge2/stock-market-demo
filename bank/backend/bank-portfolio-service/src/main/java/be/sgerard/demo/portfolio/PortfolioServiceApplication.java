package be.sgerard.demo.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Entry point of the portfolio service.
 *
 */
@SpringBootApplication
public class PortfolioServiceApplication {

    /**
     *
     * Starts the application.
     *
     * @param args command line arguments
     *
     */
    public static void main(String[] args) {
        SpringApplication.run(PortfolioServiceApplication.class, args);
    }
}
