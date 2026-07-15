-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 15/07/2026 às 01:03
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `db_estagiou`
--

-- --------------------------------------------------------


--
-- Criação e uso do banco `db_estagiou`
--
CREATE DATABASE if not exists db_estagiou;
USE db_estagiou;

-- --------------------------------------------------------

--
-- Estrutura para tabela `admin`
--

CREATE TABLE `admin` (
  `id_adm` int(11) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `senha` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `admin`
--

INSERT INTO `admin` (`id_adm`, `nome`, `email`, `senha`) VALUES
(1, 'Henrique da Silva de Moura', 'henriquemoura@gmail.com', '$2a$12$ho8z2dk8hM7HigPVCoyjq.qpgtEne2VnpE/Yp8mi2AWLi.w0Rscma');

-- --------------------------------------------------------

--
-- Estrutura para tabela `curtidos`
--

CREATE TABLE `curtidos` (
  `ID_curtidos` int(11) NOT NULL,
  `ID_usuario` int(11) NOT NULL,
  `ID_vaga` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `empresas`
--

CREATE TABLE `empresas` (
  `ID_empresa` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `endereco` varchar(255) NOT NULL,
  `telefone` varchar(20) NOT NULL,
  `logo` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `empresas`
--

INSERT INTO `empresas` (`ID_empresa`, `nome`, `email`, `senha`, `endereco`, `telefone`, `logo`) VALUES
(5, 'Rockstar Games', 'rockstargames@gmail.com', '$2y$10$e.v5gDebomwIhoYVCvSNx.j0fXjCYrUFnw8qaOzCpZp5/WmMw.AEu', 'Rua Flow Games', '(41) 67232-7164', 'img/logo_6a4eea9307a596.81402095.jpg'),
(13, 'Freddy Fazbear\'s Pizza', 'freedy.fazbear@gmail.com', '$2y$10$oKrSAPmm.4MOF99Tc.z1Sup7AlJ4t.HlmzK9LRW2SyQ.2zSVBCBAG', 'Av. João Pereira de Vargas, 2134', '(18) 88329-2327', 'img/logo_6a5010fe1523f7.65040908.jpeg'),
(15, 'Empresa Teste', 'gaby@gmail.com', '$2y$10$XkMc0QnIj3M8eAuOL/qcIOdpfPMgQ5OTbQcG4fCHMyvrTLIssz.EO', 'Rua Porto Xavier, 195', '(51) 9283-8474', 'img/logo_6a503c3aab6613.18660145.jfif'),
(16, 'leon', 'jefersonleonblue@gmail.com', '$2y$10$3gT9JRaslRaHaxjtpP4cveMslIZqhds9MYdyIz1wao5f8.I16ROQ2', 'Rua dos teste', '(51) 99112-8510', 'img/logo_6a503e931e0dd9.62984485.jfif'),
(19, 'Empresa Vitoria', 'vitoria@gmail.com', '$2y$10$N6uibKFYyz1.QhhUfsHLY.vPcY6tZzvZlSArbu8KKCOGhLodOoDKq', 'Rua dos teste,1', '(51) 99330-4409', 'img/logo_6a5573b9daeec3.62199925.jpeg');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuarios`
--

CREATE TABLE `usuarios` (
  `ID_usuario` int(11) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `usuario` varchar(255) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `foto` varchar(255) NOT NULL,
  `descricao_pessoal` text DEFAULT NULL,
  `descricao_profissional` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `usuarios`
--

INSERT INTO `usuarios` (`ID_usuario`, `nome`, `usuario`, `senha`, `email`, `foto`, `descricao_pessoal`, `descricao_profissional`) VALUES
(2, 'Rafael\r\n', '', '1234567', 'rafael@gmail.com', '', 'Sou rafael e tenho glueteos avantajados', 'Rebolador profissional, modelo de bumbum, ex bbb 2025, modelo da playboy');

-- --------------------------------------------------------

--
-- Estrutura para tabela `vaga`
--

CREATE TABLE `vaga` (
  `id_vaga` int(11) NOT NULL,
  `id_empresa` int(11) DEFAULT NULL,
  `titulo` varchar(255) NOT NULL,
  `descricao` varchar(255) NOT NULL,
  `salario` decimal(10,2) NOT NULL,
  `fechamento_vaga` datetime NOT NULL,
  `tipo_vaga` varchar(255) NOT NULL,
  `contato` varchar(255) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `vaga`
--

INSERT INTO `vaga` (`id_vaga`, `id_empresa`, `titulo`, `descricao`, `salario`, `fechamento_vaga`, `tipo_vaga`, `contato`) VALUES
(7, 5, 'Desenvolvedor junior java', 'Desenvolver aplicações em java', 2900.00, '2026-07-24 23:59:00', 'clt', 'rockstargames@gmail.com'),
(12, 13, 'Guarda Noturno Jr', 'O profissional é responsável por inspecionar dependências, zelar pela guarda do patrimônio e evitar a entrada de pessoas estranhas.', 2100.00, '2026-07-23 23:59:00', 'clt', 'freddy.fazbear@gmail.com'),
(14, 5, 'Desenvolvedor Java Júnior', 'Desenvolvimento e manutenção de sistemas utilizando Java, Spring Boot e MySQL.', 3500.00, '2026-08-31 00:00:00', 'CLT', '');

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_adm`),
  ADD UNIQUE KEY `nome` (`nome`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Índices de tabela `curtidos`
--
ALTER TABLE `curtidos`
  ADD PRIMARY KEY (`ID_curtidos`),
  ADD KEY `fk_curtidos_usuario` (`ID_usuario`),
  ADD KEY `fk_curtidos_vaga` (`ID_vaga`);

--
-- Índices de tabela `empresas`
--
ALTER TABLE `empresas`
  ADD PRIMARY KEY (`ID_empresa`),
  ADD UNIQUE KEY `nome` (`nome`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `endereco` (`endereco`),
  ADD UNIQUE KEY `telefone` (`telefone`);

--
-- Índices de tabela `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`ID_usuario`),
  ADD UNIQUE KEY `nome` (`nome`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Índices de tabela `vaga`
--
ALTER TABLE `vaga`
  ADD PRIMARY KEY (`id_vaga`) USING BTREE,
  ADD KEY `fk_vaga_empresa` (`id_empresa`) USING BTREE;

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `admin`
--
ALTER TABLE `admin`
  MODIFY `id_adm` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de tabela `curtidos`
--
ALTER TABLE `curtidos`
  MODIFY `ID_curtidos` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `empresas`
--
ALTER TABLE `empresas`
  MODIFY `ID_empresa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de tabela `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `ID_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `vaga`
--
ALTER TABLE `vaga`
  MODIFY `id_vaga` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `curtidos`
--
ALTER TABLE `curtidos`
  ADD CONSTRAINT `fk_curtidos_usuario` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`ID_usuario`),
  ADD CONSTRAINT `fk_curtidos_vaga` FOREIGN KEY (`ID_vaga`) REFERENCES `vaga` (`id_vaga`);

--
-- Restrições para tabelas `vaga`
--
ALTER TABLE `vaga`
  ADD CONSTRAINT `fk_vaga_empresa` FOREIGN KEY (`id_empresa`) REFERENCES `empresas` (`ID_empresa`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;