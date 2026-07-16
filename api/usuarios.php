<?php

header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/conexao.php';
require_once __DIR__ . '/../classes/Usuario.php';

function usuarioPublico($dados)
{
    $id = $dados['ID_usuario'] ?? $dados['id_usuario'] ?? 0;

    return [
        'id_usuario' => (int) $id,
        'nome' => (string) ($dados['nome'] ?? ''),
        'usuario' => (string) ($dados['usuario'] ?? ''),
        'email' => (string) ($dados['email'] ?? ''),
        'descricao_pessoal' => (string) ($dados['descricao_pessoal'] ?? ''),
        'descricao_profissional' => (string) ($dados['descricao_profissional'] ?? ''),
        'foto' => (string) ($dados['foto'] ?? '')
    ];
}

function tamanhoTextoUsuario($texto)
{
    return function_exists('mb_strlen') ? mb_strlen($texto, 'UTF-8') : strlen($texto);
}

function nomeUsuarioValido($usuario)
{
    return preg_match('/^[\p{L}\p{N}_]{3,20}$/u', $usuario) === 1;
}

function idUsuarioRecebido($entrada)
{
    return (int) ($_GET['id'] ?? $_GET['id_usuario'] ?? $entrada['id'] ?? $entrada['id_usuario'] ?? 0);
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
$fotoEnviada = null;

try {
    if ($metodo === 'POST' && $acao === 'login') {
        $usuarioInformado = textoRecebido($entrada, 'usuario');
        $email = textoRecebido($entrada, 'email');
        $senha = (string) ($entrada['senha'] ?? '');

        if ($usuarioInformado === '' || !filter_var($email, FILTER_VALIDATE_EMAIL) || $senha === '') {
            responderJson(['success' => false, 'mensagem' => 'Informe usuário, e-mail e senha válidos.'], 400);
        }

        $usuario = $model->login($usuarioInformado, $email, $senha);
        if (!$usuario) {
            responderJson(['success' => false, 'mensagem' => 'Usuário, e-mail ou senha inválidos.'], 401);
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
        $usuario = textoRecebido($entrada, 'usuario');
        $email = textoRecebido($entrada, 'email');
        $senha = (string) ($entrada['senha'] ?? '');
        $descricaoPessoal = textoRecebido($entrada, 'descricao_pessoal');
        $descricaoProfissional = textoRecebido($entrada, 'descricao_profissional');
        $erros = [];

        if ($nome === '') {
            $erros['nome'] = 'Informe o nome completo.';
        }

        if (!nomeUsuarioValido($usuario)) {
            $erros['usuario'] = 'O usuário deve ter de 3 a 20 letras, números ou sublinhado.';
        } elseif ($model->usuarioExiste($usuario)) {
            $erros['usuario'] = 'Este nome de usuário já está cadastrado.';
        }

        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $erros['email'] = 'Informe um e-mail válido.';
        } elseif ($model->emailExiste($email)) {
            $erros['email'] = 'Este e-mail já está cadastrado.';
        }

        if (strlen($senha) < 6) {
            $erros['senha'] = 'A senha deve ter pelo menos 6 caracteres.';
        }

        if (tamanhoTextoUsuario($descricaoPessoal) > 450) {
            $erros['descricao_pessoal'] = 'A descrição pessoal deve ter no máximo 450 caracteres.';
        }

        if (tamanhoTextoUsuario($descricaoProfissional) > 450) {
            $erros['descricao_profissional'] = 'A descrição profissional deve ter no máximo 450 caracteres.';
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
        $id = $model->registrar(
            $nome,
            $usuario,
            $email,
            $senha,
            $descricaoPessoal,
            $descricaoProfissional,
            $foto
        );
        $usuarioCadastrado = $model->buscarPorId($id);

        responderJson([
            'success' => true,
            'mensagem' => 'Usuário cadastrado com sucesso.',
            'id_usuario' => $id,
            'usuario' => usuarioPublico($usuarioCadastrado)
        ], 201);
    }

    if ($metodo === 'GET') {
        $id = idUsuarioRecebido($entrada);
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
        $id = idUsuarioRecebido($entrada);
        $atual = $model->buscarPorId($id);
        if (!$atual) {
            responderJson(['success' => false, 'mensagem' => 'Usuário não encontrado.'], 404);
        }

        $nome = textoRecebido($entrada, 'nome', $atual['nome']);
        $usuario = textoRecebido($entrada, 'usuario', $atual['usuario']);
        $email = textoRecebido($entrada, 'email', $atual['email']);
        $senha = (string) ($entrada['senha'] ?? '');
        $descricaoPessoal = textoRecebido(
            $entrada,
            'descricao_pessoal',
            $atual['descricao_pessoal'] ?? ''
        );
        $descricaoProfissional = textoRecebido(
            $entrada,
            'descricao_profissional',
            $atual['descricao_profissional'] ?? ''
        );

        if ($nome === '') {
            responderJson(['success' => false, 'mensagem' => 'Informe o nome completo.'], 400);
        }
        if (!nomeUsuarioValido($usuario) || $model->usuarioExiste($usuario, $id)) {
            responderJson(['success' => false, 'mensagem' => 'Informe um nome de usuário disponível e válido.'], 400);
        }
        if (!filter_var($email, FILTER_VALIDATE_EMAIL) || $model->emailExiste($email, $id)) {
            responderJson(['success' => false, 'mensagem' => 'Informe um e-mail disponível e válido.'], 400);
        }
        if ($senha !== '' && strlen($senha) < 6) {
            responderJson(['success' => false, 'mensagem' => 'A senha deve ter pelo menos 6 caracteres.'], 400);
        }
        if (tamanhoTextoUsuario($descricaoPessoal) > 450 || tamanhoTextoUsuario($descricaoProfissional) > 450) {
            responderJson(['success' => false, 'mensagem' => 'Cada descrição deve ter no máximo 450 caracteres.'], 400);
        }

        [$fotoEnviada, $erroFoto] = salvarFotoUsuario();
        if ($erroFoto !== null) {
            responderJson(['success' => false, 'mensagem' => $erroFoto], 400);
        }

        $foto = $atual['foto'] ?? '';
        if ($fotoEnviada !== null) {
            $foto = $fotoEnviada;
        } elseif (array_key_exists('foto', $entrada)) {
            $foto = fotoInformada($entrada);
        }

        $model->atualizar(
            $id,
            $nome,
            $usuario,
            $email,
            $descricaoPessoal,
            $descricaoProfissional,
            $foto,
            $senha
        );
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
        $id = idUsuarioRecebido($entrada);
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
    removerFotoUsuario($fotoEnviada);
    registrarErroApi($erro);
    if ((int) $erro->getCode() === 1062) {
        responderJson([
            'success' => false,
            'mensagem' => 'E-mail ou nome de usuário já cadastrado.'
        ], 409);
    }
    responderJson([
        'success' => false,
        'mensagem' => 'A API encontrou um erro ao processar o usuário.'
    ], 500);
}
