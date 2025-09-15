package com.bancolombia.crediya.api;

import com.bancolombia.crediya.api.dto.RegistrarUsuarioRequest;
import com.bancolombia.crediya.api.dto.RegistrarUsuarioResponse;
import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.usecase.obtenerusuario.ObtenerUsuarioUseCase;
import com.bancolombia.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class UsuarioController {

    //private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;

    @PostMapping("/api/v1/usuarios")
    @PreAuthorize("hasAnyRole('1', '2')")
    public Mono<ResponseEntity<?>> registrarUsuario(@RequestBody RegistrarUsuarioRequest request) {
        //logger.info("Iniciando el proceso de registro de usuario.");
        return Mono.just(request)
                .map(req -> Usuario.builder()
                        .documentoIdentidad(req.getDocumentoIdentidad())
                        .nombres(req.getNombres())
                        .apellidos(req.getApellidos())
                        .fechaNacimiento(req.getFechaNacimiento())
                        .direccion(req.getDireccion())
                        .telefono(req.getTelefono())
                        .correoElectronico(req.getCorreoElectronico())
                        .password(req.getPassword())
                        .salarioBase(req.getSalarioBase())
                        .idRol(req.getIdRol())
                        .build())
                .flatMap(usuario -> registrarUsuarioUseCase.registrarUsuario(usuario))
                .map(savedUser -> {
                    //logger.info("Usuario registrado exitosamente. ID: {}, Correo: {}", savedUser.getIdUsuario(), savedUser.getCorreoElectronico());
                    return ResponseEntity.ok().body(RegistrarUsuarioResponse.builder()
                            .idUsuario(savedUser.getIdUsuario())
                            .documentoIdentidad(savedUser.getDocumentoIdentidad())
                            .nombres(savedUser.getNombres())
                            .apellidos(savedUser.getApellidos())
                            .fechaNacimiento(savedUser.getFechaNacimiento())
                            .direccion(savedUser.getDireccion())
                            .telefono(savedUser.getTelefono())
                            .correoElectronico(savedUser.getCorreoElectronico())
                            .salarioBase(savedUser.getSalarioBase())
                            .idRol(savedUser.getIdRol())
                            .build());
                });
    }

    @GetMapping("/api/v1/usuarios")
    @PreAuthorize("hasAnyRole('1', '2')")
    public Mono<ResponseEntity<RegistrarUsuarioResponse>> obtenerUsuario(@RequestParam String documento) {
        return obtenerUsuarioUseCase.obtenerUsuarioPorDocumento(documento)
                .map(user -> ResponseEntity.ok().body(RegistrarUsuarioResponse.builder()
                        .idUsuario(user.getIdUsuario())
                        .documentoIdentidad(user.getDocumentoIdentidad())
                        .nombres(user.getNombres())
                        .apellidos(user.getApellidos())
                        .fechaNacimiento(user.getFechaNacimiento())
                        .direccion(user.getDireccion())
                        .telefono(user.getTelefono())
                        .correoElectronico(user.getCorreoElectronico())
                        .salarioBase(user.getSalarioBase())
                        .idRol(user.getIdRol())
                        .build()));
    }


}
