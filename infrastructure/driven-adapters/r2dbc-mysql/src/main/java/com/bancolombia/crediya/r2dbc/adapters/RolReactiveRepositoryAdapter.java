package com.bancolombia.crediya.r2dbc.adapters;

import com.bancolombia.crediya.model.rol.Rol;
import com.bancolombia.crediya.model.rol.gateways.RolRepository;
import com.bancolombia.crediya.r2dbc.data.RolData;
import com.bancolombia.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<Rol, RolData, Integer, RolReactiveRepository> implements RolRepository {

    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }

    @Override
    public Mono<Rol> findById(Integer id) {
        return repository.findById(id).map(this::toEntity);
    }

    @Override
    public Mono<Rol> findByNombre(String nombre) {
        return repository.findByNombre(nombre).map(this::toEntity);
    }
}
