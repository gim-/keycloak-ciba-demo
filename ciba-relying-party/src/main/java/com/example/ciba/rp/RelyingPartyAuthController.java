package com.example.ciba.rp;

import com.example.ciba.dto.UserResponseRequest;
import com.example.ciba.dto.AuthenticationChannelRequest;
import com.example.ciba.dto.AuthenticationChannelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RelyingPartyAuthController {
    private static final Logger log = LoggerFactory.getLogger(RelyingPartyAuthController.class);

    private final RestClient restClient = RestClient.create();
    private final RelyingPartyProperties properties;

    public RelyingPartyAuthController(RelyingPartyProperties properties) {
        this.properties = properties;
    }

    /**
     * Authorization header value storage. For demo purpose, a map is used.
     * In real application, make sure to utilize some temporary storage with expiration.
     */
    private final Map<String, String> authorizationHeaders = new HashMap<>();

    /**
     * Authentication initiation request, coming from the Authorization Server (Keycloak)
     * after Consumption Device (ciba-consumer application) initiates authentication.
     */
    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public void auth(@RequestBody AuthenticationChannelRequest authRequest,
                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("Received authentication channel request: {}", authRequest);

        authorizationHeaders.put(authRequest.requestId(), authorizationHeader);

        // Typically, at this point we must send a notification to the Authenticating Device.
        // For the purposes of the demo, we'll just call a webhook.
        log.info("Sending a notification to the Authentication Device");
        restClient.post()
                .uri(properties.authDeviceNotifyWebhook())
                .body(authRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Callback from Authenticating Device (ciba-authenticator application) with the user result.
     */
    @PostMapping("/auth/result")
    public ResponseEntity<Void> result(@RequestBody UserResponseRequest userResponseRequest) {
        String authorization = authorizationHeaders.get(userResponseRequest.requestId());
        if (authorization == null) {
            log.error("No authorization token found for request id: {}", userResponseRequest.requestId());
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
        }
        authorizationHeaders.remove(userResponseRequest.requestId());

        log.info("Sending status {} to Authorization Server", userResponseRequest.status());
        return restClient.post()
                .uri(properties.cibaCallbackUri())
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AuthenticationChannelResponse(userResponseRequest.status()))
                .retrieve()
                .toBodilessEntity();
    }
}
