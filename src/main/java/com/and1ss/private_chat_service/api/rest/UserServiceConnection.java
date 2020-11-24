package com.and1ss.private_chat_service.api.rest;

import com.and1ss.private_chat_service.api.dto.AccountInfoRetrievalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserServiceConnection {
    private WebClient webClient;

    private String userServiceBaseAddr;

    @Autowired
    public UserServiceConnection(Environment env) {
        userServiceBaseAddr = env.getProperty("user_service_base_addr");

        this.webClient = WebClient.builder()
                .baseUrl(userServiceBaseAddr)
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                ).build();
    }

    public Mono<AccountInfoRetrievalDTO> authorizeUserByAccessToken(String accessToken) {
        return webClient
                .method(HttpMethod.GET)
                .uri("/auth/identify")
                .body(BodyInserters.fromValue(accessToken))
                .retrieve()
                .bodyToMono(AccountInfoRetrievalDTO.class);
    }

    public Mono<AccountInfoRetrievalDTO> getUserById(UUID id) {
        return webClient
                .method(HttpMethod.GET)
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(AccountInfoRetrievalDTO.class);
    }
}
