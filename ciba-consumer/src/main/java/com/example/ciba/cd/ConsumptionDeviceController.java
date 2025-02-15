package com.example.ciba.cd;

import com.example.ciba.cd.session.AuthRequestSessionAttributes;
import com.example.ciba.dto.ClientNotificationEndpointRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
public class ConsumptionDeviceController {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionDeviceController.class);

    private final SecureRandom secureRandom = new SecureRandom();
    private final AuthRequestSessionAttributes sessionAttrs;
    private final ConsumptionDeviceProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ConsumptionDeviceController(AuthRequestSessionAttributes sessionAttrs,
                                       ObjectMapper objectMapper,
                                       ConsumptionDeviceProperties properties) {
        this.sessionAttrs = sessionAttrs;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.restClient = RestClient.builder()
                .messageConverters(List.of(
                        new FormHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter()
                ))
                .build();
    }

    @GetMapping
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/poll")
    public String requestAuthentication(
            @RequestParam String username,
            @RequestParam String message,
            Model model
    ) {

        byte[] randomBytes = new byte[128];
        secureRandom.nextBytes(randomBytes);
        String requestId = Base64.getEncoder().encodeToString(randomBytes);

        MultiValueMap<String, String> cibaRequest = new LinkedMultiValueMap<>();
        cibaRequest.add("client_id", properties.clientId());
        cibaRequest.add("client_secret", properties.clientSecret());
        cibaRequest.add("scope", "profile");
        cibaRequest.add("login_hint", username);
        cibaRequest.add("binding_message", message);
        cibaRequest.add("request_id", requestId);

        ClientNotificationEndpointRequest clientAuthRequest = restClient.post()
                .uri(properties.cibaUri())
                .body(cibaRequest)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((clientRequest, clientResponse) -> {
                    if (clientResponse.getStatusCode() == HttpStatus.OK) {
                        return objectMapper.readValue(clientResponse.getBody(),
                                ClientNotificationEndpointRequest.class);
                    }
                    throw new RestClientException("Unexpected response status: "
                            + clientResponse.getStatusCode() + ", "
                            + StreamUtils.copyToString(clientResponse.getBody(), StandardCharsets.UTF_8));
                });

        sessionAttrs.setClientNotificationRequest(clientAuthRequest);

        model.addAttribute("clientAuthRequest", clientAuthRequest);

        return "polling";
    }

    @GetMapping("/poll")
    private String poll(Model model) {
        ClientNotificationEndpointRequest authRequest =
                sessionAttrs.getClientNotificationRequest();

        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
        tokenRequest.add("client_id", properties.clientId());
        tokenRequest.add("client_secret", properties.clientSecret());
        tokenRequest.add("grant_type", "urn:openid:params:grant-type:ciba");
        tokenRequest.add("auth_req_id", authRequest.authRequestId());

        String tokenResponse = restClient.post()
                .uri(properties.tokenUri())
                .body(tokenRequest)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange((clientRequest, clientResponse) -> {
                    if (clientResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        JsonNode jsonNode = objectMapper.readTree(clientResponse.getBody());
                        String error = jsonNode.get("error").asText();
                        if (!error.equals("authorization_pending")) {
                            model.addAttribute("finished", true);
                        }
                        return jsonNode.get("error_description").asText();
                    }

                    String response = StreamUtils.copyToString(clientResponse.getBody(), StandardCharsets.UTF_8);
                    model.addAttribute("finished", true);
                    model.addAttribute("success", true);
                    model.addAttribute("tokenResponseMap", objectMapper.readValue(response, Map.class));
                    return response;
                });

        model.addAttribute("tokenResponse", tokenResponse);
        model.addAttribute("clientAuthRequest", authRequest);

        return "polling";
    }
}
