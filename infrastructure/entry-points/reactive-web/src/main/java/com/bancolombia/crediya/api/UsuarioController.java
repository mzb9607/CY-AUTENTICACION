package com.bancolombia.crediya.api;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un nuevo usuario en el sistema.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Usuario.class))),
                    @ApiResponse(responseCode = "400", description = "Error de validación",
                            content = @Content(mediaType = "text/plain")),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                            content = @Content(mediaType = "text/plain"))
            }
    )
    public Mono<ResponseEntity<Object>> registrarUsuario(@RequestBody Usuario usuario) {
        logger.info("Request para registrar usuario recibida.");
        return registrarUsuarioUseCase.registrarUsuario(usuario)
                .flatMap(savedUser -> {
                    logger.info("Usuario registrado exitosamente. Correo electrónico: {}", savedUser.getCorreoElectronico());
                    return Mono.just(ResponseEntity.ok().body((Object)savedUser));
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    logger.error("Validation error: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
                })
                .onErrorResume(Exception.class, e -> {
                    logger.error("Internal server error: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Internal server error: " + e.getMessage()));
                });
    }
}
