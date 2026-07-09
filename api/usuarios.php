<?php
header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/conexao.php';
require_once __DIR__ . '/../classes/Usuario.php';

function sendJson($data, $status = 200)
{
    http_response_code($status);
    echo json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit;
}

function usuarioPublico(array $usuario)
{
    return [
        'id_usuario' => (int) $usuario['ID_usuario'],
        'nome' => $usuario['nome'],
        'email' => $usuario['email'],
        'foto' => $usuario['foto'],
    ];
}

function salvarFoto()
{
    if (!isset($_FILES['foto']) || $_FILES['foto']['error'] === UPLOAD_ERR_NO_FILE) {
        return [null, null];
    }

    if ($_FILES['foto']['error'] !== UPLOAD_ERR_OK) {
        return [null, 'Não foi possível enviar a imagem de perfil.'];
    }

    $extensoesPermitidas = ['jpg', 'jpeg', 'png', 'gif', 'webp'];
    $extensao = strtolower(pathinfo($_FILES['foto']['name'], PATHINFO_EXTENSION));

    if (!in_array($extensao, $extensoesPermitidas, true)) {
        return [null, 'Formato de imagem inválido. Use jpg, jpeg, png, gif ou webp.'];
    }

    if ($_FILES['foto']['size'] > 5 * 1024 * 1024) {
        return [null, 'A imagem deve ter no máximo 5MB.'];
    }

    $nomeArquivo = 'perfil_' . uniqid('', true) . '.' . $extensao;
    $destino = __DIR__ . '/../img/' . $nomeArquivo;

    if (!move_uploaded_file($_FILES['foto']['tmp_name'], $destino)) {
        return [null, 'Não foi possível salvar a imagem de perfil.'];
    }

    return ['img/' . $nomeArquivo, null];
}

function removerFoto($caminho)
{
    if (!$caminho) {
        return;
    }

    $arquivo = __DIR__ . '/../' . $caminho;
    if (is_file($arquivo)) {
        unlink($arquivo);
    }
}

$usuarioModel = new Usuario($conn);

$method = $_SERVER['REQUEST_METHOD'];

// PHP não popula $_FILES em requisições PUT multipart, então uploads de foto em
// atualizações usam POST com _method=PUT (assim o corpo é interpretado como um POST normal).
if ($method === 'POST' && isset($_POST['_method'])) {
    $override = strtoupper($_POST['_method']);
    if (in_array($override, ['PUT', 'DELETE'], true)) {
        $method = $override;
    }
}

$input = json_decode(file_get_contents('php://input'), true);
if (!is_array($input)) {
    $input = [];
}
if (empty($input)) {
    $input = $_POST;
}

$id = isset($_GET['id']) ? (int) $_GET['id'] : (isset($input['id']) ? (int) $input['id'] : null);
$search = isset($_GET['q']) ? trim($_GET['q']) : '';

switch ($method) {
    case 'GET':
        if ($id !== null) {
            $usuario = $usuarioModel->lerPorIdUsuario($id);
            if (!$usuario) {
                sendJson(['mensagem' => 'Usuário não encontrado.'], 404);
            }
            sendJson(usuarioPublico($usuario));
        }

        $resultado = $usuarioModel->lerUsuarios();
        $usuarios = [];
        while ($linha = $resultado->fetch_assoc()) {
            if ($search !== '' && stripos($linha['nome'] . ' ' . $linha['email'], $search) === false) {
                continue;
            }
            $usuarios[] = usuarioPublico($linha);
        }

        sendJson($usuarios);
        break;

    case 'POST':
        if (($input['action'] ?? '') === 'login') {
            $email = trim($input['email'] ?? '');
            $senha = (string) ($input['senha'] ?? '');

            $usuario = $usuarioModel->login($email, $senha);
            if (!$usuario) {
                sendJson(['mensagem' => 'E-mail ou senha inválidos.'], 401);
            }


            sendJson(usuarioPublico($usuario));
        }

        $nome = trim($input['nome'] ?? '');
        $email = trim($input['email'] ?? '');
        $senha = (string) ($input['senha'] ?? '');

        $erros = [];
        if ($nome === '') {
            $erros['nome'] = 'Informe o nome.';
        }
        if ($email === '' || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $erros['email'] = 'Informe um e-mail válido.';
        } elseif ($usuarioModel->emailExiste($email)) {
            $erros['email'] = 'Este e-mail já está cadastrado.';
        }
        if (strlen($senha) < 6) {
            $erros['senha'] = 'A senha deve ter pelo menos 6 caracteres.';
        }

        [$foto, $erroFoto] = salvarFoto();
        if ($erroFoto) {
            $erros['foto'] = $erroFoto;
        }

        if (!empty($erros)) {
            removerFoto($foto);
            sendJson(['mensagem' => 'Preencha os campos corretamente.', 'erros' => $erros], 400);
        }

        try {
            $usuarioModel->registrar($nome, $email, $senha, $foto);
            sendJson(['mensagem' => 'Usuário cadastrado com sucesso.', 'id_usuario' => $conn->insert_id], 201);
        } catch (Exception $e) {
            removerFoto($foto);
            sendJson(['mensagem' => 'Erro ao cadastrar usuário.'], 500);
        }
        break;

    case 'PUT':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id do usuário para atualizar.'], 400);
        }

        $usuarioAtual = $usuarioModel->lerPorIdUsuario($id);
        if (!$usuarioAtual) {
            sendJson(['mensagem' => 'Usuário não encontrado.'], 404);
        }

        $nome = isset($input['nome']) && trim($input['nome']) !== '' ? trim($input['nome']) : $usuarioAtual['nome'];
        $email = isset($input['email']) && trim($input['email']) !== '' ? trim($input['email']) : $usuarioAtual['email'];
        $senha = isset($input['senha']) && $input['senha'] !== '' ? (string) $input['senha'] : null;

        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            sendJson(['mensagem' => 'Informe um e-mail válido.'], 400);
        }
        if ($senha !== null && strlen($senha) < 6) {
            sendJson(['mensagem' => 'A senha deve ter pelo menos 6 caracteres.'], 400);
        }
        if ($email !== $usuarioAtual['email'] && $usuarioModel->emailExiste($email)) {
            sendJson(['mensagem' => 'Este e-mail já está cadastrado.'], 400);
        }

        [$novaFoto, $erroFoto] = salvarFoto();
        if ($erroFoto) {
            sendJson(['mensagem' => $erroFoto], 400);
        }

        if ($novaFoto !== null) {
            removerFoto($usuarioAtual['foto'] ?? null);
        }

        $usuarioModel->atualizarUsuario($id, $nome, $email, $novaFoto, $senha);
        sendJson(['mensagem' => 'Usuário atualizado com sucesso.']);
        break;

    case 'DELETE':
        if ($id === null) {
            sendJson(['mensagem' => 'Informe o id do usuário para excluir.'], 400);
        }

        $usuarioAtual = $usuarioModel->lerPorIdUsuario($id);
        if (!$usuarioAtual) {
            sendJson(['mensagem' => 'Usuário não encontrado.'], 404);
        }

        $usuarioModel->deletarUsuario($id);
        removerFoto($usuarioAtual['foto'] ?? null);
        sendJson(['mensagem' => 'Usuário excluído com sucesso.']);
        break;

    default:
        sendJson(['mensagem' => 'Método não permitido.'], 405);
        break;
}

$conn->close();