package com.jbraga.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jbraga.minhasfinancas.exception.RegraNegocioException;
import com.jbraga.minhasfinancas.model.entity.Lancamento;
import com.jbraga.minhasfinancas.model.entity.Usuario;
import com.jbraga.minhasfinancas.model.enums.StatusLancamento;
import com.jbraga.minhasfinancas.model.enums.TipoLancamento;
import com.jbraga.minhasfinancas.model.repository.LancamentoRepository;
import com.jbraga.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.jbraga.minhasfinancas.service.impl.LancamentoServiceImpl;


@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;
    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento() {
        // cenário
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        // execução
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        // verificação
        assertEquals(lancamentoSalvo.getId(), lancamento.getId());
        assertEquals(StatusLancamento.PENDENTE, lancamento.getStatus());
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        // cenário
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        // execução e verificação
        assertThrows(RegraNegocioException.class, () -> service.salvar(lancamentoASalvar));
        verify(repository, never()).save(lancamentoASalvar);
    }

    @Test
    public void deveAtualizarUmLancamento() {
        // cenário
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        doNothing().when(service).validar(lancamentoSalvo);

        when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        // execução
        service.atualizar(lancamentoSalvo);

        // verificação
        verify(repository, times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        // execução e verificação
        assertThrows(NullPointerException.class, () -> service.atualizar(lancamento));
        verify(repository, never()).save(lancamento);
    }

    @Test
    public void deveDeletarUmLancamento() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        // execução
        service.deletar(lancamento);

        // verificação
        verify(repository).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        // execução e verificação
        assertThrows(NullPointerException.class, () -> service.deletar(lancamento));
        verify(repository, never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamentos() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);
        when(repository.findAll(any(Example.class))).thenReturn(lista);

        // execução
        List<Lancamento> resultado = service.buscar(lancamento);

        // verificação
        assertEquals(1, resultado.size());
        assertEquals(lancamento, resultado.get(0));
    }

    @Test
    public void deveAtualizarOStatusDeUmLancamento() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        doReturn(lancamento).when(service).atualizar(lancamento);

        // execução
        service.atualizarStatus(lancamento, novoStatus);

        // verificação
        assertEquals(lancamento.getStatus(), novoStatus);
        verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterUmLancamentoPorID() {
        // cenário
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        // execução
        Optional<Lancamento> resultado = service.obterPorId(id);

        // verificação
        assertTrue(resultado.isPresent());
    }

    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
        // cenário
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        // execução
        Optional<Lancamento> resultado = service.obterPorId(id);

        // verificação
        assertFalse(resultado.isPresent());
    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();

        // Verifica a validação da descrição
        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe uma descrição válida.", erro.getMessage());

        lancamento.setDescricao("");
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe uma descrição válida.", erro.getMessage());

        lancamento.setDescricao("Salario");

        // Verifica a validação do mês
        lancamento.setMes(0);
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um mês válido.", erro.getMessage());

        lancamento.setMes(13);
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um mês válido.", erro.getMessage());

        lancamento.setMes(1);

        // Verifica a validação do dia
        lancamento.setDia(-1);
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um dia válido.", erro.getMessage());

        lancamento.setDia(32);
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um dia válido.", erro.getMessage());

        lancamento.setDia(15);

        // Verifica a validação do ano
        lancamento.setAno(0);
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um ano válido.", erro.getMessage());

        lancamento.setAno(202);
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um ano válido.", erro.getMessage());

        lancamento.setAno(2020);

        // Verifica a validação do usuário
        lancamento.setUsuario(new Usuario());
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um usuário.", erro.getMessage());

        lancamento.getUsuario().setId(1L);

        // Verifica a validação do valor
        lancamento.setValor(BigDecimal.ZERO);
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um valor válido.", erro.getMessage());

        lancamento.setValor(BigDecimal.valueOf(1));

        // Verifica a validação do tipo de lançamento
        lancamento.setTipo(null);
        erro = assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
        assertEquals("Informe um tipo de lançamento.", erro.getMessage());
    }




    @Test
    public void deveObterSaldoPorUsuario() {
        // cenário
        Long idUsuario = 1L;

        when(repository
                .obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO))
                .thenReturn(BigDecimal.valueOf(100));

        when(repository
                .obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO))
                .thenReturn(BigDecimal.valueOf(50));

        // execução
        BigDecimal saldo = service.obterSaldoPorUsuario(idUsuario);

        // verificação
        Assertions.assertEquals(saldo, BigDecimal.valueOf(50));
    }

}
