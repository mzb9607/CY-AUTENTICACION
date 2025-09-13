package com.bancolombia.crediya.r2dbc.adapters;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.r2dbc.data.UsuarioData;
import com.bancolombia.crediya.r2dbc.helper.ReactiveAdapterOperations;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;

import reactor.core.publisher.Mono;
import java.math.BigInteger;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Usuario,
    UsuarioData,
    BigInteger, UsuarioReactiveRepository
> implements UsuarioRepository
{

    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> {
            return mapper.map(d, Usuario.class);
        });
    }

    public Mono<Usuario> findByCorreoElectronico(String correo) {
        log.info("Buscando usuario por correo electrónico: {}", correo);
        return repository.findByCorreoElectronico(correo).map(this::toEntity)
            .doOnSuccess(u -> log.info("Usuario encontrado por correo electrónico: {}", correo))
            .doOnError(e -> log.error("Error al buscar usuario por correo electrónico {}: {}", correo, e.getMessage()));
    }

    public Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad) {
        log.info("Buscando usuario por documento de identidad: {}", documentoIdentidad);
        return repository.findByDocumentoIdentidad(documentoIdentidad).map(this::toEntity)
            .doOnSuccess(u -> log.info("Usuario encontrado por documento de identidad: {}", documentoIdentidad))
            .doOnError(e -> log.error("Error al buscar usuario por documento de identidad {}: {}", documentoIdentidad, e.getMessage()));
    }
    
}
