package org.kolyanlock.taskmanagerapi;

import org.kolyanlock.taskmanagerapi.property.AdminProperties;
import org.kolyanlock.taskmanagerapi.property.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties({AdminProperties.class, JwtProperties.class})
public class TaskManagerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApiApplication.class, args);
    }

}
