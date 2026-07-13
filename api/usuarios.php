<?php

header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/conexao.php';
require_once __DIR__ . '/common.php';
require_once __DIR__ . '/../classes/Usuario.php';

function usuarioPublico($dados)
{
    return [
        'id_usuario' => (int) $dados['ID_usuario'],
        'nome' => $dados['nome'] ?? '',
        'usuario' => $dados['usuario'] ?? '',
        'email' => $dados['email'] ?? '',
        'descricao_profissional' => $dados['descricao_profissional'] ?? '',
        'descricao_pessoal' => $dados['descricao_pessoal'] ?? '',
        'foto' => $dados['foto'] ?? ''
    ];
}

function tamanhoTexto($texto)
{
    return function_exists('mb_strlen') ? mb_strlen($texto, 'UTF-8') : strlen($texto);
}

$model = new Usuario($conn);
$entrada = lerEntrada();
$metodo = $_SERVER['REQUEST_METHOD'];
$acao = strtolower(trim($entrada['acao'] ?? $entrada['action'] ?? ''));

if ($metodo === 'POST' && $acao === 'login') {
    $usuario = trim($entrada['usuario'] ?? '');
    $email = trim($entrada['email'] ?? '');
    $senha = (string) ($entrada['senha'] ?? '');
    $dados = $model->login($usuario, $email, $senha);

    if (!$dados) {
        responderJson(['success' => false, 'mensagem' => 'Usuário, e-mail ou senha inválidos.'], 401);
    }

    $token = criarToken($conn, (int) $dados['ID_usuario']);
    responderJson([
        'success' => true,
        'mensagem' => 'Login efetuado com sucesso.',
        'token' => $token,
        'id_usuario' => (int) $dados['ID_usuario'],
        'usuario' => usuarioPublico($dados)
    ]);
}

if ($metodo === 'POST' && $acao === 'logout') {
    exigirUsuario($conn);
    revogarToken($conn);
    responderJson(['success' => true, 'mensagem' => 'Sessão encerrada com sucesso.']);
}

if ($metodo === 'POST') {
    $nome = trim($entrada['nome'] ?? '');
    $usuario = trim($entrada['usuario'] ?? '');
    $email = trim($entrada['email'] ?? '');
    $senha = (string) ($entrada['senha'] ?? '');
    $profissional = trim($entrada['descricao_profissional'] ?? '');
    $pessoal = trim($entrada['descricao_pessoal'] ?? '');
    $foto = (string) ($entrada['foto'] ?? '');
    $erros = [];

    if ($nome === '') {
        $erros['nome'] = 'Informe o nome completo.';
    }
    if (!preg_match('/^[\p{L}0-9_]{3,20}$/u', $usuario)) {
        $erros['usuario'] = 'Use de 3 a 20 letras, números ou underline no usuário.';
    } elseif ($model->usuarioExiste($usuario)) {
        $erros['usuario'] = 'Este usuário já está cadastrado.';
    }
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $erros['email'] = 'Informe um e-mail válido.';
    } elseif ($model->emailExiste($email)) {
        $erros['email'] = 'Este e-mail já está cadastrado.';
    }
    if (strlen($senha) < 6) {
        $erros['senha'] = 'A senha deve ter pelo menos 6 caracteres.';
    }
    if (tamanhoTexto($profissional) > 450 || tamanhoTexto($pessoal) > 450) {
        $erros['descricao'] = 'Cada descrição deve ter no máximo 450 caracteres.';
    }

    if (!empty($erros)) {
        responderJson(['success' => false, 'mensagem' => 'Preencha os campos corretamente.', 'erros' => $erros], 400);
    }

    $id = $model->registrar($nome, $usuario, $email, $senha, $profissional, $pessoal, $foto);
    $dados = $model->buscarPorId($id);
    responderJson([
        'success' => true,
        'mensagem' => 'Usuário cadastrado com sucesso.',
        'id_usuario' => $id,
        'usuario' => usuarioPublico($dados)
    ], 201);
}

if ($metodo === 'GET') {
    $id = exigirUsuario($conn);
    $dados = $model->buscarPorId($id);
    if (!$dados) {
        responderJson(['success' => false, 'mensagem' => 'Usuário não encontrado.'], 404);
    }
    responderJson(['success' => true, 'usuario' => usuarioPublico($dados)]);
}

if ($metodo === 'PUT') {
    $id = exigirUsuario($conn);
    $atual = $model->buscarPorId($id);
    if (!$atual) {
        responderJson(['success' => false, 'mensagem' => 'Usuário não encontrado.'], 404);
    }

    $nome = trim($entrada['nome'] ?? $atual['nome']);
    $usuario = trim($entrada['usuario'] ?? $atual['usuario']);
    $email = trim($entrada['email'] ?? $atual['email']);
    $profissional = trim($entrada['descricao_profissional'] ?? $atual['descricao_profissional']);
    $pessoal = trim($entrada['descricao_pessoal'] ?? $atual['descricao_pessoal']);
    $foto = (string) ($entrada['foto'] ?? $atual['foto']);

    if (!preg_match('/^[\p{L}0-9_]{3,20}$/u', $usuario)
        || !filter_var($email, FILTER_VALIDATE_EMAIL)
        || $model->usuarioExiste($usuario, $id)
        || $model->emailExiste($email, $id)
        || tamanhoTexto($profissional) > 450
        || tamanhoTexto($pessoal) > 450) {
        responderJson(['success' => false, 'mensagem' => 'Os dados informados são inválidos.'], 400);
    }

    $model->atualizar($id, $nome, $usuario, $email, $profissional, $pessoal, $foto);
    responderJson(['success' => true, 'mensagem' => 'Perfil atualizado com sucesso.', 'usuario' => usuarioPublico($model->buscarPorId($id))]);
}

responderJson(['success' => false, 'mensagem' => 'Método não permitido.'], 405);
