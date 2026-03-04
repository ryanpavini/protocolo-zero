package com.ozymandias.protocolozero.repository;

import com.ozymandias.protocolozero.model.FileSystemNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileSystemRepository extends JpaRepository<FileSystemNode, Long> {

    // Comando 'ls': Lista todos os filhos (arquivos e pastas) de um diretório específico
    List<FileSystemNode> findByParentId(Long parentId);

    // Comando 'cd' ou 'cat': Busca um arquivo ou pasta específica pelo nome, dentro da pasta atual
    Optional<FileSystemNode> findByNameAndParentId(String name, Long parentId);

    // Sistema: Busca a pasta Raiz (Root '/'), que é a única pasta do sistema que não possui um 'pai'
    Optional<FileSystemNode> findByParentIsNull();
}