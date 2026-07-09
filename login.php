<?php
session_start();

require_once __DIR__ . '/api/conexao.php';
require_once __DIR__ . '/classes/Empresas.php';

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
        try {
            $empresaModel = new Empresas($conn);

            $empresa = $empresaModel->buscarPorEmail($dados['email']);

            if ($empresa && password_verify($dados['senha'], $empresa['senha'])) {
                $_SESSION['empresa_id']   = $empresa['id'];
                $_SESSION['empresa_nome'] = $empresa['nome_empresa'];

                header('Location: login.php');
                exit;
            }

            $erros['geral'] = 'E-mail ou senha inválidos.';
        } catch (Exception $e) {
            $erros['geral'] = 'Não foi possível autenticar. Tente novamente.';
        }
    }

    $_SESSION['erros']   = $erros;
    $_SESSION['antigos'] = $dados;
    header('Location: login.php');
    exit;
}
?>

<?php include_once(__DIR__ . '/classes/Usuario.php'); ?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login de Empresa</title>
    <link rel="stylesheet" href="login.css">
</head>
<body>
<div class="tela">

    <div class="lado-marca">
        <div class="marca-texto">
            <h1>Login</h1>
            <p>Acesse a conta da sua empresa</p>
        </div>
    </div>

    <div class="lado-formulario">
        <div class="conteudo">
            <h2>Entrar</h2>

            <?php if (!empty($erros['geral'])): ?>
                <div class="alerta alerta-erro"><?= htmlspecialchars($erros['geral'], ENT_QUOTES, 'UTF-8') ?></div>
            <?php endif; ?>

            <form action="login.php" method="POST" novalidate>
                <div class="grade">
                    <div class="campo campo-largo">
                        <label>Email *</label>
                        <input
                            type="email"
                            name="email"
                            value="<?= old($antigos, 'email') ?>"
                            required
                        >
                        <?= erro($erros, 'email') ?>
                    </div>
                    <div class="campo campo-largo">
                        <label>Senha *</label>
                        <input
                            type="password"
                            name="senha"
                            required
                        >
                        <?= erro($erros, 'senha') ?>
                    </div>
                </div>

                <button type="submit">
                    Entrar
                </button>
            </form>

            <a href="index.php" class="btn-link">Ainda não tenho conta</a>
        </div>
    </div>
</div>
</body>
</html>