package com.bancolombia.crediya.usecase.login;

import com.bancolombia.crediya.model.usuario.gateways.PasswordEncoder;
import com.bancolombia.crediya.model.usuario.gateways.TokenProvider;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class LoginUseCase {

    private static final Logger logger = Logger.getLogger(LoginUseCase.class.getName());

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Mono<String> login(String email, String password) {
        logger.log(Level.INFO, "Iniciando proceso de login para el email: {0}", email);
        return usuarioRepository.findByCorreoElectronico(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario o contraseña inválida")))
                .flatMap(usuario -> {
                    if (passwordEncoder.matches(password, usuario.getPassword())) {
                        logger.log(Level.INFO, "Login exitoso para el email: {0}", email);
                        return Mono.just(tokenProvider.generateToken(usuario));
                    } else {
                        logger.log(Level.WARNING, "Intento de login fallido para el email: {0}", email);
                        return Mono.error(new IllegalArgumentException("Usuario o contraseña inválida"));
                    }
                })
                .doOnError(throwable -> logger.log(Level.SEVERE, "Error en el proceso de login para el email: " + email, throwable));
    }
}
