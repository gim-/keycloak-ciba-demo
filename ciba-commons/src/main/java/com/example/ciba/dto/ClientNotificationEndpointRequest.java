package com.example.ciba.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientNotificationEndpointRequest(
        @JsonProperty(value = "auth_req_id", required = true) String authRequestId,
        @JsonProperty(value = "expires_in", required = true) int expiresIn,
        @JsonProperty("interval") int interval
) {
}
