<?php

class Usuario
{
    private $conn;

    public function __construct($conn)
    {
        $this->conn = $conn;
        $this->garantirEstrutura();
    }

    private function garantirEstrutura()
    {
        $this->conn->query("CREATE TABLE IF NOT EXISTS usuarios (
            ID_usuario INT AUTO_INCREMENT PRIMARY KEY,
            nome VARCHAR(150) NOT NULL,
            usuario VARCHAR(20) NOT NULL,
            email VARCHAR(190) NOT NULL,
            senha VARCHAR(255) NOT NULL,
            descricao_profissional TEXT NULL,
            descricao_pessoal TEXT NULL,
            foto LONGTEXT NULL,
            criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

        $this->garantirColuna('usuario', "VARCHAR(20) NOT NULL DEFAULT ''");
        $this->garantirColuna('descricao_profissional', 'TEXT NULL');
        $this->garantirColuna('descricao_pessoal', 'TEXT NULL');
        $this->garantirColuna('foto', 'LONGTEXT NULL');
    }

    private function garantirColuna($coluna, $definicao)
    {
        $nome = $this->conn->real_escape_string($coluna);
        $resultado = $this->conn->query("SHOW COLUMNS FROM usuarios LIKE '$nome'");
        if ($resultado && $resultado->num_rows === 0) {
            $this->conn->query("ALTER TABLE usuarios ADD COLUMN $coluna $definicao");
        }
    }

    public function emailExiste($email, $ignorarId = 0)
    {
        $stmt = $this->conn->prepare('SELECT ID_usuario FROM usuarios WHERE email = ? AND ID_usuario <> ? LIMIT 1');
        $stmt->bind_param('si', $email, $ignorarId);
        $stmt->execute();
        return $stmt->get_result()->num_rows > 0;
    }

    public function usuarioExiste($usuario, $ignorarId = 0)
    {
        $stmt = $this->conn->prepare('SELECT ID_usuario FROM usuarios WHERE usuario = ? AND ID_usuario <> ? LIMIT 1');
        $stmt->bind_param('si', $usuario, $ignorarId);
        $stmt->execute();
        return $stmt->get_result()->num_rows > 0;
    }

    public function registrar($nome, $usuario, $email, $senha, $descricaoProfissional, $descricaoPessoal, $foto)
    {
        $hash = password_hash($senha, PASSWORD_DEFAULT);
        $stmt = $this->conn->prepare('INSERT INTO usuarios (nome, usuario, email, senha, descricao_profissional, descricao_pessoal, foto) VALUES (?, ?, ?, ?, ?, ?, ?)');
        $stmt->bind_param('sssssss', $nome, $usuario, $email, $hash, $descricaoProfissional, $descricaoPessoal, $foto);
        $stmt->execute();
        return (int) $this->conn->insert_id;
    }

    public function login($usuario, $email, $senha)
    {
        $stmt = $this->conn->prepare('SELECT * FROM usuarios WHERE email = ? OR usuario = ? LIMIT 1');
        $stmt->bind_param('ss', $email, $usuario);
        $stmt->execute();
        $dados = $stmt->get_result()->fetch_assoc();
        if (!$dados) {
            return null;
        }

        $senhaBanco = (string) ($dados['senha'] ?? '');
        if (password_verify($senha, $senhaBanco)) {
            return $dados;
        }

        if (hash_equals($senhaBanco, $senha)) {
            $novoHash = password_hash($senha, PASSWORD_DEFAULT);
            $id = (int) $dados['ID_usuario'];
            $update = $this->conn->prepare('UPDATE usuarios SET senha = ? WHERE ID_usuario = ?');
            $update->bind_param('si', $novoHash, $id);
            $update->execute();
            return $dados;
        }

        return null;
    }

    public function buscarPorId($id)
    {
        $stmt = $this->conn->prepare('SELECT * FROM usuarios WHERE ID_usuario = ? LIMIT 1');
        $stmt->bind_param('i', $id);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
    }

    public function atualizar($id, $nome, $usuario, $email, $descricaoProfissional, $descricaoPessoal, $foto)
    {
        $stmt = $this->conn->prepare('UPDATE usuarios SET nome = ?, usuario = ?, email = ?, descricao_profissional = ?, descricao_pessoal = ?, foto = ? WHERE ID_usuario = ?');
        $stmt->bind_param('ssssssi', $nome, $usuario, $email, $descricaoProfissional, $descricaoPessoal, $foto, $id);
        return $stmt->execute();
    }
}
