package com.bancolombia.crediya.r2dbc.adapters;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.r2dbc.data.UsuarioData;
import com.bancolombia.crediya.r2dbc.helper.ReactiveAdapterOperations;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;

import reactor.core.publisher.Mono;
import java.math.BigInteger;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
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
        return repository.findByCorreoElectronico(correo).map(this::toEntity);
    }

    public Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad) {
        return repository.findByDocumentoIdentidad(documentoIdentidad).map(this::toEntity);
    }
    
}
