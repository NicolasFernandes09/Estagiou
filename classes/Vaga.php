<?php

class Vaga{

    private $conn;
    private $table_name="vaga";

    public function __construct($banco){
        $this->conn=$banco;
    }

    public function cadastrar($titulo,$descricao,$salario,$fechamento,$tipo,$contato,$id_empresa){

        $query="INSERT INTO ".$this->table_name."
        (titulo,descricao,salario,fechamento_vaga,tipo_vaga,contato,id_empresa)
        VALUES (?,?,?,?,?,?,?)";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param(
            "ssssssi",
            $titulo,
            $descricao,
            $salario,
            $fechamento,
            $tipo,
            $contato,
            $id_empresa
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

    public function buscarVagas($busca = '', $tipo = 'todas', $id_empresa = 0){

        $query = "SELECT v.*, COALESCE(e.nome, 'Empresa não informada') AS empresa_nome, e.logo AS empresa_logo
                   FROM ".$this->table_name." v
                   LEFT JOIN empresas e ON e.ID_empresa = v.id_empresa
                   WHERE (? = '' OR v.titulo LIKE CONCAT('%', ?, '%') OR e.nome LIKE CONCAT('%', ?, '%'))
                     AND (? = 'todas' OR LOWER(v.tipo_vaga) = LOWER(?))
                     AND (? = 0 OR v.id_empresa = ?)
                   ORDER BY v.fechamento_vaga DESC";

        $stmt = $this->conn->prepare($query);

        $stmt->bind_param("sssssii", $busca, $busca, $busca, $tipo, $tipo, $id_empresa, $id_empresa);

        $stmt->execute();

        return $stmt->get_result();

    }

    public function listarPorEmpresa($id_empresa){

        $query="SELECT * FROM ".$this->table_name."
        WHERE id_empresa=?";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param("i",$id_empresa);

        $stmt->execute();

        return $stmt->get_result();

    }

    public function atualizar($id,$titulo,$descricao,$salario,$fechamento,$tipo,$contato){

        $query="UPDATE ".$this->table_name."
        SET titulo=?,descricao=?,salario=?,fechamento_vaga=?,tipo_vaga=?,contato=?
        WHERE id_vaga=?";

        $stmt=$this->conn->prepare($query);

        $stmt->bind_param(
            "ssssssi",
            $titulo,
            $descricao,
            $salario,
            $fechamento,
            $tipo,
            $contato,
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