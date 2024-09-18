package com.jbraga.minhasfinancas.service;

import com.jbraga.minhasfinancas.model.entity.Lancamento;
import com.jbraga.minhasfinancas.model.enums.StatusLancamento;

import java.util.List;

public interface LancamentoService {

    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    void deletar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento lancamentoFiltro);

    void atualizarStatus (Lancamento lancamento, StatusLancamento status);

    void validar(Lancamento lancamento);
}
