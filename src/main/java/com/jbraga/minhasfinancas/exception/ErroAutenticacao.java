package com.jbraga.minhasfinancas.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErroAutenticacao extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(ErroAutenticacao.class);

    public ErroAutenticacao(String mensagem) {
        super(mensagem);
        logger.error("Erro de autenticação: {}", mensagem); // Log do erro
    }
}
