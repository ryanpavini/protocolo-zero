package com.ozymandias.protocolozero.service;

import com.ozymandias.protocolozero.dto.CommandResponse;
import com.ozymandias.protocolozero.model.Directory;
import com.ozymandias.protocolozero.model.File;
import com.ozymandias.protocolozero.model.FileSystemNode;
import com.ozymandias.protocolozero.repository.FileSystemRepository;
import com.ozymandias.protocolozero.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço responsável por interpretar e executar os comandos do terminal virtual.
 * Mantém a arquitetura Stateless retornando novos tokens quando o estado (diretório atual) muda.
 */
@Service
public class CommandService {

    private static final Logger log = LoggerFactory.getLogger(CommandService.class);

    private final FileSystemRepository repository;
    private final JwtService jwtService;

    public CommandService(FileSystemRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    /**
     * Ponto de entrada principal para qualquer comando digitado.
     */
    public CommandResponse executeCommand(String rawCommand, Long currentDirId, String username) {
        if (rawCommand == null || rawCommand.trim().isEmpty()) {
            return buildResponse("", null);
        }

        // Divide o comando do argumento (ex: ["cd", "var"] ou apenas ["ls"])
        String[] parts = rawCommand.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : "";

        log.debug("Executando comando '{}' com argumento '{}' no dirId {}", command, argument, currentDirId);

        return switch (command) {
            case "help" -> executeHelp();
            case "ls" -> executeLs(currentDirId);
            case "cat" -> executeCat(argument, currentDirId);
            case "cd" -> executeCd(argument, currentDirId, username);
            case "whoami" -> buildResponse(username + "\nNível de privilégio: restrito", null);
            default -> buildResponse("Comando não encontrado: " + command, null);
        };
    }

    // --- Implementação dos Comandos ---

    private CommandResponse executeHelp() {
        String helpText = """
                COMANDOS DISPONÍVEIS:
                  help   - Mostra esta mensagem
                  ls     - Lista o conteúdo do diretório atual
                  cd     - Navega para um diretório (ex: cd pasta ou cd ..)
                  cat    - Lê o conteúdo de um ficheiro (ex: cat readme.txt)
                  whoami - Mostra o utilizador atual
                """;
        return buildResponse(helpText, null);
    }

    private CommandResponse executeLs(Long currentDirId) {
        List<FileSystemNode> children = repository.findByParentId(currentDirId);

        if (children.isEmpty()) {
            return buildResponse("", null);
        }

        String output = children.stream()
                .filter(node -> !node.isHidden())
                .map(node -> {
                    String type = (node instanceof Directory) ? "DIR " : "FILE";
                    return String.format("%s\t%s\t%s", type, node.getPermissions(), node.getName());
                })
                .collect(Collectors.joining("\n"));

        return buildResponse(output, null);
    }

    private CommandResponse executeCat(String fileName, Long currentDirId) {
        if (fileName.isEmpty()) {
            return buildResponse("Uso: cat <nome_do_ficheiro>", null);
        }

        Optional<FileSystemNode> nodeOpt = repository.findByNameAndParentId(fileName, currentDirId);

        if (nodeOpt.isEmpty()) {
            return buildResponse("cat: " + fileName + ": Ficheiro não encontrado", null);
        }

        FileSystemNode node = nodeOpt.get();
        if (node instanceof Directory) {
            return buildResponse("cat: " + fileName + ": É um diretório", null);
        }

        File file = (File) node;
        if (file.isEncrypted()) {
            return buildResponse("cat: " + fileName + ": Ficheiro encriptado. Acesso negado.", null);
        }

        return buildResponse(file.getContent(), null);
    }

    private CommandResponse executeCd(String targetName, Long currentDirId, String username) {
        if (targetName.isEmpty()) {
            return buildResponse("Uso: cd <nome_do_diretório>", null);
        }

        // Lógica para voltar uma pasta
        if (targetName.equals("..")) {
            FileSystemNode currentDir = repository.findById(currentDirId).orElseThrow();
            if (currentDir.getParent() != null) {
                // Gera um NOVO token com o ID da pasta pai
                String newToken = jwtService.generateToken(username, currentDir.getParent().getId());
                return buildResponse("", newToken);
            } else {
                return buildResponse("", null); // Já está na raiz, não faz nada
            }
        }

        // Lógica para entrar numa pasta
        Optional<FileSystemNode> targetNodeOpt = repository.findByNameAndParentId(targetName, currentDirId);

        if (targetNodeOpt.isEmpty()) {
            return buildResponse("cd: " + targetName + ": Diretório não encontrado", null);
        }

        FileSystemNode targetNode = targetNodeOpt.get();
        if (targetNode instanceof File) {
            return buildResponse("cd: " + targetName + ": Não é um diretório", null);
        }
        String newToken = jwtService.generateToken(username, targetNode.getId());
        return buildResponse("", newToken);
    }

    private CommandResponse buildResponse(String output, String newToken) {
        return CommandResponse.builder()
                .output(output)
                .token(newToken)
                .build();
    }
}