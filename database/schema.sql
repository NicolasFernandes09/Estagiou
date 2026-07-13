CREATE DATABASE IF NOT EXISTS db_estagiou CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_estagiou;

CREATE TABLE IF NOT EXISTS usuarios (
    ID_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    usuario VARCHAR(20) NOT NULL,
    email VARCHAR(190) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    descricao_profissional TEXT NULL,
    descricao_pessoal TEXT NULL,
    foto LONGTEXT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_usuarios_usuario (usuario),
    UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS empresas (
    ID_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(180) NOT NULL,
    email VARCHAR(190) NULL,
    telefone VARCHAR(40) NULL,
    cidade VARCHAR(120) NULL,
    logo TEXT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vaga (
    id_vaga INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NULL,
    titulo VARCHAR(180) NOT NULL,
    descricao TEXT NOT NULL,
    salario VARCHAR(80) NOT NULL,
    fechamento_vaga VARCHAR(30) NOT NULL,
    tipo_vaga VARCHAR(60) NOT NULL,
    cidade VARCHAR(120) NULL,
    contato VARCHAR(190) NULL,
    numero_vagas INT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vaga_empresa FOREIGN KEY (id_empresa) REFERENCES empresas (ID_empresa) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS api_tokens (
    id_token BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    token_hash CHAR(64) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expira_em DATETIME NOT NULL,
    UNIQUE KEY uk_api_tokens_hash (token_hash),
    KEY idx_api_tokens_usuario (id_usuario),
    KEY idx_api_tokens_expiracao (expira_em),
    CONSTRAINT fk_token_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (ID_usuario) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
