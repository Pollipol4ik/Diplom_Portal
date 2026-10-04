package org.diplom_backend;

import org.diplom_backend.config.AppConfig;
import org.diplom_backend.config.StorageProperties;
import org.diplom_backend.scheduler.SchedulerProperties;
import org.diplom_backend.services.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({StorageProperties.class, SchedulerProperties.class, AppConfig.class})
public class DiplomBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiplomBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner init(@Qualifier("fileService") StorageService fileService) {
        return (args) -> {
            fileService.init();
        };
    }
}
