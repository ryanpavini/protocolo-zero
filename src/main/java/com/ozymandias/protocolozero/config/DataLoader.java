package com.ozymandias.protocolozero.config;

import com.ozymandias.protocolozero.model.Directory;
import com.ozymandias.protocolozero.model.File;
import com.ozymandias.protocolozero.repository.FileSystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de bootstrap responsável por inicializar o estado padrão
 * do Sistema de Arquivos Virtual (VFS) durante a inicialização da aplicação.
 */
@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner initDatabase(FileSystemRepository repository) {
        return args -> {
            // Valida se a raiz já existe para evitar duplicação de dados no restart do servidor
            if (repository.findByParentIsNull().isEmpty()) {
                log.info("Initializing Virtual File System (VFS) seed process...");

                // --- Nó Raiz (Root) ---
                Directory root = new Directory();
                root.setName("/");
                root.setOwner("root");
                root.setPermissions("rwxr-xr-x");
                repository.save(root);

                // --- Diretórios Principais do Sistema ---
                Directory home = new Directory();
                home.setName("home");
                home.setParent(root);
                home.setOwner("root");
                repository.save(home);

                Directory var = new Directory();
                var.setName("var");
                var.setParent(root);
                var.setOwner("root");
                repository.save(var);

                // --- Arquivos de Lore / Instruções Iniciais ---
                File readme = new File();
                readme.setName("readme.txt");
                readme.setParent(home);
                readme.setOwner("admin");
                readme.setContent("BEM-VINDO AO TERMINAL AETHELGARD.\nSistema a operar em modo de segurança.\nUse o comando 'ls' para listar ficheiros e 'cat' para ler.");
                repository.save(readme);

                log.info("VFS seed process completed successfully.");
            } else {
                log.info("VFS already seeded. Skipping initialization.");
            }
        };
    }
}