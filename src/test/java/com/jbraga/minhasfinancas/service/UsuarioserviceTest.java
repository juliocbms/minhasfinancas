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

    @MockBean
     PasswordEncoder passwordEncoder;



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
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
        // Cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        // Ação e verificação
        Assertions.assertThrows(ErroAutenticacao.class, () -> {
            service.autenticar("email@email.com", "senha");
        });
    }
@Test
    public void  deveLancarErroQUandoSenhaNaoBater(){
        //cenário
    String senha = "senha";
    Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
    Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

    //ação
    Assertions.assertThrows(ErroAutenticacao.class, () -> {
        service.autenticar("email@email.com", "123");
    });
    }

    @Test
    public void deveSalvarUmUsuario(){
        //cenario
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1l)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //ação
       Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

       //verificação
        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertEquals(1l,usuarioSalvo.getId());
        Assertions.assertEquals("nome",usuarioSalvo.getNome());
        Assertions.assertEquals("email@email.com",usuarioSalvo.getEmail());
        Assertions.assertEquals("senha",usuarioSalvo.getSenha());
    }

    @Test
    public void naoDeveSalvarUmUsuarioCOmEmailJACadastrado(){
        //cenario
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email("email@email.com").build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //ação
        Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.salvarUsuario(usuario);
        });

        //verificação
        Mockito.verify(repository, Mockito.never()).save(usuario);
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
