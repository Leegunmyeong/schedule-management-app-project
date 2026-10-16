package com.schedulemanagementappproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ScheduleManagementAppProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleManagementAppProjectApplication.class, args);
    }

}
