<?php

class Usuario
{
    private $conn;

    public function __construct($conn)
    {
        $this->conn = $conn;
    }

    public function registrar($nome, $usuario, $email, $senha, $descricaoPessoal = '', $descricaoProfissional = '', $foto = '')
    {
        $hash = password_hash($senha, PASSWORD_DEFAULT);
        $stmt = $this->conn->prepare(
            'INSERT INTO usuarios (nome, usuario, senha, email, foto, descricao_pessoal, descricao_profissional)
             VALUES (?, ?, ?, ?, ?, ?, ?)'
        );
        $stmt->bind_param(
            'sssssss',
            $nome,
            $usuario,
            $hash,
            $email,
            $foto,
            $descricaoPessoal,
            $descricaoProfissional
        );
        $stmt->execute();
        return (int) $this->conn->insert_id;
    }

    public function login($usuario, $email, $senha)
    {
        $stmt = $this->conn->prepare(
            "SELECT ID_usuario, nome, usuario, senha, email, foto, descricao_pessoal, descricao_profissional
             FROM usuarios
             WHERE email = ? AND (usuario = ? OR usuario = '')
             LIMIT 1"
        );
        $stmt->bind_param('ss', $email, $usuario);
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
            'SELECT ID_usuario, nome, usuario, email, foto, descricao_pessoal, descricao_profissional
             FROM usuarios ORDER BY nome'
        );
        return $resultado->fetch_all(MYSQLI_ASSOC);
    }

    public function buscarPorId($id)
    {
        $stmt = $this->conn->prepare(
            'SELECT ID_usuario, nome, usuario, email, foto, descricao_pessoal, descricao_profissional
             FROM usuarios WHERE ID_usuario = ? LIMIT 1'
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

    public function usuarioExiste($usuario, $ignorarId = 0)
    {
        $stmt = $this->conn->prepare(
            "SELECT ID_usuario FROM usuarios
             WHERE usuario = ? AND usuario <> '' AND ID_usuario <> ? LIMIT 1"
        );
        $stmt->bind_param('si', $usuario, $ignorarId);
        $stmt->execute();
        return $stmt->get_result()->num_rows > 0;
    }

    public function atualizar($id, $nome, $usuario, $email, $descricaoPessoal, $descricaoProfissional, $foto, $senha = '')
    {
        if ($senha !== '') {
            $hash = password_hash($senha, PASSWORD_DEFAULT);
            $stmt = $this->conn->prepare(
                'UPDATE usuarios
                 SET nome = ?, usuario = ?, email = ?, descricao_pessoal = ?, descricao_profissional = ?, foto = ?, senha = ?
                 WHERE ID_usuario = ?'
            );
            $stmt->bind_param(
                'sssssssi',
                $nome,
                $usuario,
                $email,
                $descricaoPessoal,
                $descricaoProfissional,
                $foto,
                $hash,
                $id
            );
        } else {
            $stmt = $this->conn->prepare(
                'UPDATE usuarios
                 SET nome = ?, usuario = ?, email = ?, descricao_pessoal = ?, descricao_profissional = ?, foto = ?
                 WHERE ID_usuario = ?'
            );
            $stmt->bind_param(
                'ssssssi',
                $nome,
                $usuario,
                $email,
                $descricaoPessoal,
                $descricaoProfissional,
                $foto,
                $id
            );
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
