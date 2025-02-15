package com.example.ciba.rp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RelyingPartyProperties.class)
public class RelyingPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RelyingPartyApplication.class, args);
    }
}
