<?php
class Admin {
    private $conn;
    private $table_name = "admin";

    public function __construct($banco) {
        $this->conn = $banco;
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
        $admin = $resultado->fetch_assoc();

        if ($admin && password_verify($senha, $admin['senha'])) {
            return $admin;
        }

        return false;
    }
}
