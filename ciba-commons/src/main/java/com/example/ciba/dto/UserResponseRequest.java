package com.example.ciba.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponseRequest(
        @JsonProperty(value = "request_id", required = true) String requestId,
        @JsonProperty(value = "status", required = true) AuthenticationChannelResponse.Status status
) {
}
