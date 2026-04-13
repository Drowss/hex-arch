package com.hexarch.usecase;

import com.hexarch.Login;
import com.hexarch.User;
import com.hexarch.gateways.PasswordEncoderGateway;
import com.hexarch.gateways.TokenProvider;
import com.hexarch.gateways.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserGateway userGateway;
    @Mock
    private PasswordEncoderGateway passwordEncoderGateway;
    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private UserUseCase useCase;

    @Test
    void save_shouldEncodePasswordAndSaveUser() {
        User user = User.builder().name("dev").password("1234").email("[email]").build();
        User saved = user.toBuilder().password("encoded123").build();

        when(passwordEncoderGateway.encode("1234")).thenReturn(Mono.just("encoded123"));
        when(userGateway.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.save(user))
                .expectNext(saved)
                .verifyComplete();

        verify(passwordEncoderGateway).encode("1234");
        verify(userGateway).save(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        Login login = Login.builder().name("dev").password("1234").build();
        User user = User.builder().name("dev").password("encoded123").build();

        when(userGateway.findByName("dev")).thenReturn(Mono.just(user));
        when(passwordEncoderGateway.matches("1234", "encoded123")).thenReturn(Mono.just(true));
        when(tokenProvider.createToken(user)).thenReturn(Mono.just("jwt-token-123"));

        StepVerifier.create(useCase.login(login))
                .expectNext("jwt-token-123")
                .verifyComplete();
    }

    @Test
    void login_shouldThrowError_whenUserNotFound() {
        Login login = Login.builder().name("unknown").password("1234").build();
        when(userGateway.findByName("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.login(login))
                .expectErrorMatches(e -> e.getMessage().equals("Invalid credentials"))
                .verify();
    }

    @Test
    void login_shouldThrowError_whenPasswordDoesNotMatch() {
        Login login = Login.builder().name("dev").password("wrong").build();
        User user = User.builder().name("dev").password("encoded123").build();

        when(userGateway.findByName("dev")).thenReturn(Mono.just(user));
        when(passwordEncoderGateway.matches("wrong", "encoded123")).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.login(login))
                .expectErrorMatches(e -> e.getMessage().equals("Invalid credentials"))
                .verify();
    }
}
