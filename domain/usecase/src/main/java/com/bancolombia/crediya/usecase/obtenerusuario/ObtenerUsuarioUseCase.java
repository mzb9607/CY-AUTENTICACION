package com.bancolombia.crediya.usecase.obtenerusuario;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class ObtenerUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private static final Logger logger = Logger.getLogger(ObtenerUsuarioUseCase.class.getName());

    public Mono<Usuario> obtenerUsuarioPorDocumento(String documentoIdentidad) {
        logger.info("Iniciando busqueda de usuario con documento: " + documentoIdentidad);
        return usuarioRepository.findByDocumentoIdentidad(documentoIdentidad)
                .doOnSuccess(usuario -> {
                    if(usuario != null){
                        logger.info("Usuario encontrado con documento: " + documentoIdentidad);
                    } else {
                        logger.warning("No se encontro usuario con documento: " + documentoIdentidad);
                    }
                })
                .doOnError(error -> logger.severe("Error al buscar usuario con documento: " + documentoIdentidad + " - " + error.getMessage()));
    }
}
