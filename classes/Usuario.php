<?php

class Usuario
{
    private $conn;

    public function __construct($conn)
    {
        $this->conn = $conn;
    }

    public function registrar($nome, $email, $senha, $foto = '')
    {
        $hash = password_hash($senha, PASSWORD_DEFAULT);
        $stmt = $this->conn->prepare(
            'INSERT INTO usuarios (nome, senha, email, foto) VALUES (?, ?, ?, ?)'
        );
        $stmt->bind_param('ssss', $nome, $hash, $email, $foto);
        $stmt->execute();
        return (int) $this->conn->insert_id;
    }

    public function login($email, $senha)
    {
        $stmt = $this->conn->prepare(
            'SELECT ID_usuario, nome, senha, email, foto FROM usuarios WHERE email = ? LIMIT 1'
        );
        $stmt->bind_param('s', $email);
        $stmt->execute();
        $dados = $stmt->get_result()->fetch_assoc();

        if (!$dados) {
            return null;
        }

        $senhaBanco = (string) $dados['senha'];
        if (password_verify($senha, $senhaBanco)) {
            return $dados;
        }

        if (hash_equals($senhaBanco, $senha)) {
            $this->atualizarSenha((int) $dados['ID_usuario'], $senha);
            return $dados;
        }

        return null;
    }

    public function listar()
    {
        $resultado = $this->conn->query(
            'SELECT ID_usuario, nome, email, foto FROM usuarios ORDER BY nome'
        );
        return $resultado->fetch_all(MYSQLI_ASSOC);
    }

    public function buscarPorId($id)
    {
        $stmt = $this->conn->prepare(
            'SELECT ID_usuario, nome, email, foto FROM usuarios WHERE ID_usuario = ? LIMIT 1'
        );
        $stmt->bind_param('i', $id);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
    }

    public function emailExiste($email, $ignorarId = 0)
    {
        $stmt = $this->conn->prepare(
            'SELECT ID_usuario FROM usuarios WHERE email = ? AND ID_usuario <> ? LIMIT 1'
        );
        $stmt->bind_param('si', $email, $ignorarId);
        $stmt->execute();
        return $stmt->get_result()->num_rows > 0;
    }

    public function atualizar($id, $nome, $email, $foto = null, $senha = null)
    {
        if ($foto !== null && $senha !== null && $senha !== '') {
            $hash = password_hash($senha, PASSWORD_DEFAULT);
            $stmt = $this->conn->prepare(
                'UPDATE usuarios SET nome = ?, email = ?, foto = ?, senha = ? WHERE ID_usuario = ?'
            );
            $stmt->bind_param('ssssi', $nome, $email, $foto, $hash, $id);
        } elseif ($foto !== null) {
            $stmt = $this->conn->prepare(
                'UPDATE usuarios SET nome = ?, email = ?, foto = ? WHERE ID_usuario = ?'
            );
            $stmt->bind_param('sssi', $nome, $email, $foto, $id);
        } elseif ($senha !== null && $senha !== '') {
            $hash = password_hash($senha, PASSWORD_DEFAULT);
            $stmt = $this->conn->prepare(
                'UPDATE usuarios SET nome = ?, email = ?, senha = ? WHERE ID_usuario = ?'
            );
            $stmt->bind_param('sssi', $nome, $email, $hash, $id);
        } else {
            $stmt = $this->conn->prepare(
                'UPDATE usuarios SET nome = ?, email = ? WHERE ID_usuario = ?'
            );
            $stmt->bind_param('ssi', $nome, $email, $id);
        }

        return $stmt->execute();
    }

    public function excluir($id)
    {
        $stmt = $this->conn->prepare('DELETE FROM usuarios WHERE ID_usuario = ?');
        $stmt->bind_param('i', $id);
        return $stmt->execute();
    }

    private function atualizarSenha($id, $senha)
    {
        $hash = password_hash($senha, PASSWORD_DEFAULT);
        $stmt = $this->conn->prepare('UPDATE usuarios SET senha = ? WHERE ID_usuario = ?');
        $stmt->bind_param('si', $hash, $id);
        $stmt->execute();
    }
}
