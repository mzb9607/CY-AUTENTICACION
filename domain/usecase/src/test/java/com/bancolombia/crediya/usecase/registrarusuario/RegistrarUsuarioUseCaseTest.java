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

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .nombres("Nombres")
                .apellidos("Apellidos")
                .documentoIdentidad("123456789")
                .correoElectronico("test@example.com")
                .salarioBase(5000000.0)
                .telefono("1234567890")
                .direccion("Calle Falsa 123")
                .build();
    }

    @Test
    void registrarUsuarioExitoso() {
        when(usuarioRepository.findByCorreoElectronico(any())).thenReturn(Mono.empty());
        when(usuarioRepository.findByDocumentoIdentidad(any())).thenReturn(Mono.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void registrarUsuarioCorreoExistente() {
        when(usuarioRepository.findByCorreoElectronico(any())).thenReturn(Mono.just(usuario));

        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El correo electrónico ya se encuentra registrado."))
                .verify();
    }

    @Test
    void registrarUsuarioDocumentoExistente() {
        when(usuarioRepository.findByCorreoElectronico(any())).thenReturn(Mono.empty());
        when(usuarioRepository.findByDocumentoIdentidad(any())).thenReturn(Mono.just(usuario));

        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El documento de identidad ya se encuentra registrado."))
                .verify();
    }

    @Test
    void registrarUsuarioCamposObligatoriosFaltantes() {
        usuario.setNombres(null);
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Los siguientes campos son obligatorios: nombres"))
                .verify();
    }

    @Test
    void registrarUsuarioConDocumentoIdentidadExcedeLongitud() {
        usuario.setDocumentoIdentidad("123456789012345678901");
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El documento de identidad no debe tener más de 20 caracteres."))
                .verify();
    }

    @Test
    void registrarUsuarioConEmailInvalido() {
        usuario.setCorreoElectronico("correo-invalido");
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Debe ingresar un correo electrónico válido."))
                .verify();
    }

    @Test
    void registrarUsuarioConSalarioFueraDeRango() {
        usuario.setSalarioBase(20000000.0);
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El salario base debe estar entre 0 y 15,000,000."))
                .verify();
    }

    @Test
    void registrarUsuarioApellidosFaltantes() {
        usuario.setApellidos(null);
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Los siguientes campos son obligatorios: apellidos"))
                .verify();
    }

    @Test
    void registrarUsuarioSalarioBaseFaltante() {
        usuario.setSalarioBase(null);
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Los siguientes campos son obligatorios: salario base"))
                .verify();
    }

    @Test
    void registrarUsuarioDocumentoIdentidadFaltante() {
        usuario.setDocumentoIdentidad(null);
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Los siguientes campos son obligatorios: documento de identidad"))
                .verify();
    }

    @Test
    void registrarUsuarioNombresExcedeLongitud() {
        usuario.setNombres("a".repeat(101));
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre no debe tener más de 100 caracteres."))
                .verify();
    }

    @Test
    void registrarUsuarioApellidosExcedeLongitud() {
        usuario.setApellidos("a".repeat(101));
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El apellido no debe tener más de 100 caracteres."))
                .verify();
    }

    @Test
    void registrarUsuarioCorreoElectronicoExcedeLongitud() {
        usuario.setCorreoElectronico("a".repeat(256) + "@example.com");
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El email no debe tener más de 255 caracteres."))
                .verify();
    }

    @Test
    void registrarUsuarioTelefonoExcedeLongitud() {
        usuario.setTelefono("1".repeat(21));
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El teléfono no debe tener más de 20 caracteres."))
                .verify();
    }

    @Test
    void registrarUsuarioDireccionExcedeLongitud() {
        usuario.setDireccion("a".repeat(256));
        StepVerifier.create(registrarUsuarioUseCase.registrarUsuario(usuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("La dirección no debe tener más de 255 caracteres."))
                .verify();
    }
}