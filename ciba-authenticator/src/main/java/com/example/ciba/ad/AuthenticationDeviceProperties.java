package com.example.ciba.ad;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ciba.authenticator")
public record AuthenticationDeviceProperties(
        String relyingPartyResultUri
) {
}
