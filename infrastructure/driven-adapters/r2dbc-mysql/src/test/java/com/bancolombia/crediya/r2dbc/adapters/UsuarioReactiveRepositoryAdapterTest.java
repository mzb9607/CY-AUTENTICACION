
package com.bancolombia.crediya.r2dbc.adapters;

import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.r2dbc.data.UsuarioData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioReactiveRepositoryAdapterTest {

    @Mock
    private UsuarioReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private UsuarioReactiveRepositoryAdapter adapter;

    private Usuario usuario;
    private UsuarioData usuarioData;

        @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .nombres("Nombres")
                .apellidos("Apellidos")
                .documentoIdentidad("123456789")
                .correoElectronico("test@example.com")
                .password("password123")
                .salarioBase(5000000.0)
                .build();

        usuarioData = new UsuarioData();
        usuarioData.setNombres("Nombres");
        usuarioData.setApellidos("Apellidos");
        usuarioData.setDocumentoIdentidad("123456789");
        usuarioData.setCorreoElectronico("test@example.com");
        usuarioData.setPassword("password123");
        usuarioData.setSalarioBase(5000000.0);
    }

    @Test
    void save() {
        when(mapper.map(any(Usuario.class), eq(UsuarioData.class))).thenReturn(usuarioData);
        when(repository.save(any(UsuarioData.class))).thenReturn(Mono.just(usuarioData));
        when(mapper.map(any(UsuarioData.class), eq(Usuario.class))).thenReturn(usuario);

        StepVerifier.create(adapter.save(usuario))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void findByCorreoElectronico() {
        when(repository.findByCorreoElectronico(any(String.class))).thenReturn(Mono.just(usuarioData));
        when(mapper.map(any(UsuarioData.class), eq(Usuario.class))).thenReturn(usuario);

        StepVerifier.create(adapter.findByCorreoElectronico("test@example.com"))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void findByDocumentoIdentidad() {
        when(repository.findByDocumentoIdentidad(any(String.class))).thenReturn(Mono.just(usuarioData));
        when(mapper.map(any(UsuarioData.class), eq(Usuario.class))).thenReturn(usuario);

        StepVerifier.create(adapter.findByDocumentoIdentidad("123456789"))
                .expectNext(usuario)
                .verifyComplete();
    }
}
