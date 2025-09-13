package com.bancolombia.crediya.model.usuario.gateways;

import com.bancolombia.crediya.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface TokenProvider {
    String generateToken(Usuario usuario);
    Mono<Usuario> getAuthentication(String token);
}
