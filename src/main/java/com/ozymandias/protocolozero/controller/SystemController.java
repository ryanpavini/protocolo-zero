package com.ozymandias.protocolozero.controller;

import com.ozymandias.protocolozero.dto.BootResponse;
import com.ozymandias.protocolozero.model.FileSystemNode;
import com.ozymandias.protocolozero.repository.FileSystemRepository;
import com.ozymandias.protocolozero.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST responsável pelas rotas de sistema que não exigem
 * autenticação prévia.
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    private static final Logger log = LoggerFactory.getLogger(SystemController.class);

    private final FileSystemRepository repository;
    private final JwtService jwtService;

    public SystemController(FileSystemRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint público para iniciar a conexão com o terminal.
     * Retorna o token JWT Stateless contendo o ID do diretório Raiz (/).
     */
    @PostMapping("/boot")
    public ResponseEntity<BootResponse> bootSequence() {
        log.info("Initiating system boot sequence for new incoming connection...");

        // Busca a pasta raiz (root) no banco de dados
        FileSystemNode root = repository.findByParentIsNull()
                .orElseThrow(() -> new RuntimeException("CRITICAL ERROR: Root directory not found. VFS corrupted."));

        // Gera o token de sessão fixando o usuário como 'guest' e embutindo o ID da pasta raiz
        String token = jwtService.generateToken("guest", root.getId());

        String welcomeMessage = "AETHELGARD OS v1.0.9\nLigação segura estabelecida.\nDigite 'help' para listar os comandos disponíveis.";

        // 4. Cria o envelope (DTO) e envia com status 200 (OK)
        BootResponse response = new BootResponse(token, welcomeMessage, root.getName());

        log.info("Boot sequence complete. Token issued for root directory ID: {}", root.getId());
        return ResponseEntity.ok(response);
    }
}