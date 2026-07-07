<?php
header('Content-Type: application/json; charset=utf-8');
include 'conexao.php';

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
        $sql = "SELECT c.ID_curtidos, c.ID_usuario, c.ID_vaga, u.nome AS usuario, v.titulo AS vaga
                FROM curtidos c
                LEFT JOIN usuarios u ON u.ID_usuario = c.ID_usuario
                LEFT JOIN vaga v ON v.id_vaga = c.ID_vaga";

        if ($id !== null) {
            $sql .= ' WHERE c.ID_curtidos = ' . (int)$id;
        } elseif ($search !== '') {
            $term = $conn->real_escape_string($search);
            $sql .= " WHERE u.nome LIKE '%$term%' OR v.titulo LIKE '%$term%'";
        }
        $sql .= ' ORDER BY c.ID_curtidos DESC';

        $result = $conn->query($sql);
        $curtidos = [];
        while ($linha = $result->fetch_assoc()) {
            $curtidos[] = $linha;
        }

        sendJson($id !== null && empty($curtidos) ? ['mensagem' => 'Curtida não encontrada'] : $curtidos, $id !== null && empty($curtidos) ? 404 : 200);
        break;

    case 'POST':
        $idUsuario = isset($input['ID_usuario']) ? (int)$input['ID_usuario'] : (isset($input['id_usuario']) ? (int)$input['id_usuario'] : null);
        $idVaga = isset($input['ID_vaga']) ? (int)$input['ID_vaga'] : (isset($input['id_vaga']) ? (int)$input['id_vaga'] : null);

        if ($idUsuario === null || $idVaga === null) {
            sendJson(['mensagem' => 'Informe ID_usuario e ID_vaga.'], 400);
        }

        $check = $conn->query("SELECT ID_curtidos FROM curtidos WHERE ID_usuario = $idUsuario AND ID_vaga = $idVaga");
        if ($check->num_rows > 0) {
            sendJson(['mensagem' => 'Essa curtida já existe.'], 409);
        }

        $sql = "INSERT INTO curtidos (ID_usuario, ID_vaga) VALUES ($idUsuario, $idVaga)";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Curtida registrada com sucesso.', 'ID_curtidos' => $conn->insert_id], 201);
        }

        sendJson(['mensagem' => 'Erro ao registrar curtida.'], 500);
        break;

    case 'PUT':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id da curtida para atualizar.'], 400);
        }

        $campos = [];
        if (isset($input['ID_usuario']) || isset($input['id_usuario'])) {
            $campos[] = "ID_usuario = " . (int)(isset($input['ID_usuario']) ? $input['ID_usuario'] : $input['id_usuario']);
        }
        if (isset($input['ID_vaga']) || isset($input['id_vaga'])) {
            $campos[] = "ID_vaga = " . (int)(isset($input['ID_vaga']) ? $input['ID_vaga'] : $input['id_vaga']);
        }

        if (empty($campos)) {
            sendJson(['mensagem' => 'Nenhum campo foi enviado para atualização.'], 400);
        }

        $sql = "UPDATE curtidos SET " . implode(', ', $campos) . " WHERE ID_curtidos = $id";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Curtida atualizada com sucesso.'], 200);
        }

        sendJson(['mensagem' => 'Erro ao atualizar curtida.'], 500);
        break;

    case 'DELETE':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id da curtida para excluir.'], 400);
        }

        $sql = "DELETE FROM curtidos WHERE ID_curtidos = $id";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Curtida excluída com sucesso.'], 200);
        }

        sendJson(['mensagem' => 'Erro ao excluir curtida.'], 500);
        break;

    default:
        sendJson(['mensagem' => 'Método não permitido.'], 405);
        break;
}

$conn->close();
