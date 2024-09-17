package com.jbraga.minhasfinancas.service;

import com.jbraga.minhasfinancas.exception.RegraNegocioException;
import com.jbraga.minhasfinancas.model.entity.Usuario;
import com.jbraga.minhasfinancas.model.repository.UsuarioRepository;
import com.jbraga.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class UsuarioserviceTest {

    @Autowired
    private UsuarioService service;

    @MockBean
    private UsuarioRepository repository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void deveAutenticarUmUsuarioComSucesso() {
        // Cenário
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder()
                .email(email)
                .senha(senha)
                .id(1L)
                .build();

        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
        Mockito.when(passwordEncoder.matches(senha, usuario.getSenha())).thenReturn(true); // Simula a correspondência da senha

        // Ação
        Usuario result = service.autenticar(email, senha);

        // Verificação
        Assertions.assertNotNull(result, "O resultado não deve ser nulo");
        Assertions.assertEquals(email, result.getEmail(), "O email retornado não corresponde ao esperado");
        Assertions.assertEquals(senha, result.getSenha(), "A senha retornada não corresponde ao esperado");
    }

    @Test
    public void deveValidarEmail() {
        // Cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        // Ação
        Assertions.assertDoesNotThrow(() -> {
            service.validarEmail("email@email.com");
        });
    }


    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        // Cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        // Ação e verificação
        Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validarEmail("email@email.com");
        });
    }
}
