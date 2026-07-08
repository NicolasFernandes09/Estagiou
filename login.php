<<<<<<< HEAD
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
=======
<?php include_once(__DIR__ . '/classes/Usuario.php'); ?>
>>>>>>> 827772f1d5bb62c46139d0e84465506ad2ac1b0b
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
<<<<<<< HEAD
    <title>Login de Empresa</title>
    <link rel="stylesheet" href="login.css">
</head>
<body>
<div class="tela">

    <div class="lado-marca">
        <div class="marca-texto">
            <h1>Bem-vindo de volta</h1>
            <p>Acesse sua conta de empresa</p>
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

            <a href="index.php" class="link">Ainda não tenho conta</a>
        </div>
    </div>
</div>
=======
    <title>Mural de Oportunidades - Login</title>
    <link rel="stylesheet" href="login.css">
</head>
<body>

    <div class="login-container">
        <div class="brand-panel">
            <div class="brand-content">
                <h1>Login</h1>
                <p>Entre para ver vagas da escola</p>
            </div>
        </div>

        <div class="form-panel">
            <div class="form-content">
                <h2>Acesse sua conta</h2>
                <p class="subtitle">Use seu usuário e senha cadastrados.</p>
                
                <form action="index.php?rota=login" method="POST">
                    <div class="form-group">
                        <label for="web-usuario">Usuário</label>
                        <input type="text" id="web-usuario" name="usuario" class="form-control" placeholder="Seu usuário" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="web-senha">Senha</label>
                        <input type="password" id="web-senha" name="senha" class="form-control" placeholder="Sua senha" required>
                        <div class="input-hint">Mínimo de 6 caracteres</div>
                    </div>
                    
                    <button type="submit" class="btn-submit">Entrar</button>
                </form>
                
                <a href="#" class="toggle-link">Criar conta</a>
            </div>
        </div>
    </div>

>>>>>>> 827772f1d5bb62c46139d0e84465506ad2ac1b0b
</body>
</html>