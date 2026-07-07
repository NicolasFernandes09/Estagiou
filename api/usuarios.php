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
        $sql = 'SELECT * FROM usuarios';
        if ($id !== null) {
            $sql .= ' WHERE ID_usuario = ' . (int)$id;
        } elseif ($search !== '') {
            $term = $conn->real_escape_string($search);
            $sql .= " WHERE nome LIKE '%$term%' OR email LIKE '%$term%'";
        }
        $sql .= ' ORDER BY ID_usuario DESC';

        $result = $conn->query($sql);
        $usuarios = [];
        while ($linha = $result->fetch_assoc()) {
            $usuarios[] = $linha;
        }

        sendJson($id !== null && empty($usuarios) ? ['mensagem' => 'Usuário não encontrado'] : $usuarios, $id !== null && empty($usuarios) ? 404 : 200);
        break;

    case 'POST':
        $nome = isset($input['nome']) ? trim($input['nome']) : '';
        $senha = isset($input['senha']) ? trim($input['senha']) : '';
        $email = isset($input['email']) ? trim($input['email']) : '';

        if ($nome === '' || $senha === '' || $email === '') {
            sendJson(['mensagem' => 'Preencha todos os campos obrigatórios.'], 400);
        }

        $sql = "INSERT INTO usuarios (nome, senha, email)
                VALUES ('{$conn->real_escape_string($nome)}', '{$conn->real_escape_string($senha)}', '{$conn->real_escape_string($email)}')";

        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Usuário cadastrado com sucesso.', 'ID_usuario' => $conn->insert_id], 201);
        }

        sendJson(['mensagem' => 'Erro ao cadastrar usuário.'], 500);
        break;

    case 'PUT':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id do usuário para atualizar.'], 400);
        }

        $campos = [];
        if (isset($input['nome'])) {
            $campos[] = "nome = '{$conn->real_escape_string(trim($input['nome']))}'";
        }
        if (isset($input['senha'])) {
            $campos[] = "senha = '{$conn->real_escape_string(trim($input['senha']))}'";
        }
        if (isset($input['email'])) {
            $campos[] = "email = '{$conn->real_escape_string(trim($input['email']))}'";
        }

        if (empty($campos)) {
            sendJson(['mensagem' => 'Nenhum campo foi enviado para atualização.'], 400);
        }

        $sql = "UPDATE usuarios SET " . implode(', ', $campos) . " WHERE ID_usuario = $id";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Usuário atualizado com sucesso.'], 200);
        }

        sendJson(['mensagem' => 'Erro ao atualizar usuário.'], 500);
        break;

    case 'DELETE':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id do usuário para excluir.'], 400);
        }

        $sql = "DELETE FROM usuarios WHERE ID_usuario = $id";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Usuário excluído com sucesso.'], 200);
        }

        sendJson(['mensagem' => 'Erro ao excluir usuário.'], 500);
        break;

    default:
        sendJson(['mensagem' => 'Método não permitido.'], 405);
        break;
}

$conn->close();
