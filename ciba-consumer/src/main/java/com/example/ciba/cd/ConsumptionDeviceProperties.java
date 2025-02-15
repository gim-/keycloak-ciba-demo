package com.example.ciba.cd;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("demo.consumer")
public record ConsumptionDeviceProperties(
        String keycloakUri,
        String keycloakRealm,
        String tokenUri,
        String clientId,
        String clientSecret,
        String cibaUri
) {
}
