package com.bancolombia.crediya.jwtprovider;

import com.bancolombia.crediya.model.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderAdapterTest {

    private JwtTokenProviderAdapter jwtTokenProviderAdapter;
    private final String secret = "thisIsAVeryLongAndSecureSecretKeyForTestingPurposesAndItShouldBeAtLeast256BitsLong";
    private final long validityInMilliseconds = 3600000; // 1 hour
    private SecretKey testSecretKey;

    @BeforeEach
    void setUp() {
        jwtTokenProviderAdapter = new JwtTokenProviderAdapter(secret, validityInMilliseconds);
        testSecretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        Usuario usuario = Usuario.builder()
                .idUsuario(BigInteger.valueOf(1L))
                .correoElectronico("test@example.com")
                .idRol(1)
                .build();

        String token = jwtTokenProviderAdapter.generateToken(usuario);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parser()
                .verifyWith(testSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(usuario.getCorreoElectronico(), claims.getSubject());
        assertEquals(usuario.getIdUsuario().toString(), claims.get("idUsuario", String.class));
        assertEquals(usuario.getIdRol(), claims.get("rol", Integer.class));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void getAuthentication_ShouldReturnUserForValidToken() {
        Usuario usuario = Usuario.builder()
                .idUsuario(BigInteger.valueOf(1L))
                .correoElectronico("test@example.com")
                .idRol(1)
                .build();

        String token = jwtTokenProviderAdapter.generateToken(usuario);

        Mono<Usuario> result = jwtTokenProviderAdapter.getAuthentication(token);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getIdUsuario().equals(usuario.getIdUsuario()) &&
                        u.getCorreoElectronico().equals(usuario.getCorreoElectronico()) &&
                        u.getIdRol().equals(usuario.getIdRol()))
                .verifyComplete();
    }

    @Test
    void getAuthentication_ShouldReturnEmptyMonoForInvalidToken() {
        String invalidToken = "invalid.token.string";

        Mono<Usuario> result = jwtTokenProviderAdapter.getAuthentication(invalidToken);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void getAuthentication_ShouldReturnEmptyMonoForExpiredToken() throws InterruptedException {
        // Create a token with a very short validity period
        JwtTokenProviderAdapter shortLivedTokenProvider = new JwtTokenProviderAdapter(secret, 1);

        Usuario usuario = Usuario.builder()
                .idUsuario(BigInteger.valueOf(1L))
                .correoElectronico("test@example.com")
                .idRol(1)
                .build();

        String token = shortLivedTokenProvider.generateToken(usuario);

        // Wait for the token to expire
        Thread.sleep(10);

        Mono<Usuario> result = jwtTokenProviderAdapter.getAuthentication(token);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }
}
