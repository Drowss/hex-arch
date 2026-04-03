package com.hexarch.security;

import com.hexarch.gateways.PasswordEncoderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RequiredArgsConstructor
@Slf4j
public class PasswordEncoderAdapter implements PasswordEncoderGateway {
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<String> encode(String password) {
        return Mono.fromSupplier(() -> passwordEncoder.encode(password))
                .doOnSuccess(result -> log.info("Success encoding", kv("password", result)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> matches(String password, String encryptedPassword) {
        return Mono.fromSupplier(() -> passwordEncoder.matches(password, encryptedPassword))
                .doOnSuccess(result -> log.info("password matcher result", kv("matcher", result)))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
