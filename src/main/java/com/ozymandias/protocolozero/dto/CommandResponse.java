package com.ozymandias.protocolozero.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO que carrega a resposta do servidor após a execução de um comando.
 */
@Data
@Builder
public class CommandResponse {

    private String output;
    private String currentPath;
    private String token;
}