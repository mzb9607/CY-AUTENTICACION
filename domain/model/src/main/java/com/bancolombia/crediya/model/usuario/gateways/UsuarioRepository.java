package com.bancolombia.crediya.model.usuario.gateways;

import java.math.BigInteger;

import com.bancolombia.crediya.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> save(Usuario usuario);
    Mono<Usuario> findByCorreoElectronico(String correo);
    Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad);

}
