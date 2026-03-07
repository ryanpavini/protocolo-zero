package com.ozymandias.protocolozero.dto;

import lombok.Data;

/**
 * DTO que representa a entrada de dados do usuário no terminal.
 */
@Data
public class CommandRequest {
    private String command;
}