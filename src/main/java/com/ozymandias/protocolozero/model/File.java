package com.ozymandias.protocolozero.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "file")
@Getter @Setter
public class File extends FileSystemNode {

    // O conteúdo texto do arquivo (Logs, E-mails, Lore)
    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean isEncrypted = false;

    // Senha necessária para descriptografar (se isEncrypted for true)
    private String decryptionKey;
}