package com.jbraga.minhasfinancas.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LancamentoDTO {

    private  Long id;
    String descricao;
    private  Integer mes;
    private  Integer ano;
    private BigDecimal valor;
    private  Long usuario;
    private String tipo;
    private  String status;

}
