# BiblioSystem — Sistema de Gerenciamento de Biblioteca

> **Disciplina:** CMP1611 – Programação Orientada a Objetos com Banco de Dados  
> **Instituição:** Pontifícia Universidade Católica de Goiás (PUC Goiás)  
> **Autor:** Arthur Mamedes Borges 
> **Tecnologias:** Java SE 17 · Swing · MySQL 8 · JDBC · Maven

---

## Sobre o Projeto

O **BiblioSystem** é um sistema desktop de gerenciamento de biblioteca desenvolvido em Java com interface gráfica Swing. Implementa arquitetura em 4 camadas (Model · DAO · Controller · View), os 4 pilares de POO, e acesso a banco de dados MySQL exclusivamente via JDBC.

**Funcionalidades principais:** cadastro de livros, autores, editoras, categorias e leitores; registro e controle de empréstimos e devoluções; reservas; login com controle de acesso; listagem ordenável; internacionalização PT/EN.

---

## Pré-requisitos

Antes de importar e executar o projeto, certifique-se de ter instalado:

| Ferramenta | Versão mínima | Download |
|---|---|---|
| JDK (Java Development Kit) | 17 | [adoptium.net](https://adoptium.net) |
| Eclipse IDE for Java Developers | 2022-09+ | [eclipse.org/downloads](https://www.eclipse.org/downloads/) |
| MySQL Server | 8.0+ | [dev.mysql.com/downloads](https://dev.mysql.com/downloads/mysql/) |
| Maven (opcional — Eclipse já inclui) | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |

> **Dica:** O Eclipse já vem com o Maven embutido (M2E). Não é necessário instalar o Maven separadamente para importar o projeto.
>
> **Dica Docker:** Se preferir não instalar o MySQL localmente, use o Docker — um único comando sobe o banco já configurado.
---

## Configuração do Banco de Dados

### Opção A - MySQL local (HeidiSQL / Workbench)

**Execute esse passo ANTES de importar o projeto.**

#### Passo 1 — Criar o banco via script SQL

1. Abra o **HeidiSQL**, **MySQL Workbench** ou outro cliente MySQL.
2. Conecte-se ao servidor MySQL com usuário `root` (ou outro com privilégios).
3. Abra o arquivo `banco_dados/biblioteca.sql`.
4. Execute o script completo. Ele cria o banco, as tabelas e insere dados de teste.

O script cria automaticamente:
- Banco de dados: **`bibliosystem`**
- Usuário padrão do sistema: `admin` / senha: `admin123`

#### Passo 2 — Configurar a conexão no código

Abra o arquivo:

```
src/main/java/br/pucgoias/biblioteca/util/ConexaoBD.java
```

Verifique (e ajuste se necessário) as constantes de conexão:

```java
private static final String URL    = "jdbc:mysql://localhost:3306/bibliosystem?useSSL=false&serverTimezone=America/Sao_Paulo";
private static final String USUARIO = "root";      // ← altere se necessário
private static final String SENHA   = "root";      // ← altere se necessário
```

### Opção B — Docker Compose (recomendado: zero configuração)

> **Pré-requisito:** Docker Desktop instalado e em execução.

O projeto inclui um `docker-compose.yml` na raiz que sobe o MySQL 8 e executa o script SQL automaticamente.

```bash 
# 1. Na raiz do projeto (onde está o docker-compose.yml), execute:
docker compose up -d

# 2. Aguarde ~15 segundos para o MySQL inicializar completamente.
#    Verifique se o container está rodando:
docker ps
# Deve aparecer: bibliosystem-db   Up X seconds   0.0.0.0:3306->3306/tcp
```

O Docker irá:

- Baixar a imagem `mysql:8.0` automaticamente (se ainda não tiver)
- Criar o banco `bibliosystem` com usuário `root` / senha `root`
- Executar o script `banco_dados/bibliosystem.sql` com todos os dados de teste

> **Nenhuma configuração adicional necessária.** A conexão em `ConexaoBD.java` já aponta para `localhost:3306` com as credenciais corretas.

Para parar o container quando não estiver usando:

```bash
docker compose down
```

Para parar e apagar os dados (reset completo):

```bash
docker compose down -v
```

---

## Como Importar na IDE

O projeto suporta **duas formas** de importação. Escolha a que preferir:

---

### Opção A — Importar pelo arquivo ZIP

1. **Descompacte** o arquivo `CMP1611-MATRICULA-NOME.zip` em uma pasta de sua escolha.  
   Dentro do zip há a pasta `OPROJETO/` contendo o projeto Maven.

2. Abra o **Eclipse**.

3. No menu superior, clique em:  
   **`File`** → **`Import...`**

4. Na janela de importação, expanda a categoria **`Maven`** e selecione:  
   **`Existing Maven Projects`**  
   Clique em **`Next >`**.

5. No campo **`Root Directory`**, clique em **`Browse...`** e navegue até a pasta `OPROJETO/` (onde está o arquivo `pom.xml`).

6. O Eclipse irá detectar automaticamente o projeto. Marque a caixa de seleção que aparece e clique em **`Finish`**.

7. Aguarde o Eclipse baixar as dependências Maven (requer conexão com internet na primeira vez). O progresso aparece na barra inferior direita.

8. Quando concluído, o projeto aparecerá no **Package Explorer** sem erros.

---

### Opção B — Clonar pelo Git

**Pré-requisito:** Git instalado ([git-scm.com](https://git-scm.com/downloads))

#### Via Eclipse (interface gráfica):

1. Abra o Eclipse.

2. No menu superior, clique em:  
   **`File`** → **`Import...`**

3. Expanda a categoria **`Git`** e selecione:  
   **`Projects from Git`**  
   Clique em **`Next >`**.

4. Selecione **`Clone URL`** e clique em **`Next >`**.

5. No campo **`URL`**, cole a URL do repositório:

    ```
   https://github.com/ArthemizLabs/BiblioSystem.git
   ```

7. Clique em **`Next >`** nas próximas telas (branch `main`).

8. Na tela **`Import Projects`**, selecione:  
   **`Import existing Eclipse projects`** → **`Next >`** → **`Finish`**.

9. Se o projeto não for reconhecido automaticamente, use a **Opção A** após o clone:  
   **`File`** → **`Import`** → **`Maven`** → **`Existing Maven Projects`** → aponte para a pasta clonada.

#### Via terminal (alternativa rápida):

```bash
# 1. Clone o repositório
git clone https://github.com/ArthemizLabs/biblio-system.git

# 2. Acesse a pasta
cd biblio-system

# 3. Compile e baixe as dependências via Maven
mvn clean install -DskipTests

# 4. Abra o Eclipse e importe como Maven Project (File → Import → Maven → Existing Maven Projects)
```

---

## Executando o Sistema

Após importar com sucesso no Eclipse:

1. Confirme que **não há erros** no projeto (ícones vermelhos no Package Explorer).  
   Se houver erros de build, clique com botão direito no projeto → **`Maven`** → **`Update Project...`** → **`OK`**.

2. Navegue até a classe principal:
   ```
   src/main/java/br/pucgoias/biblioteca/App.java
   ```

3. Clique com o botão direito sobre `App.java` → **`Run As`** → **`Java Application`**.

4. A tela de login do BiblioSystem abrirá. Use as credenciais padrão:
   - **Usuário:** `admin`
   - **Senha:** `admin123`

### Via terminal (alternativa rápida):

```bash
# 1. Acesse a pasta
cd biblio-system

# 2. Compile e baixe as dependências via Maven
mvn compile

# 3. Execute o projeto
mvn exec:java
```

---

## Estrutura do Projeto

```
├── biblio-system
│   ├── banco_dados/
│       └── biblioteca.sql           ← Script MySQL completo com dados de teste
│   ├── src/
│   │   └── main/
│   │       ├── java/br/pucgoias/biblioteca/
│   │       │       ├── controller/  ← Regras de negócio
│   │       │       ├── dao/         ← Acesso a dados via JDBC
│   │       │       ├── model/       ← Entidades (Livro, Autor, Leitor...)
│   │       │       └── util/        ← ConexaoBD, Mensagens (i18n), Validador
│   │       │       ├── view/        ← Janelas Swing (JFrame, JInternalFrame)
│   │       │       ├── App.java     ← Inicializador do sistema 
│   │       └── resources/
│   │           └── messages_en.properties   ← Textos em Inglês
│   │           ├── messages_pt.properties   ← Textos em Português
│   ├── docker-compose.yml           ← Script Docker para subir o banco 
│   ├── pom.xml                      ← Dependências Maven (MySQL Connector/J)
```

---

## Arquitetura do Projeto

```
┌─────────────────────────────────────────────────────┐
│  VIEW  (Swing: JFrame, JInternalFrame, JDialog)     │
│  Apenas exibe dados e captura eventos do usuário    │
├─────────────────────────────────────────────────────┤
│  CONTROLLER  (Regras de negócio e validações)       │
│  LivroController, LeitorController, etc.            │
├─────────────────────────────────────────────────────┤
│  DAO  (Data Access Object — SQL fica aqui)          │
│  Implementa interfaces: ILivroDAO, ILeitorDAO...    │
├─────────────────────────────────────────────────────┤
│  MODEL  (Entidades POO)                             │
│  ItemAcervo (abstrata) → Livro, Leitor, Usuario...  │
└─────────────────────────────────────────────────────┘
             ↓ JDBC apenas
        [ MySQL 8 — bibliosystem ]
```

**Pilares de POO demonstrados:**
- **Encapsulamento** — atributos privados com getters/setters em todas as entidades
- **Herança** — `Livro` herda de `ItemAcervo` (classe abstrata)
- **Polimorfismo** — método `toString()` e `exibir()` sobrescritos
- **Abstração** — interfaces `IGenericDAO<T>` para todos os DAOs
- **Exceções customizadas** — `BancoDadosException`, `ValidacaoException`

---

## Internacionalização (i18n)

O sistema suporta **Português (PT-BR)** e **Inglês (EN-US)**. Para trocar o idioma:

1. No menu da aplicação, acesse **`Configurações`** → **`Idioma`**.
2. Selecione o idioma desejado e confirme.
3. Os textos da interface serão atualizados via `ResourceBundle`.

---

## Credenciais Padrão (dados de teste)

| Usuário | Senha | Perfil |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `funcionario` | `func123` | Funcionário |

> Estes dados são inseridos automaticamente pelo script `biblioteca.sql`.

---

## Solução de Problemas Comuns

| Problema | Solução |
|---|---|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Execute `Maven → Update Project` para baixar o connector. |
| `Access denied for user 'root'@'localhost'` | Verifique usuário/senha em `ConexaoBD.java`. |
| `Unknown database 'bibliosystem'` | Execute o script `biblioteca.sql` antes de rodar o sistema. |
| Erros vermelhos após importação | Botão direito no projeto → `Maven` → `Update Project` → marque `Force Update` → `OK`. |
| Interface em branco / não abre | Confirme que está executando `App.java` como **Java Application**, não como Applet. |
| Container Docker não sobe (`port already in use`) | Outro processo usa a porta 3306 (provavelmente MySQL local). Pare o MySQL local ou mude a porta no `docker-compose.yml`: `"3307:3306"` e atualize a URL em `ConexaoBD.java`. |
| `bibliosystem-db` sobe mas banco não existe | O script SQL ainda está sendo executado. Aguarde 20–30 segundos e tente novamente. |

---

## 📄 Licença

Projeto acadêmico desenvolvido para fins de avaliação.  
© 2026 Arthur Mamedes Borges — [ArthemizLabs](https://github.com/ArthemizLabs)
