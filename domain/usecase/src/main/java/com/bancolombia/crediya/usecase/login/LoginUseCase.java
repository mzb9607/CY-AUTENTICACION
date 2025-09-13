package com.bancolombia.crediya.usecase.login;

import com.bancolombia.crediya.model.usuario.gateways.PasswordEncoder;
import com.bancolombia.crediya.model.usuario.gateways.TokenProvider;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Mono<String> login(String email, String password) {
        return usuarioRepository.findByCorreoElectronico(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario o contraseña inválida")))
                .flatMap(usuario -> {
                    if (passwordEncoder.matches(password, usuario.getPassword())) {
                        return Mono.just(tokenProvider.generateToken(usuario));
                    } else {
                        return Mono.error(new IllegalArgumentException("Usuario o contraseña inválida"));
                    }
                });
    }
}
