package com.hexarch.gateways;

import com.hexarch.User;
import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> createToken(User user);
}
