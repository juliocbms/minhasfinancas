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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento() {
        // Persistindo um usuário
        Usuario usuario = Usuario.builder()
                .nome("Usuario teste")
                .email("usuario@teste.com")
                .senha("123")
                .build();

        usuario = entityManager.persist(usuario);

        // Criando e persistindo um lançamento
        Lancamento lancamento = Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("lançamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .tipo(TipoLancamento.RECEITA)
                .usuario(usuario)  // Associando o usuário ao lançamento
                .build();

        lancamento = repository.save(lancamento);

        // Verificando se o ID do lançamento não é nulo
        Assertions.assertNotNull(lancamento.getId());
    }

    @Test
    public void deveDeletarUmLancamento() {
        // Persistindo um usuário
        Usuario usuario = Usuario.builder()
                .nome("Usuario teste")
                .email("usuario@teste.com")
                .senha("123")
                .build();

        usuario = entityManager.persist(usuario);

        // Criando e persistindo um lançamento
        Lancamento lancamento = criarLancamento();
        lancamento.setUsuario(usuario);  // Associando o usuário ao lançamento
        lancamento = entityManager.persist(lancamento);

        // Carregando o lançamento para garantir que ele está no contexto de persistência
        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        // Deletando o lançamento
        repository.delete(lancamento);

        // Verificando se o lançamento foi deletado
        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        Assertions.assertNull(lancamentoInexistente);
    }

    private Lancamento criarLancamento() {
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
