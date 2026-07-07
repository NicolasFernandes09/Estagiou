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
        $sql = 'SELECT * FROM empresas';
        if ($id !== null) {
            $sql .= ' WHERE ID_empresa = ' . (int)$id;
        } elseif ($search !== '') {
            $term = $conn->real_escape_string($search);
            $sql .= " WHERE nome LIKE '%$term%' OR email LIKE '%$term%' OR endereco LIKE '%$term%' OR telefone LIKE '%$term%'";
        }
        $sql .= ' ORDER BY ID_empresa DESC';

        $result = $conn->query($sql);
        $empresas = [];
        while ($linha = $result->fetch_assoc()) {
            $empresas[] = $linha;
        }

        sendJson($id !== null && empty($empresas) ? ['mensagem' => 'Empresa não encontrada'] : $empresas, $id !== null && empty($empresas) ? 404 : 200);
        break;

    case 'POST':
        $nome = isset($input['nome']) ? trim($input['nome']) : '';
        $email = isset($input['email']) ? trim($input['email']) : '';
        $senha = isset($input['senha']) ? trim($input['senha']) : '';
        $endereco = isset($input['endereco']) ? trim($input['endereco']) : '';
        $telefone = isset($input['telefone']) ? trim($input['telefone']) : '';
        $logo = isset($input['logo']) ? trim($input['logo']) : '';

        if ($nome === '' || $email === '' || $senha === '' || $endereco === '' || $telefone === '') {
            sendJson(['mensagem' => 'Preencha todos os campos obrigatórios.'], 400);
        }

        $sql = "INSERT INTO empresas (nome, email, senha, endereco, telefone, logo)
                VALUES ('{$conn->real_escape_string($nome)}', '{$conn->real_escape_string($email)}', '{$conn->real_escape_string($senha)}', '{$conn->real_escape_string($endereco)}', '{$conn->real_escape_string($telefone)}', '{$conn->real_escape_string($logo)}')";

        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Empresa cadastrada com sucesso.', 'ID_empresa' => $conn->insert_id], 201);
        }

        sendJson(['mensagem' => 'Erro ao cadastrar empresa.'], 500);
        break;

    case 'PUT':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id da empresa para atualizar.'], 400);
        }

        $campos = [];
        if (isset($input['nome'])) {
            $campos[] = "nome = '{$conn->real_escape_string(trim($input['nome']))}'";
        }
        if (isset($input['email'])) {
            $campos[] = "email = '{$conn->real_escape_string(trim($input['email']))}'";
        }
        if (isset($input['senha'])) {
            $campos[] = "senha = '{$conn->real_escape_string(trim($input['senha']))}'";
        }
        if (isset($input['endereco'])) {
            $campos[] = "endereco = '{$conn->real_escape_string(trim($input['endereco']))}'";
        }
        if (isset($input['telefone'])) {
            $campos[] = "telefone = '{$conn->real_escape_string(trim($input['telefone']))}'";
        }
        if (isset($input['logo'])) {
            $campos[] = "logo = '{$conn->real_escape_string(trim($input['logo']))}'";
        }

        if (empty($campos)) {
            sendJson(['mensagem' => 'Nenhum campo foi enviado para atualização.'], 400);
        }

        $sql = "UPDATE empresas SET " . implode(', ', $campos) . " WHERE ID_empresa = $id";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Empresa atualizada com sucesso.'], 200);
        }

        sendJson(['mensagem' => 'Erro ao atualizar empresa.'], 500);
        break;

    case 'DELETE':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id da empresa para excluir.'], 400);
        }

        $sql = "DELETE FROM empresas WHERE ID_empresa = $id";
        if ($conn->query($sql)) {
            sendJson(['mensagem' => 'Empresa excluída com sucesso.'], 200);
        }

        sendJson(['mensagem' => 'Erro ao excluir empresa.'], 500);
        break;

    default:
        sendJson(['mensagem' => 'Método não permitido.'], 405);
        break;
}

$conn->close();
