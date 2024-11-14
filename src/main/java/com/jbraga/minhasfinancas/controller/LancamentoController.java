package com.jbraga.minhasfinancas.controller;

import com.jbraga.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.jbraga.minhasfinancas.api.dto.LancamentoDTO;
import com.jbraga.minhasfinancas.exception.RegraNegocioException;
import com.jbraga.minhasfinancas.model.entity.Lancamento;
import com.jbraga.minhasfinancas.model.entity.Usuario;
import com.jbraga.minhasfinancas.model.enums.StatusLancamento;
import com.jbraga.minhasfinancas.model.enums.TipoLancamento;
import com.jbraga.minhasfinancas.service.LancamentoService;
import com.jbraga.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LancamentoController {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<?> buscar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "dia", required = false) Integer dia,
            @RequestParam(value = "dataCadastro", required = false) String dataCadastro,
            @RequestParam("usuario") Long idUsuario
    ) {
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setNome(nome);
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        lancamentoFiltro.setDia(dia);
        lancamentoFiltro.setDataCadastro(LocalDate.parse(dataCadastro));

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
        }
        lancamentoFiltro.setUsuario(usuario.get());
        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento entidade = converte(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity<>(entidade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/atualizar")
    public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return service.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converte(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity<>("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusDTO dto){
return  service.obterPorId(id).map( entity -> {
StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
if (statusSelecionado == null){
    return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
}
try {
    entity.setStatus(statusSelecionado);
    service.atualizar(entity);
    return ResponseEntity.ok(entity);
}catch (RegraNegocioException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
}


}).orElseGet(() ->
        new ResponseEntity<>("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long id) {
        return service.obterPorId(id).map(entidade -> {
            service.deletar(entidade);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }).orElseGet(() ->
                new ResponseEntity<>("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
    }

    private Lancamento converte(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setDia(dto.getDia());
        lancamento.setValor(dto.getValor());
        lancamento.setNome(dto.getNome());
        lancamento.setDataCadastro(LocalDate.parse(dto.getDataCadastro()));

        Usuario usuario = usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado."));

        lancamento.setUsuario(usuario);

        if (dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        if (dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }
}
