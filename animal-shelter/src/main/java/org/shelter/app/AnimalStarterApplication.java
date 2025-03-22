package org.shelter.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AnimalStarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnimalStarterApplication.class, args);

    }
}
