<?php
class Usuario
{
    private $conn;
    private $table_name = "usuarios";

    // O construtor está perfeito, ele exige a conexão ao criar o objeto
    public function __construct($banco)
    {
        if ($banco === null) {
            throw new Exception("A conexão com o banco de dados não foi fornecida.");
        }
        $this->conn = $banco;
    }

    public function registrar($nome, $usuario, $email, $senha, $descricaoPessoal, $descricaoProfissional, $foto)
    {
        $query = "INSERT INTO " . $this->table_name . " (nome, usuario, email, senha, descricao_pessoal, descricao_profissional, foto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $hashed_password = password_hash($senha, PASSWORD_BCRYPT);
        $stmt->bind_param("ssssss", $nome, $usuario, $email, $hashed_password, $descricaoPessoal, $descricaoProfissional, $foto);

        // Retorna true se inseriu com sucesso, ou false se falhou
        return $stmt->execute();
    }

    public function login($usuario, $email, $senha)
    {
        $query = "SELECT * FROM " . $this->table_name . " WHERE usuario = ? OR email = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("ss", $usuario, $email);
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

    public function lerUsuarios()
    {
        $query = "SELECT * FROM " . $this->table_name;
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->execute();
        return $stmt->get_result();
    }

    public function lerPorIdUsuario($id)
    {
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

    public function atualizarUsuario($id, $nome, $email, $foto, $senha)
    {
        $query = "UPDATE " . $this->table_name . " SET nome = ?, email = ?, foto = ?, senha = ? WHERE id_usuario = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("ssi", $nome, $email, $foto, $senha, $id);
        $stmt->execute();
        return $stmt;
    }

    public function deletarUsuario($id)
    {
        $query = "DELETE FROM " . $this->table_name . " WHERE id_usuario = ?";
        $stmt = $this->conn->prepare($query);

        if (!$stmt) {
            throw new Exception("Erro ao preparar consulta: " . $this->conn->error);
        }

        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }
}
?>