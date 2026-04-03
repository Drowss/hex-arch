package com.hexarch.api.person;

import com.hexarch.api.person.mapper.UserMapper;
import com.hexarch.api.person.request.LoginRequest;
import com.hexarch.api.person.request.UserRequest;
import com.hexarch.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

import java.time.Duration;

import static com.hexarch.api.util.ValidateFields.validateFields;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserHandler {
    private final UserUseCase userUseCase;

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(UserRequest.class)
                .doOnSuccess(userRequest -> {
                    log.info("Create user entry request", kv("userRequest", userRequest));
                    validateFields(userRequest);
                })
                .map(UserMapper.MAPPER::toDomain)
                .flatMap(userUseCase::save)
                .map(UserMapper.MAPPER::toResponse)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .doOnError(error ->  log.info("Create user error entry request {}", error.getMessage()))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue("Something went wrong"));
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        boolean securedLogin = request.headers().firstHeader("x-https-secure") == null;
        return request.bodyToMono(LoginRequest.class)
                .doOnSuccess(userRequest -> {
                    log.info("Login user entry request", kv("loginRequest", userRequest));
                    validateFields(userRequest);
                })
                .map(UserMapper.MAPPER::toDomain)
                .flatMap(userUseCase::login)
                .flatMap(token -> {
                    ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                            .httpOnly(true)
                            .path("/")
                            .maxAge(Duration.ofMinutes(15))
                            .secure(securedLogin)
                            .build();
                    return ServerResponse.ok()
                            .cookie(cookie)
                            .bodyValue("Login exitoso");
                })
                .doOnError(error ->  log.info("Login user error entry request {}", error.getMessage()))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue("Something went wrong"));
    }

    public Mono<ServerResponse> securedPath(ServerRequest request) {
        return Mono.justOrEmpty(request.cookies().getFirst("auth_token"))
                .flatMap(cookie -> ServerResponse.ok()
                        .bodyValue("Cookie value: " + cookie.getValue()))
                .switchIfEmpty(ServerResponse.status(HttpStatus.FORBIDDEN)
                        .bodyValue("Forbidden: cookie not found"));
    }
}