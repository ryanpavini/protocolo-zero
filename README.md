# Protocolo Zero - Backend API

## Sobre o Projeto

O **Protocolo Zero** é uma API backend desenvolvida para suportar um
jogo de simulação de terminal (Aethelgard OS) via web. O sistema foi
construído utilizando **Spring Boot** e fornece um **Sistema de Arquivos
Virtual (VFS)** armazenado em banco de dados relacional.

A navegação do usuário é controlada por meio de **tokens JWT**,
permitindo uma arquitetura **stateless** e escalável. O projeto aplica
princípios de **orientação a objetos**, incluindo **herança no
mapeamento objeto-relacional (ORM)** para simular o comportamento de
diretórios e arquivos em um ambiente **Linux-like**.

------------------------------------------------------------------------

# Tecnologias e Ferramentas

-   **Linguagem:** Java 21
-   **Framework:** Spring Boot 4.0.3
-   **Segurança:** Spring Security, JJWT (JSON Web Token)
-   **Persistência:** Spring Data JPA, Hibernate
-   **Banco de Dados:** PostgreSQL 16
-   **Infraestrutura:** Docker e Docker Compose
-   **Gerenciamento de Dependências:** Maven

------------------------------------------------------------------------

# Arquitetura e Principais Funcionalidades

## Virtual File System (VFS)

Modelagem baseada em herança:

-   `FileSystemNode` (entidade base)
-   `Directory`
-   `File`

Essa estrutura permite **consultas hierárquicas recursivas** e simula
permissões de arquivos e diretórios.

## Autenticação Stateless

O estado de navegação do usuário **não é armazenado no servidor**.

O **ID do diretório atual** é incluído como um **Claim dentro do token
JWT**.\
Sempre que o usuário muda de diretório (`cd`), um **novo token é gerado
e enviado ao cliente**.

## Command Interpreter

Um serviço responsável pelo **parsing e execução de comandos do
terminal**.

`CommandService` interpreta comandos enviados como texto e os converte
em operações do sistema:

-   `ls`
-   `cd`
-   `cat`
-   `help`
-   `whoami`

## Database Seeder (DataLoader)

Rotina de inicialização automática que cria a estrutura inicial do
sistema caso o banco esteja vazio.

Diretórios iniciais:

/ ├── home └── var

Além disso, arquivos iniciais de **lore** são gerados automaticamente.

------------------------------------------------------------------------

# Pré-requisitos

Para executar o projeto localmente:

-   **Java Development Kit (JDK) 21**
-   **Maven**
-   **Docker Desktop** ou **Docker Engine + Docker Compose**

------------------------------------------------------------------------

# Como Executar (Ambiente de Desenvolvimento)

## 1. Clonar o repositório

``` bash
git clone https://github.com/ryanpavini/protocolo-zero.git
cd protocolo-zero
```

## 2. Subir o banco de dados com Docker

O PostgreSQL foi configurado para usar a **porta 5433**.
``` bash
docker-compose up -d
```

## 3. Executar a aplicação Spring Boot

``` bash
mvn spring-boot:run
```

A API estará disponível em:

http://localhost:8080

------------------------------------------------------------------------

# Endpoints Principais

## Inicialização do Sistema

### POST `/api/system/boot`

Rota pública responsável por iniciar o sistema.

**Retorna:** - Mensagem de boas-vindas - Token JWT contendo o estado
inicial (`/`)

------------------------------------------------------------------------

## Execução de Comandos

### POST `/api/terminal/command`

Rota protegida que executa comandos do terminal.

**Header obrigatório:**

Authorization: Bearer `<token>`{=html}

**Body (JSON):**

``` json
{
  "command": "ls"
}
```

**Resposta:** - Saída do terminal - Novo token JWT caso o estado tenha
mudado (ex: `cd`)

------------------------------------------------------------------------

# Autor

**Ryan Pavini**

LinkedIn\
https://www.linkedin.com/in/ryan-pavini/
