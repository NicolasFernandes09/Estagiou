<?php
session_start();

require_once __DIR__ . '/../api/conexao.php';
require_once __DIR__ . '/../classes/Admin.php';
require_once __DIR__ . '/../classes/Usuario.php';
require_once __DIR__ . '/../classes/Empresas.php';

$erros   = $_SESSION['erros'] ?? [];
$antigos = $_SESSION['antigos'] ?? [];

unset($_SESSION['erros'], $_SESSION['antigos']);

function old(array $antigos, string $campo): string
{
    return htmlspecialchars($antigos[$campo] ?? '', ENT_QUOTES, 'UTF-8');
}

function erro(array $erros, string $campo): string
{
    return isset($erros[$campo])
        ? '<span class="erro-msg">' . htmlspecialchars($erros[$campo], ENT_QUOTES, 'UTF-8') . '</span>'
        : '';
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $erros = [];
    $dados = [
        'email' => trim($_POST['email'] ?? ''),
        'senha' => trim($_POST['senha'] ?? ''),
    ];

    if ($dados['email'] === '' || !filter_var($dados['email'], FILTER_VALIDATE_EMAIL)) {
        $erros['email'] = 'Informe um e-mail válido.';
    }

    if ($dados['senha'] === '') {
        $erros['senha'] = 'Informe a senha.';
    }

    if (empty($erros)) {
        $adminModel = new Admin($conn);
        $admin = $adminModel->login($dados['email'], $dados['senha']);

        if ($admin) {
            $_SESSION['admin_id']    = $admin['id_adm'];
            $_SESSION['admin_nome']  = $admin['nome'];
            $_SESSION['usuario_tipo'] = 'admin';

            header('Location: administrador.php');
            exit;
        }

        $usuarioModel = new Usuario($conn);
        $usuario = $usuarioModel->login($dados['email'], $dados['senha']);

        if ($usuario) {
            $_SESSION['usuario_id']   = $usuario['ID_usuario'];
            $_SESSION['usuario_nome'] = $usuario['nome'];
            $_SESSION['usuario_tipo'] = 'usuario';

            header('Location: listarVagas.php');
            exit;
        }

        $empresaModel = new Empresas($conn);
        $empresa = $empresaModel->login($dados['email'], $dados['senha']);

        if ($empresa) {
            $_SESSION['empresa_id']   = $empresa['ID_empresa'];
            $_SESSION['empresa_nome'] = $empresa['nome'];
            $_SESSION['usuario_tipo'] = 'empresa';

            header('Location: listarVagas.php');
            exit;
        }

        $erros['geral'] = 'E-mail ou senha inválidos.';
    }

    $_SESSION['erros']   = $erros;
    $_SESSION['antigos'] = $dados;
    header('Location: login.php');
    exit;
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="../assets/css/base.css">
    <link rel="stylesheet" href="../assets/css/forms.css">
</head>
<body>
<div class="tela">

    <div class="lado-marca">
        <div class="marca-texto">
            <h1>Bem-vindo de volta</h1>
            <p>Acesse sua conta para continuar.</p>
        </div>
    </div>

    <div class="lado-formulario">
        <div class="conteudo">
            <h2>Entrar</h2>

            <?php if (!empty($erros['geral'])): ?>
                <div class="alerta alerta-erro"><?= htmlspecialchars($erros['geral'], ENT_QUOTES, 'UTF-8') ?></div>
            <?php endif; ?>

            <form action="login.php" method="POST" novalidate>
                <div class="campo">
                    <label>Email</label>
                    <input
                        type="email"
                        name="email"
                        value="<?= old($antigos, 'email') ?>"
                        required
                    >
                    <?= erro($erros, 'email') ?>
                </div>

                <div class="campo">
                    <label>Senha</label>
                    <input
                        type="password"
                        name="senha"
                        required
                    >
                    <?= erro($erros, 'senha') ?>
                </div>

                <button type="submit">Entrar</button>
            </form>

            <a href="cadastrarEmpresa.php" class="toggle-link">Ainda não tenho conta</a>
        </div>
    </div>
</div>
</body>
</html>
