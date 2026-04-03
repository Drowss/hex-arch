package com.hexarch;

import com.hexarch.gateways.TokenProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAdapter implements TokenProvider {
    @Value("${adapter.jwt.secret}")
    private String secret;

    @Override
    public Mono<String> createToken(User user) {
        return Mono.fromSupplier(() -> {
            try {
                JWTClaimsSet claims = new JWTClaimsSet.Builder()
                        .subject(user.getName())
                        .issueTime(Date.from(Instant.now()))
                        .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                        .claim("user", user.getName())
                        .build();

                JWSSigner signer = new MACSigner(secret.getBytes());

                SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
                signedJWT.sign(signer);

                return signedJWT.serialize();

            } catch (JOSEException e) {
                throw new RuntimeException("Error creating JWT", e);
            }
        });
    }
}
