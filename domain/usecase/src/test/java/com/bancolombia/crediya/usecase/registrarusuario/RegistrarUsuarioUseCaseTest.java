package com.bancolombia.crediya.usecase.registrarusuario;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private Usuario testUsuario;

    @BeforeEach
    void setUp() {
        testUsuario = Usuario.builder()
                .nombres("Juan")
                .apellidos("Perez")
                .correoElectronico("juan.perez@example.com")
                .documentoIdentidad("123456789")
                .salarioBase(1000000.0)
                .build();
    }

    @Test
    void registrarUsuario_success() {
        when(usuarioRepository.findByCorreoElectronico(any(String.class))).thenReturn(Mono.empty());
        when(usuarioRepository.findByDocumentoIdentidad(any(String.class))).thenReturn(Mono.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(testUsuario));

        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(testUsuario))
                .expectNext(testUsuario)
                .verifyComplete();
    }
}
