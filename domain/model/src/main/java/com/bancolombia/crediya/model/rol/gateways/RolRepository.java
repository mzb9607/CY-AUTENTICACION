package com.bancolombia.crediya.model.rol.gateways;

import com.bancolombia.crediya.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> findById(Integer id);
    Mono<Rol> findByNombre(String nombre);
}
