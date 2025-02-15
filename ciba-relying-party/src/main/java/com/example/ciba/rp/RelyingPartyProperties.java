package com.example.ciba.rp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("demo.relying.party")
public record RelyingPartyProperties(
        String keycloakUri,
        String keycloakRealm,
        String cibaUri,
        String cibaCallbackUri,
        String authDeviceNotifyWebhook
) {
}
