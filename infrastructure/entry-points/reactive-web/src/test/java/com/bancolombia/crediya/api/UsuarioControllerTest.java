
package com.bancolombia.crediya.api;

import com.bancolombia.crediya.api.dto.RegistrarUsuarioResponse;
import com.bancolombia.crediya.api.dto.RegistrarUsuarioRequest;
import com.bancolombia.crediya.model.usuario.Usuario;
import com.bancolombia.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import com.bancolombia.crediya.jwtprovider.JwtTokenProviderAdapter;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {UsuarioController.class})
@ComponentScan(basePackages = "com.bancolombia.crediya.api")
class UsuarioControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private Usuario usuario;
    private RegistrarUsuarioRequest registrarUsuarioRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .nombres("Nombres")
                .apellidos("Apellidos")
                .documentoIdentidad("123456789")
                .correoElectronico("test@example.com")
                .password("password123")
                .salarioBase(5000000.0)
                .idRol(1)
                .build();

        registrarUsuarioRequest = RegistrarUsuarioRequest.builder()
                .nombres("Nombres")
                .apellidos("Apellidos")
                .documentoIdentidad("123456789")
                .correoElectronico("test@example.com")
                .password("password123")
                .salarioBase(5000000.0)
                .idRol(1)
                .build();
    }

    @Test
    void registrarUsuarioExitoso() {
        when(registrarUsuarioUseCase.registrarUsuario(any(Usuario.class))).thenReturn(Mono.just(usuario));

        webTestClient.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrarUsuarioRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrarUsuarioResponse.class)
                .value(response -> {
                    assert response.getNombres().equals(usuario.getNombres());
                    assert response.getApellidos().equals(usuario.getApellidos());
                });
    }

    @Test
    void registrarUsuarioErrorValidacion() {
        when(registrarUsuarioUseCase.registrarUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Error de validacion")));

        webTestClient.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrarUsuarioRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Map.class)
                .value(errorResponse -> {
                    assert errorResponse.get("error").equals("Bad Request");
                    assert errorResponse.get("message").equals("Error de validacion");
                });
    }

    @Test
    void registrarUsuarioErrorInterno() {
        when(registrarUsuarioUseCase.registrarUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(new RuntimeException("Error interno")));

        webTestClient.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrarUsuarioRequest)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(Map.class)
                .value(errorResponse -> {
                    assert errorResponse.get("error").equals("Internal Server Error");
                    assert errorResponse.get("message").equals("Error interno");
                });
    }
}
