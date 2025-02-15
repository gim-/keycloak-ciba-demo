package com.example.ciba.cd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConsumptionDeviceProperties.class)
public class ConsumptionDeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumptionDeviceApplication.class, args);
    }

}
