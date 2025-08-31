package com.bancolombia.crediya.usecase.registrarusuario;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        return validarUsuario(usuario)
                .flatMap(this::validarCorreoElectronicoExistente)
                .flatMap(this::validarDocumentoIdentidadExistente)
                .flatMap(usuarioRepository::save);
    }

    public Mono<Usuario> validarCorreoElectronicoExistente(Usuario usuario) {
        return usuarioRepository.findByCorreoElectronico(usuario.getCorreoElectronico())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El correo electrónico ya se encuentra registrado."));
                    }
                    return Mono.just(usuario);
                });
    }

    public Mono<Usuario> validarDocumentoIdentidadExistente(Usuario usuario) {
        return usuarioRepository.findByDocumentoIdentidad(usuario.getDocumentoIdentidad())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El documento de identidad ya se encuentra registrado."));
                    }
                    return Mono.just(usuario);
                });
    }


    public Mono<Usuario> validarUsuario(Usuario usuario) {
        List<String> validationErrors = new ArrayList<>();
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        // Validaciones de campos obligatorios
        if (Objects.isNull(usuario.getNombres()) || usuario.getNombres().trim().isEmpty()) {
            validationErrors.add("nombres");
        }
        if (Objects.isNull(usuario.getApellidos()) || usuario.getApellidos().trim().isEmpty()) {
            validationErrors.add("apellidos");
        }
        if (Objects.isNull(usuario.getSalarioBase())) {
            validationErrors.add("salario base");
        }
        if (Objects.isNull(usuario.getDocumentoIdentidad()) || usuario.getDocumentoIdentidad().trim().isEmpty()) {
            validationErrors.add("documento de identidad");
        }
 
        if (!validationErrors.isEmpty()) {
            String errorMessage = "Los siguientes campos son obligatorios: " + String.join(", ", validationErrors) + ".";
            return Mono.error(new IllegalArgumentException(errorMessage));
        }
 
        // Validaciones de longitud
        if (usuario.getDocumentoIdentidad().length() > 20) {
            return Mono.error(new IllegalArgumentException("El documento de identidad no debe tener más de 20 caracteres."));
        }
        if (usuario.getNombres().length() > 100) {
            return Mono.error(new IllegalArgumentException("El nombre no debe tener más de 100 caracteres."));
        }
        if (usuario.getApellidos().length() > 100) {
            return Mono.error(new IllegalArgumentException("El apellido no debe tener más de 100 caracteres."));
        }
        if (usuario.getCorreoElectronico().length() > 255) {
            return Mono.error(new IllegalArgumentException("El email no debe tener más de 255 caracteres."));
        }
        if (usuario.getTelefono() != null && usuario.getTelefono().length() > 20) {
            return Mono.error(new IllegalArgumentException("El teléfono no debe tener más de 20 caracteres."));
        }
        if (usuario.getDireccion() != null && usuario.getDireccion().length() > 255) {
            return Mono.error(new IllegalArgumentException("La dirección no debe tener más de 255 caracteres."));
        }

        // Validación de formato de correo electrónico
        if (!Pattern.matches(emailRegex, usuario.getCorreoElectronico())) {
            return Mono.error(new IllegalArgumentException("Debe ingresar un correo electrónico válido."));
        }

        // Validación de rango de salario_base
        if (usuario.getSalarioBase() < 0 || usuario.getSalarioBase() > 15000000) {
            return Mono.error(new IllegalArgumentException("El salario base debe estar entre 0 y 15,000,000."));
        }

        return Mono.just(usuario);
        
    }
}

