<?php
class Usuario {
    private $conn;
    private $table_name = "usuarios";

    public function __construct($banco) {
        $this->conn = $banco;
    }

    public function registrar($nome, $email, $senha) {
        $query = "INSERT INTO " . $this->table_name . " (nome, email, senha) VALUES (?, ?, ?)";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $hashed_password = password_hash($senha, PASSWORD_BCRYPT);
        $stmt->bind_param("sss", $nome, $email, $hashed_password);
        $stmt->execute();

        return $stmt;
    }

    public function login($email, $senha) {
        $query = "SELECT * FROM " . $this->table_name . " WHERE email = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("s", $email);
        $stmt->execute();

        $resultado = $stmt->get_result();
        $usuario = $resultado->fetch_assoc();

        if ($usuario && password_verify($senha, $usuario['senha'])) {
            return $usuario;
        }

        return false;
    }

    public function lerUsuarios() {
        $query = "SELECT * FROM " . $this->table_name;
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->execute();
        return $stmt->get_result();
    }

    public function lerPorIdUsuario($id) {
        $query = "SELECT ID_usuario AS id_usuario, nome, email FROM " . $this->table_name . " WHERE id_usuario = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("i", $id);
        $stmt->execute();
        $resultado = $stmt->get_result();
        return $resultado->fetch_assoc();
    }

    public function atualizarUsuario($id, $nome, $email) {
        $query = "UPDATE " . $this->table_name . " SET nome = ?, email = ? WHERE id_usuario = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("ssi", $nome, $email, $id);
        $stmt->execute();
        return $stmt;
    }

    public function deletarUsuario($id) {
        $query = "DELETE FROM " . $this->table_name . " WHERE id_usuario = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("i", $id);
        $stmt->execute();
        return $stmt;
    }
}
?>