package com.ozymandias.protocolozero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO responsável por carregar a resposta
 * inicial do servidor para o cliente logo após o boot.
 */
@Data
@AllArgsConstructor
public class BootResponse {
    private String token;
    private String message;
    private String currentDirectory;
}