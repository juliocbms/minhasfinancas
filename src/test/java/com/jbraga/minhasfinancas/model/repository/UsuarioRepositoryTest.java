package com.jbraga.minhasfinancas.model.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jbraga.minhasfinancas.model.entity.Usuario;




@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveVerificarAExistenciaDeUmEmail() {
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        boolean result = repository.existsByEmail("usuario@email.com");
        Assertions.assertTrue(result);

    }

    @Test
    public void deveRetornarfalsoQUandoNaoHouverUsuarioCadastradoEmail(){
        boolean result = repository.existsByEmail("usuario@email.com");
        Assertions.assertFalse(result);
    }

@Test
public  void devePersistirUmUsuarioNaBasedeDAdos(){
    Usuario usuario = criarUsuario();
   Usuario usuarioSalvo = repository.save(usuario);
   Assertions.assertNotNull(usuarioSalvo.getId());
}

@Test
public void deveBuscarUmUsuarioPorEmail(){
    Usuario usuario = criarUsuario();
    entityManager.persist(usuario);
    Optional<Usuario> result= repository.findByEmail("usuario@email.com");
    Assertions.assertTrue(result.isPresent());
}
    @Test
    public void deveRetornarVazioaoBuscarUsuarioProEmailQUandoNaoExisteNaBase(){
        Optional<Usuario> result= repository.findByEmail("usuario@email.com");
        Assertions.assertFalse(result.isPresent());
    }

    public static Usuario criarUsuario() {
        return Usuario
                .builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }
}
