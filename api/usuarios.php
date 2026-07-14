<?php

header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/conexao.php';
require_once __DIR__ . '/../classes/Usuario.php';

function usuarioPublico($dados)
{
    return [
        'id_usuario' => (int) $dados['ID_usuario'],
        'nome' => (string) $dados['nome'],
        'email' => (string) $dados['email'],
        'foto' => (string) ($dados['foto'] ?? '')
    ];
}

function removerFotoUsuario($caminho)
{
    if (!is_string($caminho) || strpos($caminho, 'img/perfil_') !== 0) {
        return;
    }

    $arquivo = dirname(__DIR__) . DIRECTORY_SEPARATOR . str_replace('/', DIRECTORY_SEPARATOR, $caminho);
    if (is_file($arquivo)) {
        unlink($arquivo);
    }
}

function salvarFotoUsuario()
{
    if (!isset($_FILES['foto']) || $_FILES['foto']['error'] === UPLOAD_ERR_NO_FILE) {
        return [null, null];
    }

    if ($_FILES['foto']['error'] !== UPLOAD_ERR_OK) {
        return [null, 'Não foi possível enviar a foto.'];
    }

    if ((int) $_FILES['foto']['size'] > 5 * 1024 * 1024) {
        return [null, 'A foto deve ter no máximo 5 MB.'];
    }

    $extensoes = ['jpg', 'jpeg', 'png', 'gif', 'webp'];
    $extensao = strtolower(pathinfo($_FILES['foto']['name'], PATHINFO_EXTENSION));
    if (!in_array($extensao, $extensoes, true)) {
        return [null, 'Formato de foto inválido.'];
    }

    $diretorio = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'img';
    if (!is_dir($diretorio) && !mkdir($diretorio, 0775, true)) {
        return [null, 'Não foi possível preparar o armazenamento da foto.'];
    }

    $nome = 'perfil_' . bin2hex(random_bytes(12)) . '.' . $extensao;
    $destino = $diretorio . DIRECTORY_SEPARATOR . $nome;
    if (!move_uploaded_file($_FILES['foto']['tmp_name'], $destino)) {
        return [null, 'Não foi possível salvar a foto.'];
    }

    return ['img/' . $nome, null];
}

function fotoInformada($entrada)
{
    $foto = textoRecebido($entrada, 'foto');
    if ($foto === '' || strpos($foto, 'content://') === 0 || strlen($foto) > 255) {
        return '';
    }
    return $foto;
}

$model = new Usuario($conn);
$entrada = lerEntrada();
$metodo = strtoupper($_SERVER['REQUEST_METHOD']);

if ($metodo === 'POST' && isset($entrada['_method'])) {
    $substituto = strtoupper((string) $entrada['_method']);
    if (in_array($substituto, ['PUT', 'DELETE'], true)) {
        $metodo = $substituto;
    }
}

$acao = acaoRecebida($entrada);

