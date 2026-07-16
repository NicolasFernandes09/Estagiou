CREATE DATABASE IF NOT EXISTS db_estagiou
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE db_estagiou;

CREATE TABLE IF NOT EXISTS admin (
    id_adm INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_adm),
    UNIQUE KEY uk_admin_nome (nome),
    UNIQUE KEY uk_admin_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS usuarios (
    ID_usuario INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    usuario VARCHAR(255) NOT NULL DEFAULT '',
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    foto VARCHAR(255) NOT NULL DEFAULT '',
    descricao_pessoal TEXT NULL,
    descricao_profissional TEXT NULL,
    PRIMARY KEY (ID_usuario),
    UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS empresas (
    ID_empresa INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    logo VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID_empresa),
    UNIQUE KEY uk_empresas_nome (nome),
    UNIQUE KEY uk_empresas_email (email),
    UNIQUE KEY uk_empresas_endereco (endereco),
    UNIQUE KEY uk_empresas_telefone (telefone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS vaga (
    id_vaga INT NOT NULL AUTO_INCREMENT,
    id_empresa INT DEFAULT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    salario DECIMAL(10,2) NOT NULL,
    fechamento_vaga DATETIME NOT NULL,
    tipo_vaga VARCHAR(255) NOT NULL,
    contato VARCHAR(255) NOT NULL DEFAULT '',
    PRIMARY KEY (id_vaga),
    KEY idx_vaga_empresa (id_empresa),
    CONSTRAINT fk_vaga_empresa FOREIGN KEY (id_empresa)
        REFERENCES empresas (ID_empresa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS curtidos (
    ID_curtidos INT NOT NULL AUTO_INCREMENT,
    ID_usuario INT NOT NULL,
    ID_vaga INT NOT NULL,
    PRIMARY KEY (ID_curtidos),
    UNIQUE KEY uk_curtidos_usuario_vaga (ID_usuario, ID_vaga),
    KEY idx_curtidos_vaga (ID_vaga),
    CONSTRAINT fk_curtidos_usuario FOREIGN KEY (ID_usuario)
        REFERENCES usuarios (ID_usuario),
    CONSTRAINT fk_curtidos_vaga FOREIGN KEY (ID_vaga)
        REFERENCES vaga (id_vaga)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
