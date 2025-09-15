package com.bancolombia.crediya.usecase.registrarusuario;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.model.usuario.gateways.PasswordEncoder;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;
import com.bancolombia.crediya.model.rol.gateways.RolRepository;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private static final Logger LOGGER = Logger.getLogger(RegistrarUsuarioUseCase.class.getName());

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        LOGGER.log(Level.INFO, "Iniciando proceso de registro para el usuario: {0}", usuario.getCorreoElectronico());
        return validarUsuario(usuario)
                .flatMap(this::validarCorreoElectronicoExistente)
                .flatMap(this::validarDocumentoIdentidadExistente)
                .flatMap(this::validarRolExistente)
                .map(u -> {
                    u.setPassword(passwordEncoder.encode(u.getPassword()));
                    LOGGER.log(Level.INFO, "Contraseña codificada para el usuario: {0}", u.getCorreoElectronico());
                    return u;
                })
                .flatMap(usuarioRepository::save)
                .doOnSuccess(u -> LOGGER.log(Level.INFO, "Usuario registrado exitosamente: {0}", u.getCorreoElectronico()))
                .doOnError(e -> LOGGER.log(Level.SEVERE, "Error al registrar usuario: " + e.getMessage(), e));
    }

    public Mono<Usuario> validarCorreoElectronicoExistente(Usuario usuario) {
        LOGGER.log(Level.INFO, "Validando existencia de correo electrónico: {0}", usuario.getCorreoElectronico());
        return usuarioRepository.findByCorreoElectronico(usuario.getCorreoElectronico())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        LOGGER.log(Level.WARNING, "El correo electrónico ya se encuentra registrado: {0}", usuario.getCorreoElectronico());
                        return Mono.error(new IllegalArgumentException("El correo electrónico ya se encuentra registrado."));
                    }
                    LOGGER.log(Level.INFO, "El correo electrónico no se encuentra registrado: {0}", usuario.getCorreoElectronico());
                    return Mono.just(usuario);
                });
    }

    public Mono<Usuario> validarDocumentoIdentidadExistente(Usuario usuario) {
        LOGGER.log(Level.INFO, "Validando existencia de documento de identidad: {0}", usuario.getDocumentoIdentidad());
        return usuarioRepository.findByDocumentoIdentidad(usuario.getDocumentoIdentidad())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        LOGGER.log(Level.WARNING, "El documento de identidad ya se encuentra registrado: {0}", usuario.getDocumentoIdentidad());
                        return Mono.error(new IllegalArgumentException("El documento de identidad ya se encuentra registrado."));
                    }
                    LOGGER.log(Level.INFO, "El documento de identidad no se encuentra registrado: {0}", usuario.getDocumentoIdentidad());
                    return Mono.just(usuario);
                });
    }

    public Mono<Usuario> validarRolExistente(Usuario usuario) {
        LOGGER.log(Level.INFO, "Validando existencia de rol: {0}", usuario.getIdRol());
        return rolRepository.findById(usuario.getIdRol())
                .hasElement()
                .flatMap(exists -> {
                    if (!exists) {
                        String error = "El rol especificado no existe: " + usuario.getIdRol();
                        LOGGER.log(Level.WARNING, error);
                        return Mono.error(new IllegalArgumentException(error));
                    }
                    LOGGER.log(Level.INFO, "El rol existe: {0}", usuario.getIdRol());
                    return Mono.just(usuario);
                });
    }


    public Mono<Usuario> validarUsuario(Usuario usuario) {
        LOGGER.log(Level.INFO, "Validando datos del usuario: {0}", usuario.getCorreoElectronico());
        List<String> validationErrors = new ArrayList<>();
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        if (Objects.isNull(usuario.getNombres()) || usuario.getNombres().trim().isEmpty()) {
            validationErrors.add("nombres");
        }
        if (Objects.isNull(usuario.getApellidos()) || usuario.getApellidos().trim().isEmpty()) {
            validationErrors.add("apellidos");
        }
        if (Objects.isNull(usuario.getPassword()) || usuario.getPassword().trim().isEmpty()) {
            validationErrors.add("password");
        }
        if (Objects.isNull(usuario.getSalarioBase())) {
            validationErrors.add("salario base");
        }
        if (Objects.isNull(usuario.getDocumentoIdentidad()) || usuario.getDocumentoIdentidad().trim().isEmpty()) {
            validationErrors.add("documento de identidad");
        }
 
        if (!validationErrors.isEmpty()) {
            String errorMessage = "Los siguientes campos son obligatorios: " + String.join(", ", validationErrors) + ".";
            LOGGER.log(Level.WARNING, "Error de validación de usuario: {0}", errorMessage);
            return Mono.error(new IllegalArgumentException(errorMessage));
        }
 
        if (usuario.getDocumentoIdentidad().length() > 20) {
            LOGGER.log(Level.WARNING, "El documento de identidad excede el límite de caracteres.");
            return Mono.error(new IllegalArgumentException("El documento de identidad no debe tener más de 20 caracteres."));
        }
        if (usuario.getNombres().length() > 100) {
            LOGGER.log(Level.WARNING, "El nombre excede el límite de caracteres.");
            return Mono.error(new IllegalArgumentException("El nombre no debe tener más de 100 caracteres."));
        }
        if (usuario.getApellidos().length() > 100) {
            LOGGER.log(Level.WARNING, "El apellido excede el límite de caracteres.");
            return Mono.error(new IllegalArgumentException("El apellido no debe tener más de 100 caracteres."));
        }
        if (usuario.getCorreoElectronico().length() > 255) {
            LOGGER.log(Level.WARNING, "El email excede el límite de caracteres.");
            return Mono.error(new IllegalArgumentException("El email no debe tener más de 255 caracteres."));
        }
        if (usuario.getTelefono() != null && usuario.getTelefono().length() > 20) {
            LOGGER.log(Level.WARNING, "El teléfono excede el límite de caracteres.");
            return Mono.error(new IllegalArgumentException("El teléfono no debe tener más de 20 caracteres."));
        }
        if (usuario.getDireccion() != null && usuario.getDireccion().length() > 255) {
            LOGGER.log(Level.WARNING, "La dirección excede el límite de caracteres.");
            return Mono.error(new IllegalArgumentException("La dirección no debe tener más de 255 caracteres."));
        }

        if (!Pattern.matches(emailRegex, usuario.getCorreoElectronico())) {
            LOGGER.log(Level.WARNING, "El formato del correo electrónico es inválido.");
            return Mono.error(new IllegalArgumentException("Debe ingresar un correo electrónico válido."));
        }

        if (usuario.getSalarioBase() < 0 || usuario.getSalarioBase() > 15000000) {
            LOGGER.log(Level.WARNING, "El salario base está fuera del rango permitido.");
            return Mono.error(new IllegalArgumentException("El salario base debe estar entre 0 y 15,000,000."));
        }

        LOGGER.log(Level.INFO, "Validación de usuario exitosa para: {0}", usuario.getCorreoElectronico());
        return Mono.just(usuario);
        
    }
}

