package com.example.ciba.cd.session;

import com.example.ciba.dto.ClientNotificationEndpointRequest;

/**
 * @see SessionAttributesConfiguration
 */
public class AuthRequestSessionAttributes {

    private ClientNotificationEndpointRequest clientNotificationRequest;

    public ClientNotificationEndpointRequest getClientNotificationRequest() {
        return clientNotificationRequest;
    }

    public void setClientNotificationRequest(ClientNotificationEndpointRequest clientNotificationRequest) {
        this.clientNotificationRequest = clientNotificationRequest;
    }
}
