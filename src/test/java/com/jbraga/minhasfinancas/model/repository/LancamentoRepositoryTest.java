package com.jbraga.minhasfinancas.model.repository;

import com.jbraga.minhasfinancas.model.entity.Lancamento;
import com.jbraga.minhasfinancas.model.entity.Usuario;
import com.jbraga.minhasfinancas.model.enums.StatusLancamento;
import com.jbraga.minhasfinancas.model.enums.TipoLancamento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@SpringBootTest
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento() {

        Usuario usuario = Usuario.builder()
                .nome("Usuario teste")
                .email("usuario@teste.com")
                .senha("123")
                .build();

        usuario = entityManager.persist(usuario);


        Lancamento lancamento = Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("lançamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .tipo(TipoLancamento.RECEITA)
                .usuario(usuario)
                .build();

        lancamento = repository.save(lancamento);


        Assertions.assertNotNull(lancamento.getId());
    }

    @Test
    public void deveDeletarUmLancamento() {

        Usuario usuario = Usuario.builder()
                .nome("Usuario teste")
                .email("usuario@teste.com")
                .senha("123")
                .build();

        usuario = entityManager.persist(usuario);


        Lancamento lancamento = criarLancamento();
        lancamento.setUsuario(usuario);
        lancamento = entityManager.persist(lancamento);


        lancamento = entityManager.find(Lancamento.class, lancamento.getId());


        repository.delete(lancamento);


        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        Assertions.assertNull(lancamentoInexistente);
    }

    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento.setAno(2018);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        repository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertEquals(lancamentoAtualizado.getAno(), 2018);
        Assertions.assertEquals(lancamentoAtualizado.getDescricao(), "Teste Atualizar");
        Assertions.assertEquals(lancamentoAtualizado.getStatus(), StatusLancamento.CANCELADO);
    }

    @Test
    public  void deveBuscarUmLancamentoPorId(){
        Lancamento lancamento = criarEPersistirUmLancamento();

       Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

       Assertions.assertTrue(lancamentoEncontrado.isPresent());
    }

    private Lancamento criarEPersistirUmLancamento() {
        Usuario usuario = Usuario.builder()
                .nome("Usuario teste")
                .email("usuario@teste.com")
                .senha("123")
                .build();

        usuario = entityManager.persist(usuario);

        Lancamento lancamento = criarLancamento();
        lancamento.setUsuario(usuario);
        entityManager.persist(lancamento);
        return lancamento;
    }

   public static Lancamento criarLancamento() {
        return Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("lançamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .tipo(TipoLancamento.RECEITA)
                .build();
    }
}
