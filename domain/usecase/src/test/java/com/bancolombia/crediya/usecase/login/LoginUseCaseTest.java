package com.bancolombia.crediya.usecase.login;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.model.usuario.gateways.PasswordEncoder;
import com.bancolombia.crediya.model.usuario.gateways.TokenProvider;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;

import static org.mockito.Mockito.*;

class LoginUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private Usuario testUser;
    private String testEmail = "test@example.com";
    private String testPassword = "rawPassword";
    private String encodedPassword = "encodedPassword";
    private String testToken = "mockToken";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = Usuario.builder()
                .idUsuario(BigInteger.valueOf(123L))
                .correoElectronico(testEmail)
                .password(encodedPassword)
                .idRol(1)
                .build();
    }

    @Test
    void login_Successful() {
        when(usuarioRepository.findByCorreoElectronico(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
        when(tokenProvider.generateToken(testUser)).thenReturn(testToken);

        Mono<String> result = loginUseCase.login(testEmail, testPassword);

        StepVerifier.create(result)
                .expectNext(testToken)
                .verifyComplete();

        verify(usuarioRepository, times(1)).findByCorreoElectronico(testEmail);
        verify(passwordEncoder, times(1)).matches(testPassword, encodedPassword);
        verify(tokenProvider, times(1)).generateToken(testUser);
    }

    @Test
    void login_UserNotFound() {
        when(usuarioRepository.findByCorreoElectronico(testEmail)).thenReturn(Mono.empty());

        Mono<String> result = loginUseCase.login(testEmail, testPassword);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Usuario o contraseña inválida"))
                .verify();

        verify(usuarioRepository, times(1)).findByCorreoElectronico(testEmail);
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }

    @Test
    void login_InvalidPassword() {
        when(usuarioRepository.findByCorreoElectronico(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(false);

        Mono<String> result = loginUseCase.login(testEmail, testPassword);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Usuario o contraseña inválida"))
                .verify();

        verify(usuarioRepository, times(1)).findByCorreoElectronico(testEmail);
        verify(passwordEncoder, times(1)).matches(testPassword, encodedPassword);
        verifyNoInteractions(tokenProvider);
    }
}
