package com.hexarch.usecase;

import com.hexarch.Login;
import com.hexarch.User;
import com.hexarch.gateways.PasswordEncoderGateway;
import com.hexarch.gateways.TokenProvider;
import com.hexarch.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserGateway userGateway;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final TokenProvider tokenProvider;

    public Mono<User> save(User user) {
        return passwordEncoderGateway.encode(user.getPassword())
                .flatMap(encryptedPassword -> userGateway.save(user.toBuilder().password(encryptedPassword).build()));
    }

    public Mono<String> login(Login login) {
        return userGateway.findByName(login.getName())
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .flatMap(user ->
                        passwordEncoderGateway.matches(login.getPassword(), user.getPassword())
                                .flatMap(result -> {
                                    if (result) {
                                        return Mono.just(user);
                                    } else {
                                        return Mono.error(new RuntimeException("Invalid credentials"));
                                    }
                                })
                )
                .flatMap(tokenProvider::createToken);
    }
}
