package com.hexarch.config;

import com.hexarch.gateways.PasswordEncoderGateway;
import com.hexarch.gateways.TokenProvider;
import com.hexarch.gateways.UserGateway;
import com.hexarch.usecase.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {
    @Bean
    public UserUseCase userUseCase(UserGateway userGateway, PasswordEncoderGateway passwordEncoderGateway, TokenProvider tokenProvider) {
        return new UserUseCase(userGateway, passwordEncoderGateway, tokenProvider);
    }
}