try {
    if ($metodo === 'POST' && $acao === 'login') {
        $email = textoRecebido($entrada, 'email');
        $senha = (string) ($entrada['senha'] ?? '');

        if (!filter_var($email, FILTER_VALIDATE_EMAIL) || $senha === '') {
            responderJson(['success' => false, 'mensagem' => 'Informe e-mail e senha válidos.'], 400);
        }

        $usuario = $model->login($email, $senha);
        if (!$usuario) {
            responderJson(['success' => false, 'mensagem' => 'E-mail ou senha inválidos.'], 401);
        }

        responderJson([
            'success' => true,
            'mensagem' => 'Login efetuado com sucesso.',
            'usuario' => usuarioPublico($usuario)
        ]);
    }

    if ($metodo === 'POST' && $acao === 'logout') {
        responderJson(['success' => true, 'mensagem' => 'Sessão encerrada com sucesso.']);
    }

    if ($metodo === 'POST') {
        $nome = textoRecebido($entrada, 'nome');
        $email = textoRecebido($entrada, 'email');
        $senha = (string) ($entrada['senha'] ?? '');
        $erros = [];

        if ($nome === '') {
            $erros['nome'] = 'Informe o nome completo.';
        }

        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $erros['email'] = 'Informe um e-mail válido.';
        } elseif ($model->emailExiste($email)) {
            $erros['email'] = 'Este e-mail já está cadastrado.';
        }

        if (strlen($senha) < 6) {
            $erros['senha'] = 'A senha deve ter pelo menos 6 caracteres.';
        }

        [$fotoEnviada, $erroFoto] = salvarFotoUsuario();
        if ($erroFoto !== null) {
            $erros['foto'] = $erroFoto;
        }

        if (!empty($erros)) {
            removerFotoUsuario($fotoEnviada);
            responderJson([
                'success' => false,
                'mensagem' => reset($erros),
                'erros' => $erros
            ], 400);
        }

        $foto = $fotoEnviada ?? fotoInformada($entrada);
        $id = $model->registrar($nome, $email, $senha, $foto);
        $usuario = $model->buscarPorId($id);

        responderJson([
            'success' => true,
            'mensagem' => 'Usuário cadastrado com sucesso.',
            'id_usuario' => $id,
            'usuario' => usuarioPublico($usuario)
        ], 201);
    }

    if ($metodo === 'GET') {
        $id = isset($_GET['id']) ? (int) $_GET['id'] : 0;
        if ($id > 0) {
            $usuario = $model->buscarPorId($id);
            if (!$usuario) {
                responderJson(['success' => false, 'mensagem' => 'Usuário não encontrado.'], 404);
            }
            responderJson(['success' => true, 'usuario' => usuarioPublico($usuario)]);
        }

        $usuarios = array_map('usuarioPublico', $model->listar());
        responderJson($usuarios);
    }

    if ($metodo === 'PUT') {
        $id = isset($_GET['id']) ? (int) $_GET['id'] : (int) ($entrada['id'] ?? 0);
        $atual = $model->buscarPorId($id);
        if (!$atual) {
            responderJson(['success' => false, 'mensagem' => 'Usuário não encontrado.'], 404);
        }

        $nome = textoRecebido($entrada, 'nome', $atual['nome']);
        $email = textoRecebido($entrada, 'email', $atual['email']);
        $senha = (string) ($entrada['senha'] ?? '');

        if ($nome === '') {
            responderJson(['success' => false, 'mensagem' => 'Informe o nome completo.'], 400);
        }
        if (!filter_var($email, FILTER_VALIDATE_EMAIL) || $model->emailExiste($email, $id)) {
            responderJson(['success' => false, 'mensagem' => 'Informe um e-mail disponível e válido.'], 400);
        }
        if ($senha !== '' && strlen($senha) < 6) {
            responderJson(['success' => false, 'mensagem' => 'A senha deve ter pelo menos 6 caracteres.'], 400);
        }

        [$fotoEnviada, $erroFoto] = salvarFotoUsuario();
        if ($erroFoto !== null) {
            responderJson(['success' => false, 'mensagem' => $erroFoto], 400);
        }

        $foto = $fotoEnviada;
        if ($foto === null && array_key_exists('foto', $entrada)) {
            $foto = fotoInformada($entrada);
        }

        $model->atualizar($id, $nome, $email, $foto, $senha);
        if ($fotoEnviada !== null) {
            removerFotoUsuario($atual['foto'] ?? '');
        }

        responderJson([
            'success' => true,
            'mensagem' => 'Perfil atualizado com sucesso.',
            'usuario' => usuarioPublico($model->buscarPorId($id))
        ]);
    }

    if ($metodo === 'DELETE') {
        $id = isset($_GET['id']) ? (int) $_GET['id'] : (int) ($entrada['id'] ?? 0);
        $usuario = $model->buscarPorId($id);
        if (!$usuario) {
            responderJson(['success' => false, 'mensagem' => 'Usuário não encontrado.'], 404);
        }

        $model->excluir($id);
        removerFotoUsuario($usuario['foto'] ?? '');
        responderJson(['success' => true, 'mensagem' => 'Usuário excluído com sucesso.']);
    }

    responderJson(['success' => false, 'mensagem' => 'Método não permitido.'], 405);
} catch (Throwable $erro) {
    registrarErroApi($erro);
    responderJson([
        'success' => false,
        'mensagem' => 'A API encontrou um erro ao processar o usuário.'
    ], 500);
}
