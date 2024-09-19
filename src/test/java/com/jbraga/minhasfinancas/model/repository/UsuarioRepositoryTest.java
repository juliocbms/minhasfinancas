package com.jbraga.minhasfinancas.model.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
        //cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //ação/ execução
        boolean result = repository.existsByEmail("usuario@email.com");

        //verificacao
        Assertions.assertTrue(result);

    }

    @Test
    public void deveRetornarfalsoQUandoNaoHouverUsuarioCadastradoEmail(){
        //cenário


        //ação
        boolean result = repository.existsByEmail("usuario@email.com");

        //verificação
        Assertions.assertFalse(result);
    }

@Test
public  void devePersistirUmUsuarioNaBasedeDAdos(){
        //cenario
    Usuario usuario = criarUsuario();
    //ação
   Usuario usuarioSalvo = repository.save(usuario);
//verificação
   Assertions.assertNotNull(usuarioSalvo.getId());
}

@Test
public void deveBuscarUmUsuarioPorEmail(){
        //cenario
    Usuario usuario = criarUsuario();
    entityManager.persist(usuario);

    //verificacao
    Optional<Usuario> result= repository.findByEmail("usuario@email.com");

    //ação
    Assertions.assertTrue(result.isPresent());
}
    @Test
    public void deveRetornarVazioaoBuscarUsuarioProEmailQUandoNaoExisteNaBase(){
        //cenario

        //verificacao
        Optional<Usuario> result= repository.findByEmail("usuario@email.com");

        //ação
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
