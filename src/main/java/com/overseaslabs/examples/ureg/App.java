package com.overseaslabs.examples.ureg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.overseaslabs.examples.ureg", "com.overseaslabs.examples.utils"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
