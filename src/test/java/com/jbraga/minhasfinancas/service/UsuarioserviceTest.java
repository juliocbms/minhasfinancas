package com.jbraga.minhasfinancas.service;

import com.jbraga.minhasfinancas.exception.ErroAutenticacao;
import com.jbraga.minhasfinancas.exception.RegraNegocioException;
import com.jbraga.minhasfinancas.model.entity.Usuario;
import com.jbraga.minhasfinancas.model.repository.UsuarioRepository;
import com.jbraga.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class UsuarioserviceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void deveAutenticarUmUsuarioComSucesso() {

        String email = "email@email.com";
        String senha = "senha";


        String senhaCripto = encoder.encode(senha);


        Usuario usuario = Usuario.builder()
                .email(email)
                .senha(senhaCripto)
                .id(1L)
                .build();


        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));


        Usuario result = service.autenticar(email, senha);


        Assertions.assertNotNull(result, "O resultado não deve ser nulo");
        Assertions.assertEquals(email, result.getEmail(), "O email retornado não corresponde ao esperado");


        Assertions.assertNotEquals(senha, result.getSenha(), "A senha retornada não deve ser a senha em texto claro.");


        Assertions.assertTrue(encoder.matches(senha, result.getSenha()), "A senha fornecida não corresponde à senha criptografada.");
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {

        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());


        Assertions.assertThrows(ErroAutenticacao.class, () -> {
            service.autenticar("email@email.com", "senha");
        });
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater() {

        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));


        Assertions.assertThrows(ErroAutenticacao.class, () -> {
            service.autenticar("email@email.com", "123");
        });
    }

    @Test
    public void deveSalvarUmUsuario() {

        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());


        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();


        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);


        Usuario usuarioSalvo = service.salvarUsuario(usuario);


        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertEquals(1L, usuarioSalvo.getId());
        Assertions.assertEquals("nome", usuarioSalvo.getNome());
        Assertions.assertEquals("email@email.com", usuarioSalvo.getEmail());


        Assertions.assertTrue(encoder.matches("senha", usuarioSalvo.getSenha()), "A senha criptografada não corresponde à senha fornecida.");
    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {

        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email("email@email.com").build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);


        Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.salvarUsuario(usuario);
        });


        Mockito.verify(repository, Mockito.never()).save(usuario);
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);


        Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validarEmail("email@email.com");
        });
    }
}
