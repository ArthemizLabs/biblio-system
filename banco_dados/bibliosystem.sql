-- ============================================================
-- BiblioSystem - Roteiro de Criação do Banco de Dados
-- Banco: bibliosystem
-- Usuário: root
-- Senha: root
-- ============================================================

CREATE DATABASE IF NOT EXISTS bibliosystem
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bibliosystem;

-- ------------------------------------------------------------
-- TABELAS INDEPENDENTES (sem FK)
-- ------------------------------------------------------------

CREATE TABLE autor (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    nacionalidade VARCHAR(60)
);

CREATE TABLE editora (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nome   VARCHAR(100) NOT NULL,
    cidade VARCHAR(60)
);

CREATE TABLE categoria (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(60)  NOT NULL,
    descricao VARCHAR(200)
);

CREATE TABLE leitor (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nome     VARCHAR(100) NOT NULL,
    cpf      CHAR(14)     NOT NULL UNIQUE,
    email    VARCHAR(100),
    telefone VARCHAR(20)
);

CREATE TABLE usuario (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    login  VARCHAR(50)  NOT NULL UNIQUE,
    senha  VARCHAR(100) NOT NULL,
    perfil ENUM('ADMIN','FUNCIONÁRIO') NOT NULL DEFAULT 'FUNCIONÁRIO'
);

-- ------------------------------------------------------------
-- TABELAS COM FK
-- ------------------------------------------------------------

CREATE TABLE livro (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    titulo         VARCHAR(200) NOT NULL,
    isbn           VARCHAR(20)  NOT NULL UNIQUE,
    ano_publicacao INT,
    quantidade     INT          NOT NULL DEFAULT 1,
    id_autor       INT,
    id_editora     INT,
    id_categoria   INT,
    FOREIGN KEY (id_autor)     REFERENCES autor(id),
    FOREIGN KEY (id_editora)   REFERENCES editora(id),
    FOREIGN KEY (id_categoria) REFERENCES categoria(id)
);

CREATE TABLE emprestimo (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    data_emprestimo         DATE        NOT NULL,
    data_devolucao_prevista DATE        NOT NULL,
    data_devolucao_real     DATE,
    status                  ENUM('ATIVO','DEVOLVIDO') NOT NULL DEFAULT 'ATIVO',
    id_leitor               INT NOT NULL,
    id_livro                INT NOT NULL,
    FOREIGN KEY (id_leitor) REFERENCES leitor(id),
    FOREIGN KEY (id_livro)  REFERENCES livro(id)
);

CREATE TABLE reserva (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    data_reserva DATE NOT NULL,
    status       ENUM('ABERTA','CANCELADA','ATENDIDA') NOT NULL DEFAULT 'ABERTA',
    id_leitor    INT NOT NULL,
    id_livro     INT NOT NULL,
    FOREIGN KEY (id_leitor) REFERENCES leitor(id),
    FOREIGN KEY (id_livro)  REFERENCES livro(id)
);

-- ------------------------------------------------------------
-- DADOS DE TESTE
-- ------------------------------------------------------------

INSERT INTO usuario (login, senha, usuario.perfil) VALUES
    ('admin',       'admin123',    'ADMIN'),
    ('funcionário', 'func123',     'FUNCIONÁRIO');

INSERT INTO autor (nome, nacionalidade) VALUES
    ('Machado de Assis',    'Brasileiro'),
    ('Clarice Lispector',   'Brasileira'),
    ('George Orwell',       'Britânico'),
    ('Joshua Bloch',        'Americano');

INSERT INTO editora (nome, cidade) VALUES
    ('Companhia das Letras', 'São Paulo'),
    ('Alta Books',           'Rio de Janeiro'),
    ('Addison-Wesley',       'Boston'),
    ('L&PM Editores',        'Porto Alegre');

INSERT INTO categoria (nome, descricao) VALUES
    ('Literatura Brasileira', 'Obras de autores nacionais'),
    ('Literatura Estrangeira','Obras de autores internacionais'),
    ('Tecnologia',            'Livros técnicos de TI e programação'),
    ('Ciências Humanas',      'Filosofia, sociologia e história');

INSERT INTO livro (titulo, isbn, ano_publicacao, quantidade, id_autor, id_editora, id_categoria) VALUES
    ('Dom Casmurro',              '9788535902778', 1899, 3, 1, 1, 1),
    ('A Hora da Estrela',         '9788530407858', 1977, 2, 2, 1, 1),
    ('1984',                      '9788535914849', 1949, 4, 3, 4, 2),
    ('Effective Java',            '9780134685991', 2018, 2, 4, 3, 3);

INSERT INTO leitor (nome, cpf, email, telefone) VALUES
    ('João Silva',   '123.456.789-00', 'joao@email.com',   '(62) 99999-0001'),
    ('Maria Souza',  '987.654.321-00', 'maria@email.com',  '(62) 99999-0002'),
    ('Pedro Costa',  '456.789.123-00', 'pedro@email.com',  '(62) 99999-0003');

INSERT INTO emprestimo (data_emprestimo, data_devolucao_prevista, status, id_leitor, id_livro) VALUES
    ('2026-05-10', '2026-05-24', 'ATIVO',     1, 1),
    ('2026-05-15', '2026-05-29', 'ATIVO',     2, 3),
    ('2026-04-01', '2026-04-15', 'DEVOLVIDO', 3, 2);

INSERT INTO reserva (data_reserva, status, id_leitor, id_livro) VALUES
    ('2026-05-20', 'ABERTA',    1, 3),
    ('2026-05-22', 'CANCELADA', 2, 4);
