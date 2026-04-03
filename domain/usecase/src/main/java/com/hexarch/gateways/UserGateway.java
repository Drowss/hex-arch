package com.hexarch.gateways;

import com.hexarch.User;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<User> save(User user);
    Mono<User> findByName(String name);
}
