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

| Ferramenta                           | Versão mínima | Download                                                          |
|--------------------------------------|---------------|-------------------------------------------------------------------|
| JDK (Java Development Kit)           | 17            | [adoptium.net](https://adoptium.net)                              |
| Eclipse IDE for Java Developers      | 2022-09+      | [eclipse.org/downloads](https://www.eclipse.org/downloads/)       |
| MySQL Server                         | 8.0+          | [dev.mysql.com/downloads](https://dev.mysql.com/downloads/mysql/) |
| Maven (opcional — Eclipse já inclui) | 3.8+          | [maven.apache.org](https://maven.apache.org/download.cgi)         |

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
3. Abra o arquivo `banco_dados/bibliosystem.sql`.
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
private static final String URL = "jdbc:mysql://localhost:3306/bibliosystem?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci";
private static final String USUARIO = "root";      // ← altere se necessário
private static final String SENHA   = "root";      // ← altere se necessário
```

> **Importante:** o parâmetro `connectionCollation=utf8mb4_unicode_ci` garante que a conexão use `utf8mb4` na negociação com o servidor MySQL, evitando corrupção de caracteres especiais (acentos, cedilhas) no armazenamento e na leitura dos dados.

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

> O projeto foi desenvolvido no **IntelliJ IDEA**, mas é um **projeto Maven padrão** e abre sem ajustes em qualquer IDE (Eclipse, IntelliJ ou VS Code). As instruções abaixo priorizam o Eclipse.

Há **duas formas** de importar. Escolha a que preferir:

---

### Opção A — Importar pelo arquivo ZIP (formato da entrega)

1. **Descompacte** o arquivo `CMP1611-MATRICULA-NOME.zip` em uma pasta de sua escolha.  
   Dentro dele há a pasta `OPROJETO/`, que contém o projeto Maven (com o arquivo `pom.xml` na raiz).

2. Abra o **Eclipse**.

3. No menu superior, clique em **`File`** → **`Import...`**.

4. Expanda a categoria **`Maven`**, selecione **`Existing Maven Projects`** e clique em **`Next >`**.

5. No campo **`Root Directory`**, clique em **`Browse...`** e aponte para a pasta **`OPROJETO/`** (onde está o `pom.xml`).

6. O Eclipse detecta o projeto e marca o `pom.xml` na lista. Clique em **`Finish`**.

7. Na primeira importação, o Eclipse baixa as dependências Maven (requer internet). Acompanhe o progresso na barra inferior direita.

8. Concluído, o projeto aparece no **Package Explorer** sem erros.

---

### Opção B — Clonar pelo Git

**Pré-requisito:** Git instalado ([git-scm.com](https://git-scm.com/downloads)).

#### Via Eclipse (detecção automática de Maven):

1. No menu superior, clique em **`File`** → **`Import...`**.

2. Expanda a categoria **`Git`**, selecione **`Projects from Git (with smart import)`** e clique em **`Next >`**.

   > A variante **com *smart import*** reconhece o `pom.xml` e importa como projeto Maven automaticamente. Em versões muito antigas do Eclipse pode aparecer apenas `Projects from Git` — nesse caso, finalize o clone e importe pela **Opção A**.

3. Selecione **`Clone URI`** e clique em **`Next >`**.

4. No campo **`URI`**, cole a URL do repositório:

   ```
   https://github.com/ArthemizLabs/biblio-system.git
   ```

5. **`Next >`** → selecione a branch **`main`** → **`Next >`** → escolha a pasta local de destino → **`Next >`**.

6. O *smart import* detecta o projeto Maven. Clique em **`Finish`**.

7. Se, por usar uma versão antiga do Eclipse, o projeto **não** for reconhecido como Maven, importe-o pela **Opção A**:  
   **`File`** → **`Import`** → **`Maven`** → **`Existing Maven Projects`**, apontando para a pasta clonada.

> **Não use** `Import existing Eclipse projects` para este repositório. Os arquivos de configuração do Eclipse (`.project`, `.classpath`, `.settings`) **não** são versionados (estão no `.gitignore`); a importação correta é sempre como **projeto Maven**.

#### Via terminal (alternativa rápida):

```bash
# 1. Clone o repositório
git clone https://github.com/ArthemizLabs/biblio-system.git

# 2. Acesse a pasta
cd biblio-system

# 3. Compile e baixe as dependências via Maven
mvn clean install -DskipTests

# 4. No Eclipse: File → Import → Maven → Existing Maven Projects (apontando para a pasta clonada)
```

---

## Executando o Sistema

> **Antes de executar, o banco precisa estar criado e em execução.** Siga a seção [Configuração do Banco de Dados](#configuração-do-banco-de-dados) (MySQL local **ou** Docker). Sem o banco no ar, o login falha com erro de conexão.

### Pelo Eclipse

1. Confirme que **não há erros** no projeto (ícones vermelhos no Package Explorer).  
   Havendo erros de build, clique com o botão direito no projeto → **`Maven`** → **`Update Project...`** → marque **`Force Update of Snapshots/Releases`** → **`OK`**.

2. Abra a classe principal:
   ```
   src/main/java/br/pucgoias/biblioteca/App.java
   ```

3. Clique com o botão direito sobre `App.java` → **`Run As`** → **`Java Application`**.

4. A tela de login abrirá. Use as credenciais padrão:
   - **Usuário:** `admin`
   - **Senha:** `admin123`

### Pelo terminal (Maven)

Execute na **raiz do projeto** (onde está o `pom.xml`):

```bash
# Opção 1 — compilar e executar diretamente
mvn compile
mvn exec:java

# Opção 2 — gerar um JAR executável com as dependências e rodar sem IDE
mvn clean package
java -jar target/bibliosystem-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Estrutura do Projeto

```
biblio-system/
├── banco_dados/
│   └── bibliosystem.sql              ← Script MySQL completo com dados de teste
├── src/
│   └── main/
│       ├── java/br/pucgoias/biblioteca/
│       │   ├── controller/           ← Regras de negócio e validações
│       │   ├── dao/                  ← Acesso a dados via JDBC
│       │   │   └── interfaces/       ← Contratos DAO
│       │   ├── model/                ← Entidades (ItemAcervo, Livro, Autor, Leitor...)
│       │   ├── util/                 ← ConexaoBD, Mensagens (i18n), exceptions/
│       │   └── view/                 ← Janelas Swing (JFrame, JInternalFrame)
│       │   │   └── interfaces/       ← IdiomaListener (i18n)
│       │   ├── App.java              ← Classe principal (inicializa o sistema)
│       └── resources/
│           ├── messages_pt.properties  ← Textos em Português
│           └── messages_en.properties  ← Textos em Inglês
├── docker-compose.yml                ← Sobe o banco MySQL via Docker
└── pom.xml                           ← Dependências Maven (MySQL Connector/J)
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

## Indicação por Classe

> Esta seção atende ao **Trabalho da disciplina de Programação Orientada a Objetos (POO)**, indicando em quais classes cada conceito exigido é aplicado.

O BiblioSystem demonstra os **seis conceitos** cobrados na disciplina. O quadro a seguir resume **em quais classes cada conceito é aplicado**:

| Conceito                   | Principais classes / arquivos                                                                                                                                   | Como é aplicado                                                                                                                                                                                                              |
|----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Abstração**              | `ItemAcervo` (abstract); `IGenericoDAO<T, ID>` e demais interfaces `I…DAO`                                                                                      | Classe abstrata que não pode ser instanciada e define métodos abstratos; as interfaces expõem o contrato sem revelar a implementação.                                                                                        |
| **Encapsulamento**         | `Livro`, `Autor`, `Editora`, `Categoria`, `Leitor`, `Usuario`, `Emprestimo`, `Reserva`, `ItemAcervo`                                                            | Atributos `private` acessados somente por *getters/setters*; enums internos (`Perfil`, `Status`) protegem os estados válidos.                                                                                                |
| **Herança**                | `Livro extends ItemAcervo`; `BancoDadosException`/`ValidacaoException extends RuntimeException`; telas `extends JFrame`/`JInternalFrame`                        | `Livro` reaproveita atributos e comportamento de `ItemAcervo`; exceções e telas herdam de classes da plataforma Java/Swing.                                                                                                  |
| **Polimorfismo**           | `toString()` em `Livro`, `Autor`, `Editora`, `Categoria`, `Leitor`, `Usuario`, `ItemAcervo`; `getIdentificador()`/`getDescricaoCompleta()` em `Livro`           | Sobrescrita (`@Override`): o Swing chama `toString()` em tempo de execução para exibir objetos em `JComboBox`/`JTable`; `Livro` reimplementa os métodos abstratos herdados de `ItemAcervo`.                                  |
| **Interface**              | `IGenericoDAO<T, ID>`, `ILivroDAO`, `IAutorDAO`, `IEditoraDAO`, `ICategoriaDAO`, `ILeitorDAO`, `IUsuarioDAO`, `IEmprestimoDAO`, `IReservaDAO`; `IdiomaListener` | Interface genérica define o CRUD; cada interface especializa o contrato e é concretizada por um DAO (`AutorDAO implements IAutorDAO`, etc.). `IdiomaListener` define o contrato de atualização dinâmica de idioma nas Views. |
| **Tratamento de Exceções** | `BancoDadosException`, `ValidacaoException` (uso em DAO, Controller e View)                                                                                     | Exceções customizadas: os DAOs capturam `SQLException` e relançam `BancoDadosException`; os Controllers lançam `ValidacaoException`; as Views tratam ambas com `try/catch`.                                                  |

**Destaque dos conceitos:**
- **Herança** → `Livro` herda de `ItemAcervo`.
- **Polimorfismo** → sobrescrita de `toString()` (chamado pelo Swing) e dos métodos abstratos de `ItemAcervo`.
- **Interface** → família de interfaces `I…DAO` (a partir de `IGenericoDAO`) implementada pelos DAOs.

---

## Internacionalização (i18n)

O sistema suporta **Português (PT-BR)** e **Inglês (EN-US)**. Para trocar o idioma:

1. No menu lateral, acesse **`SISTEMA`** → **`Idioma`**.
2. Selecione o idioma desejado e clique em **`Confirmar`**.
3. Todos os textos da interface são atualizados.

**Implementação técnica:**

- `Mensagens` (em `util/`) gerencia o `ResourceBundle` ativo e mantém uma lista de ouvintes.
- `IdiomaListener` (em `view/interfaces/`) é a interface com o método `onIdiomaChanged()`.
- Todas as janelas (`TelaMenu`, `CadastroLivroFrame`, `EmprestimoFrame`, etc.) implementam `IdiomaListener` e se registram em `Mensagens` ao abrir, desregistrando-se ao fechar.
- Ao chamar `Mensagens.setIdioma(locale)`, todos os ouvintes ativos são notificados e atualizam seus labels, abas, botões e cabeçalhos de tabela no mesmo ciclo de evento.

**Arquivos de tradução** (`src/main/resources/`):

| Arquivo                   | Idioma              |
|---------------------------|---------------------|
| `messages_pt.properties`  | Português (PT-BR)   |
| `messages_en.properties`  | Inglês (EN-US)      |

---

## Credenciais Padrão (dados de teste)

| Usuário       | Senha      | Perfil        |
|---------------|------------|---------------|
| `admin`       | `admin123` | Administrador |
| `funcionario` | `func123`  | Funcionário   |

> Estes dados são inseridos automaticamente pelo script `bibliosystem.sql`.

---

## Solução de Problemas Comuns

| Problema                                           | Solução                                                                                                                                                                                                                                                                                |
|----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Execute `Maven → Update Project` para baixar o connector.                                                                                                                                                                                                                              |
| `Access denied for user 'root'@'localhost'`        | Verifique usuário/senha em `ConexaoBD.java`.                                                                                                                                                                                                                                           |
| `Unknown database 'bibliosystem'`                  | Execute o script `bibliosystem.sql` antes de rodar o sistema.                                                                                                                                                                                                                          |
| Erros vermelhos após importação                    | Botão direito no projeto → `Maven` → `Update Project` → marque `Force Update` → `OK`.                                                                                                                                                                                                  |
| Interface em branco / não abre                     | Confirme que está executando `App.java` como **Java Application**, não como Applet.                                                                                                                                                                                                    |
| Container Docker não sobe (`port already in use`)  | Outro processo usa a porta 3306 (provavelmente MySQL local). Pare o MySQL local ou mude a porta no `docker-compose.yml`: `"3307:3306"` e atualize a URL em `ConexaoBD.java`.                                                                                                           |
| `bibliosystem-db` sobe mas banco não existe        | O script SQL ainda está sendo executado. Aguarde 20–30 segundos e tente novamente.                                                                                                                                                                                                     |
| Caracteres especiais corrompidos (ex: `CiÃªncias`) | O banco foi populado com charset errado. Conecte ao MySQL com `--default-character-set=utf8mb4`, corrija os registros via `UPDATE` ou recadastre-os pelo sistema. A URL em `ConexaoBD.java` já inclui `connectionCollation=utf8mb4_unicode_ci`, que previne o problema em novos dados. |

---

## 📄 Licença

Projeto acadêmico desenvolvido para fins de avaliação.  
© 2026 [Arthur Mamedes Borges](https://github.com/A4thu4) — [ArthemizLabs](https://github.com/ArthemizLabs)

[MIT](LICENSE)
