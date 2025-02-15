package com.example.ciba.ad;

import com.example.ciba.dto.AuthenticationChannelRequest;
import com.example.ciba.dto.UserResponseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class AuthenticationDeviceRestController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationDeviceRestController.class);

    private final AuthenticationDeviceProperties properties;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RestClient restClient;

    public AuthenticationDeviceRestController(AuthenticationDeviceProperties properties,
                                              SimpMessagingTemplate simpMessagingTemplate) {
        this.properties = properties;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.restClient = RestClient.create();
    }

    @PostMapping("/notify")
    public void authRequestNotify(@RequestBody AuthenticationChannelRequest authRequest) {
        log.info("Received new authentication request: {}", authRequest);

        simpMessagingTemplate.convertAndSend("/topic/auth", authRequest);
    }

    @MessageMapping("/status")
    public void status(@RequestBody UserResponseRequest userResponse) {
        log.info("Received user response: {}", userResponse.status());

        restClient.post()
                .uri(properties.relyingPartyResultUri())
                .body(userResponse)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();
    }
}
