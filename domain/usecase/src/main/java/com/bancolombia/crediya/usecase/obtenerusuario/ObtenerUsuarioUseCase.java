package com.bancolombia.crediya.usecase.obtenerusuario;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ObtenerUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public Mono<Usuario> obtenerUsuarioPorDocumento(String documentoIdentidad) {
        return usuarioRepository.findByDocumentoIdentidad(documentoIdentidad);
    }
}
