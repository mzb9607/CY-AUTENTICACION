package com.bancolombia.crediya.config;

import com.bancolombia.crediya.model.usuario.gateways.UsuarioRepository;
import com.bancolombia.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = UseCasesConfig.class)
class UseCasesConfigTest {

    @Autowired
    private ApplicationContext context;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    void registrarUsuarioUseCaseBeanExists() {
        RegistrarUsuarioUseCase useCase = context.getBean(RegistrarUsuarioUseCase.class);
        assertNotNull(useCase, "El bean de RegistrarUsuarioUseCase no fue encontrado en el contexto");
    }
}