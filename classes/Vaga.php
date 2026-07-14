<?php

class Vaga
{
    private $conn;

    public function __construct($conn)
    {
        $this->conn = $conn;
    }

    public function listar($busca = '')
    {
        $sql = $this->consultaBase();
        if ($busca === '') {
            $resultado = $this->conn->query($sql . ' ORDER BY v.id_vaga DESC');
            return $resultado->fetch_all(MYSQLI_ASSOC);
        }

        $termo = '%' . $busca . '%';
        $stmt = $this->conn->prepare(
            $sql . ' WHERE v.titulo LIKE ? OR v.descricao LIKE ? OR v.tipo_vaga LIKE ? OR e.nome LIKE ? ORDER BY v.id_vaga DESC'
        );
        $stmt->bind_param('ssss', $termo, $termo, $termo, $termo);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    public function buscarPorId($id)
    {
        $stmt = $this->conn->prepare($this->consultaBase() . ' WHERE v.id_vaga = ? LIMIT 1');
        $stmt->bind_param('i', $id);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
    }

    public function cadastrar($idEmpresa, $titulo, $descricao, $salario, $fechamento, $tipo, $contato)
    {
        $stmt = $this->conn->prepare(
            'INSERT INTO vaga (id_empresa, titulo, descricao, salario, fechamento_vaga, tipo_vaga, contato) VALUES (?, ?, ?, ?, ?, ?, ?)'
        );
        $stmt->bind_param('issssss', $idEmpresa, $titulo, $descricao, $salario, $fechamento, $tipo, $contato);
        $stmt->execute();
        return (int) $this->conn->insert_id;
    }

    public function atualizar($id, $idEmpresa, $titulo, $descricao, $salario, $fechamento, $tipo, $contato)
    {
        $stmt = $this->conn->prepare(
            'UPDATE vaga SET id_empresa = ?, titulo = ?, descricao = ?, salario = ?, fechamento_vaga = ?, tipo_vaga = ?, contato = ? WHERE id_vaga = ?'
        );
        $stmt->bind_param('issssssi', $idEmpresa, $titulo, $descricao, $salario, $fechamento, $tipo, $contato, $id);
        return $stmt->execute();
    }

    public function excluir($id)
    {
        $stmt = $this->conn->prepare('DELETE FROM vaga WHERE id_vaga = ?');
        $stmt->bind_param('i', $id);
        return $stmt->execute();
    }

    private function consultaBase()
    {
        return "SELECT v.id_vaga, v.id_empresa, v.titulo, v.descricao, v.salario,
                       v.fechamento_vaga, v.tipo_vaga, v.contato,
                       e.nome AS empresa, e.telefone, e.logo,
                       e.endereco AS cidade
                FROM vaga v
                LEFT JOIN empresas e ON e.ID_empresa = v.id_empresa";
    }
}
