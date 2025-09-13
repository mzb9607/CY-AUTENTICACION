package com.bancolombia.crediya.jwtprovider;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.model.usuario.gateways.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProviderAdapter implements TokenProvider {

    private final SecretKey secretKey;
    private final long validityInMilliseconds;

    public JwtTokenProviderAdapter(@Value("${jwt.secret:defaultSecretKeyWhichIsVeryLongAndSecureAndAtLeast256Bits}") String secret, @Value("${jwt.expiration:3600000}") long validityInMilliseconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    @Override
    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(usuario.getCorreoElectronico())
                .claim("idUsuario", usuario.getIdUsuario().toString()) // BigInteger como String
                .claim("rol", usuario.getIdRol()) // Store role name
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey) // Método actualizado
                .compact();
    }

    @Override
    public Mono<Usuario> getAuthentication(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            BigInteger idUsuario = new BigInteger(claims.get("idUsuario", String.class)); // Recuperar como String
            String documentoIdentidad = claims.get("documentoIdentidad", String.class);
            Integer idRol = claims.get("rol", Integer.class);


            Usuario usuario = Usuario.builder()
                    .idUsuario(idUsuario)
                    .correoElectronico(email)
                    .documentoIdentidad(documentoIdentidad)
                    .idRol(idRol)
                    .build();

            return Mono.just(usuario);
            
        } catch (JwtException | IllegalArgumentException e) {
            return Mono.empty();
        }
    }
}
