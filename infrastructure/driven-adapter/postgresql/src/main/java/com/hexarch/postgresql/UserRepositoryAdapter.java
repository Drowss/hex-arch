package com.hexarch.postgresql;

import com.hexarch.User;
import com.hexarch.gateways.UserGateway;
import com.hexarch.postgresql.mapper.UserMapper;
import com.hexarch.postgresql.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryAdapter implements UserGateway {
    private final UserRepository userRepository;

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(UserMapper.MAPPER.toEntity(user))
                .doOnSuccess(userEntity -> log.info("user saved", kv("user", userEntity)))
                .map(UserMapper.MAPPER::toDomain)
                .onErrorMap(error -> {
                    if (error instanceof DuplicateKeyException) return new RuntimeException("Name already exists");
                    return error;
                })
                .doOnError(error -> log.error("Unexpected error saving user", error));
    }

    @Override
    public Mono<User> findByName(String name) {
        return userRepository.findByName(name)
                .doOnSuccess(userEntity -> log.info("user found by name", kv("user", userEntity)))
                .map(UserMapper.MAPPER::toDomain)
                .doOnError(error -> log.error("Unexpected error finding user", error));
    }
}
