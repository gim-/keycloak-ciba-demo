package com.example.ciba.ad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AuthenticationDeviceProperties.class)
public class AuthenticationDeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationDeviceApplication.class, args);
    }
}
