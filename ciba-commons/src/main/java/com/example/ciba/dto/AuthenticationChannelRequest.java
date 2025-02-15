package com.example.ciba.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public record AuthenticationChannelRequest(
        @JsonProperty("scope") String scope,
        @JsonProperty("login_hint") String loginHint,
        @JsonProperty("is_consent_required") boolean isConsentRequire,
        @JsonProperty("binding_message") String bindingMessage,
        @JsonProperty("acr_values") String acrValues,
        @JsonProperty(value = "request_id", required = true) String requestId,
        Map<String, Object> additionalParameters
) {
    public AuthenticationChannelRequest(String clientId, String clientSecret, String scope, String loginHint, String bindingMessage, String requestId) {
        this(scope, loginHint, false, bindingMessage, null, requestId, new HashMap<>());
    }

    public AuthenticationChannelRequest {
        additionalParameters = new HashMap<>();
    }

    @JsonAnySetter
    public void setOtherClaims(String name, Object value) {
        additionalParameters.put(name, value);
    }
}
