package com.bancolombia.crediya.r2dbc.adapters;

import com.bancolombia.crediya.r2dbc.data.UsuarioData;

import java.math.BigInteger;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioData, BigInteger>, ReactiveQueryByExampleExecutor<UsuarioData> {

    Mono<UsuarioData> findByCorreoElectronico(String correo);
    Mono<UsuarioData> findByDocumentoIdentidad(String documentoIdentidad);
}