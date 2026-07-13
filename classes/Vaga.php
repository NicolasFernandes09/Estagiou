<?php

class Vaga
{
    private $conn;

    public function __construct($conn)
    {
        $this->conn = $conn;
        $this->garantirEstrutura();
    }

    private function garantirEstrutura()
    {
        $this->conn->query("CREATE TABLE IF NOT EXISTS empresas (
            ID_empresa INT AUTO_INCREMENT PRIMARY KEY,
            nome VARCHAR(180) NOT NULL,
            email VARCHAR(190) NULL,
            telefone VARCHAR(40) NULL,
            cidade VARCHAR(120) NULL,
            logo TEXT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

        $this->conn->query("CREATE TABLE IF NOT EXISTS vaga (
            id_vaga INT AUTO_INCREMENT PRIMARY KEY,
            id_empresa INT NULL,
            titulo VARCHAR(180) NOT NULL,
            descricao TEXT NOT NULL,
            salario VARCHAR(80) NOT NULL,
            fechamento_vaga VARCHAR(30) NOT NULL,
            tipo_vaga VARCHAR(60) NOT NULL,
            cidade VARCHAR(120) NULL,
            contato VARCHAR(190) NULL,
            numero_vagas INT NULL,
            criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

        $this->garantirColuna('vaga', 'cidade', 'VARCHAR(120) NULL');
        $this->garantirColuna('vaga', 'contato', 'VARCHAR(190) NULL');
        $this->garantirColuna('vaga', 'numero_vagas', 'INT NULL');
        $this->garantirColuna('empresas', 'email', 'VARCHAR(190) NULL');
        $this->garantirColuna('empresas', 'telefone', 'VARCHAR(40) NULL');
        $this->garantirColuna('empresas', 'cidade', 'VARCHAR(120) NULL');
        $this->garantirColuna('empresas', 'logo', 'TEXT NULL');
    }

    private function garantirColuna($tabela, $coluna, $definicao)
    {
        $nome = $this->conn->real_escape_string($coluna);
        $resultado = $this->conn->query("SHOW COLUMNS FROM $tabela LIKE '$nome'");
        if ($resultado && $resultado->num_rows === 0) {
            $this->conn->query("ALTER TABLE $tabela ADD COLUMN $coluna $definicao");
        }
    }

    public function listar($id = null, $busca = '')
    {
        $sql = "SELECT v.id_vaga, v.id_empresa, v.titulo, v.descricao, v.salario,
                       v.fechamento_vaga, v.tipo_vaga,
                       COALESCE(NULLIF(e.nome, ''), 'Empresa') AS empresa,
                       COALESCE(NULLIF(v.cidade, ''), e.cidade, 'Cidade não informada') AS cidade,
                       COALESCE(NULLIF(v.contato, ''), e.email, '') AS contato,
                       COALESCE(e.telefone, '') AS telefone,
                       COALESCE(e.logo, '') AS logo,
                       v.numero_vagas
                FROM vaga v
                LEFT JOIN empresas e ON e.ID_empresa = v.id_empresa";

        if ($id !== null) {
            $sql .= ' WHERE v.id_vaga = ?';
            $stmt = $this->conn->prepare($sql);
            $stmt->bind_param('i', $id);
        } elseif ($busca !== '') {
            $sql .= " WHERE v.titulo LIKE CONCAT('%', ?, '%')
                      OR v.descricao LIKE CONCAT('%', ?, '%')
                      OR v.tipo_vaga LIKE CONCAT('%', ?, '%')
                      OR e.nome LIKE CONCAT('%', ?, '%')";
            $stmt = $this->conn->prepare($sql . ' ORDER BY v.id_vaga DESC');
            $stmt->bind_param('ssss', $busca, $busca, $busca, $busca);
            $stmt->execute();
            return $stmt->get_result();
        } else {
            $stmt = $this->conn->prepare($sql . ' ORDER BY v.id_vaga DESC');
        }

        $stmt->execute();
        return $stmt->get_result();
    }

    public function inserir($dados)
    {
        $stmt = $this->conn->prepare('INSERT INTO vaga (id_empresa, titulo, descricao, salario, fechamento_vaga, tipo_vaga, cidade, contato, numero_vagas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)');
        $stmt->bind_param(
            'isssssssi',
            $dados['id_empresa'],
            $dados['titulo'],
            $dados['descricao'],
            $dados['salario'],
            $dados['fechamento_vaga'],
            $dados['tipo_vaga'],
            $dados['cidade'],
            $dados['contato'],
            $dados['numero_vagas']
        );
        $stmt->execute();
        return (int) $this->conn->insert_id;
    }

    public function atualizar($id, $dados)
    {
        $campos = [];
        $tipos = '';
        $valores = [];
        $permitidos = [
            'id_empresa' => 'i',
            'titulo' => 's',
            'descricao' => 's',
            'salario' => 's',
            'fechamento_vaga' => 's',
            'tipo_vaga' => 's',
            'cidade' => 's',
            'contato' => 's',
            'numero_vagas' => 'i'
        ];

        foreach ($permitidos as $campo => $tipo) {
            if (array_key_exists($campo, $dados)) {
                $campos[] = "$campo = ?";
                $tipos .= $tipo;
                $valores[] = $dados[$campo];
            }
        }

        if (empty($campos)) {
            return false;
        }

        $tipos .= 'i';
        $valores[] = $id;
        $stmt = $this->conn->prepare('UPDATE vaga SET ' . implode(', ', $campos) . ' WHERE id_vaga = ?');
        $parametros = [$tipos];
        foreach ($valores as $indice => $valor) {
            $parametros[] = &$valores[$indice];
        }
        call_user_func_array([$stmt, 'bind_param'], $parametros);
        return $stmt->execute();
    }

    public function excluir($id)
    {
        $stmt = $this->conn->prepare('DELETE FROM vaga WHERE id_vaga = ?');
        $stmt->bind_param('i', $id);
        return $stmt->execute();
    }
}
