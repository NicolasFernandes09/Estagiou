<?php

class Vaga{

    private $conn;
    private $table_name="vaga";

    public function __construct($banco){
        $this->conn=$banco;
    }

    public function cadastrar($titulo,$descricao,$salario,$fechamento,$tipo){

        $query="INSERT INTO ".$this->table_name."
        (titulo,descricao,salario,fechamento_vaga,tipo_vaga)
        VALUES (?,?,?,?,?)";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param(
            "sssss",
            $titulo,
            $descricao,
            $salario,
            $fechamento,
            $tipo
        );

        $stmt->execute();

        return $stmt;

    }

    public function listar(){

        $query="SELECT * FROM ".$this->table_name;

        $stmt=$this->conn->prepare($query);

        $stmt->execute();

        return $stmt->get_result();

    }

    public function buscarPorId($id){

        $query="SELECT * FROM ".$this->table_name." WHERE id_vaga=?";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param("i",$id);

        $stmt->execute();

        return $stmt->get_result()->fetch_assoc();

    }

    public function listarPorEmpresa($id_empresa){

        $query="SELECT * FROM ".$this->table_name."
        WHERE id_empresa=?";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param("i",$id_empresa);

        $stmt->execute();

        return $stmt->get_result();

    }

    public function atualizar($id,$titulo,$descricao,$salario,$fechamento,$tipo){

        $query="UPDATE ".$this->table_name."
        SET titulo=?,descricao=?,salario=?,fechamento_vaga=?,tipo_vaga=?
        WHERE id_vaga=?";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param(
            "sssssi",
            $titulo,
            $descricao,
            $salario,
            $fechamento,
            $tipo,
            $id
        );

        $stmt->execute();

        return $stmt;

    }

    public function deletar($id){

        $query="DELETE FROM ".$this->table_name."
        WHERE id_vaga=?";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param("i",$id);

        $stmt->execute();

        return $stmt;

    }

}

?>