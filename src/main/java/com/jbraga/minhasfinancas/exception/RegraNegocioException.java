package com.jbraga.minhasfinancas.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegraNegocioException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(RegraNegocioException.class);

    public RegraNegocioException(String msg) {
        super(msg);
        logger.warn("Violação de regra de negócio: {}", msg); // Log de aviso
    }
}
