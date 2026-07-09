<?php
class Usuario {
    private $conn;
    private $table_name = "usuarios";

    // O construtor está perfeito, ele exige a conexão ao criar o objeto
    public function __construct($banco) {
        if ($banco === null) {
            throw new Exception("A conexão com o banco de dados não foi fornecida.");
        }
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
        
        // Retorna true se inseriu com sucesso, ou false se falhou
        return $stmt->execute(); 
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

        if ($usuario) {
            $senhaArmazenada = (string) ($usuario['senha'] ?? '');

            if (password_verify($senha, $senhaArmazenada)) {
                return $usuario;
            }

            if ($senha === $senhaArmazenada) {
                $novoHash = password_hash($senha, PASSWORD_BCRYPT);
                $updateStmt = $this->conn->prepare("UPDATE " . $this->table_name . " SET senha = ? WHERE email = ?");

                if ($updateStmt) {
                    $updateStmt->bind_param("ss", $novoHash, $email);
                    $updateStmt->execute();
                }

                return $usuario;
            }
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
        $query = "SELECT * FROM " . $this->table_name . " WHERE id = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("i", $id);
        $stmt->execute();
        $resultado = $stmt->get_result();
        return $resultado->fetch_assoc();
    }

    public function ehAdmin($id) {
        $usuario = $this->lerPorIdUsuario($id);
        return $usuario && ($usuario['nivel'] ?? 'user') === 'admin';
    }

    public function atualizarUsuario($id, $nome, $email, $nivel = null) {
        if ($nivel !== null) {
            $query = "UPDATE " . $this->table_name . " SET nome = ?, email = ?, nivel = ? WHERE id = ?";
            $stmt = $this->conn->prepare($query);

            if (!$stmt) {
                throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
            }

            $stmt->bind_param("sssi", $nome, $email, $nivel, $id);
        } else {
            $query = "UPDATE " . $this->table_name . " SET nome = ?, email = ? WHERE id = ?";
            $stmt = $this->conn->prepare($query);

            if (!$stmt) {
                throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
            }

            $stmt->bind_param("ssi", $nome, $email, $id);
        }

        return $stmt->execute();
    }

    public function deletarUsuario($id) {
        $query = "DELETE FROM " . $this->table_name . " WHERE id = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }
}
?>