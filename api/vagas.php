<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/conexao.php';

function sendJson($data, $status = 200) {
    http_response_code($status);
    echo json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit;
}

$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents('php://input'), true);
if (!is_array($input)) {
    $input = [];
}
if (empty($input)) {
    $input = $_POST;
}

$id = isset($_GET['id']) ? (int)$_GET['id'] : (isset($input['id']) ? (int)$input['id'] : null);
$search = isset($_GET['q']) ? trim($_GET['q']) : (isset($_GET['search']) ? trim($_GET['search']) : '');

switch ($method) {
    case 'GET':
        $sql = "SELECT v.id_vaga, v.id_empresa, v.titulo, v.descricao, v.salario, v.fechamento_vaga, v.tipo_vaga, e.nome AS empresa, e.email AS empresa_email, e.telefone, e.logo
                FROM vaga v
                LEFT JOIN empresas e ON e.ID_empresa = v.id_empresa";

        if ($id !== null) {
            $sql .= " WHERE v.id_vaga = " . (int)$id;
        } elseif ($search !== '') {
            $term = $conn->real_escape_string($search);
            $sql .= " WHERE v.titulo LIKE '%$term%' OR v.descricao LIKE '%$term%' OR v.tipo_vaga LIKE '%$term%' OR e.nome LIKE '%$term%'";
        }

        $sql .= " ORDER BY v.id_vaga DESC";
        $result = $conn->query($sql);

        $vagas = [];
        while ($linha = $result->fetch_assoc()) {
            $vagas[] = $linha;
        }

        sendJson($id !== null && empty($vagas) ? ['mensagem' => 'Vaga não encontrada'] : $vagas, $id !== null && empty($vagas) ? 404 : 200);
        break;

    case 'POST':
        $idEmpresa = isset($input['id_empresa']) ? (int)$input['id_empresa'] : null;
        $titulo = isset($input['titulo']) ? trim($input['titulo']) : '';
        $descricao = isset($input['descricao']) ? trim($input['descricao']) : '';
        $salario = isset($input['salario']) ? trim($input['salario']) : '';
        $fechamento = isset($input['fechamento_vaga']) ? trim($input['fechamento_vaga']) : '';
        $tipo = isset($input['tipo_vaga']) ? trim($input['tipo_vaga']) : '';

        if ($idEmpresa === null || $titulo === '' || $descricao === '' || $salario === '' || $fechamento === '' || $tipo === '') {
            sendJson(['mensagem' => 'Preencha todos os campos obrigatórios.'], 400);
        }

        $sql = "INSERT INTO vaga (id_empresa, titulo, descricao, salario, fechamento_vaga, tipo_vaga)
                VALUES ($idEmpresa, '{$conn->real_escape_string($titulo)}', '{$conn->real_escape_string($descricao)}', '{$conn->real_escape_string($salario)}', '{$conn->real_escape_string($fechamento)}', '{$conn->real_escape_string($tipo)}')";

        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Vaga cadastrada com sucesso.', 'id_vaga' => $conn->insert_id], 201);
        }

        sendJson(['mensagem' => 'Erro ao cadastrar vaga.'], 500);
        break;

    case 'PUT':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id da vaga para atualizar.'], 400);
        }

        $campos = [];
        if (isset($input['id_empresa'])) {
            $campos[] = "id_empresa = " . (int)$input['id_empresa'];
        }
        if (isset($input['titulo'])) {
            $campos[] = "titulo = '{$conn->real_escape_string(trim($input['titulo']))}'";
        }
        if (isset($input['descricao'])) {
            $campos[] = "descricao = '{$conn->real_escape_string(trim($input['descricao']))}'";
        }
        if (isset($input['salario'])) {
            $campos[] = "salario = '{$conn->real_escape_string(trim($input['salario']))}'";
        }
        if (isset($input['fechamento_vaga'])) {
            $campos[] = "fechamento_vaga = '{$conn->real_escape_string(trim($input['fechamento_vaga']))}'";
        }
        if (isset($input['tipo_vaga'])) {
            $campos[] = "tipo_vaga = '{$conn->real_escape_string(trim($input['tipo_vaga']))}'";
        }

        if (empty($campos)) {
            sendJson(['mensagem' => 'Nenhum campo foi enviado para atualização.'], 400);
        }

        $sql = "UPDATE vaga SET " . implode(', ', $campos) . " WHERE id_vaga = $id";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Vaga atualizada com sucesso.'], 200);
        }

        sendJson(['mensagem' => 'Erro ao atualizar vaga.'], 500);
        break;

    case 'DELETE':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id da vaga para excluir.'], 400);
        }

        $sql = "DELETE FROM vaga WHERE id_vaga = $id";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Vaga excluída com sucesso.'], 200);
        }

        sendJson(['mensagem' => 'Erro ao excluir vaga.'], 500);
        break;

    default:
        sendJson(['mensagem' => 'Método não permitido.'], 405);
        break;
}

$conn->close();
