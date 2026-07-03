<?php

class Empresa {

    private $conn;
    private $table_name = "empresas";

    public function __construct($banco){
        $this->conn = $banco;
    }

    public function cadastrar($nome, $email, $senha, $endereco, $telefone, $logo){

        $query = "INSERT INTO ".$this->table_name."
        (nome,email,senha,endereco,telefone,logo)
        VALUES (?,?,?,?,?,?)";

        $stmt = $this->conn->prepare($query);

        if(!$stmt){
            throw new Exception("Erro ao preparar consulta: ".$this->conn->error);
        }

        $senhaHash = password_hash($senha, PASSWORD_BCRYPT);

        $stmt->bind_param(
            "ssssss",
            $nome,
            $email,
            $senhaHash,
            $endereco,
            $telefone,
            $logo
        );

        $stmt->execute();

        return $stmt;
    }

    public function login($email,$senha){

        $query = "SELECT * FROM ".$this->table_name." WHERE email=?";

        $stmt = $this->conn->prepare($query);

        if(!$stmt){
            throw new Exception("Erro ao preparar consulta: ".$this->conn->error);
        }

        $stmt->bind_param("s",$email);

        $stmt->execute();

        $resultado = $stmt->get_result();

        $empresa = $resultado->fetch_assoc();

        if($empresa && password_verify($senha,$empresa['senha'])){
            return $empresa;
        }

        return false;
    }

    public function listarEmpresas(){

        $query = "SELECT * FROM ".$this->table_name;

        $stmt = $this->conn->prepare($query);

        $stmt->execute();

        return $stmt->get_result();

    }

    public function buscarPorId($id){

        $query = "SELECT * FROM ".$this->table_name." WHERE id_empresa=?";

        $stmt = $this->conn->prepare($query);

        $stmt->bind_param("i",$id);

        $stmt->execute();

        return $stmt->get_result()->fetch_assoc();

    }

    public function atualizar($id,$nome,$email,$endereco,$telefone,$logo){

        $query = "UPDATE ".$this->table_name."
        SET nome=?,email=?,endereco=?,telefone=?,logo=?
        WHERE id_empresa=?";

        $stmt = $this->conn->prepare($query);

        $stmt->bind_param(
            "sssssi",
            $nome,
            $email,
            $endereco,
            $telefone,
            $logo,
            $id
        );

        $stmt->execute();

        return $stmt;

    }

    public function deletar($id){

        $query="DELETE FROM ".$this->table_name." WHERE id_empresa=?";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param("i",$id);

        $stmt->execute();

        return $stmt;

    }

}

?>