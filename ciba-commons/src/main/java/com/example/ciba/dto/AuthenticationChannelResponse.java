package com.example.ciba.dto;

public record AuthenticationChannelResponse(Status status) {

    public enum Status {
        SUCCEED,
        UNAUTHORIZED,
        CANCELLED;
    }
}
