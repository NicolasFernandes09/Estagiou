<?php
session_start();

// Redirecionar imediatamente se já estiver logado (evita processamento desnecessário)
if (isset($_SESSION['logado']) && $_SESSION['logado'] === true) {
    header('Location: index.php');
    exit;
}

require_once(__DIR__ . '/api/conexao.php'); 
require_once(__DIR__ . '/classes/Usuario.php');

$mensagem_erro = '';
$mensagem_sucesso = '';

// Processar login se for POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = trim($_POST['usuario'] ?? '');
    $senha = trim($_POST['senha'] ?? '');
    
    // Validações básicas
    if (empty($email) || empty($senha)) {
        $mensagem_erro = 'Por favor, preencha todos os campos.';
    } elseif (strlen($senha) < 6) {
        $mensagem_erro = 'A senha deve ter pelo menos 6 caracteres.';
    } else {
        try {
            // Agora a variável $conn existe porque importamos o arquivo de conexão acima
            $usuario = new Usuario($conn);
            $resultado = $usuario->login($email, $senha);
            
            if ($resultado) {
                // Login bem-sucedido
                $_SESSION['usuario_id'] = $resultado['id'];
                $_SESSION['usuario_nome'] = $resultado['nome'];
                $_SESSION['usuario_email'] = $resultado['email'];
                $_SESSION['usuario_nivel'] = $resultado['nivel'] ?? 'user';
                $_SESSION['logado'] = true;
                
                header('Location: index.php');
                exit;
            } else {
                $mensagem_erro = 'E-mail ou senha incorretos.';
            }
        } catch (Exception $e) {
            // Em desenvolvimento, você pode usar $e->getMessage() para debugar, mas em produção use uma frase genérica
            $mensagem_erro = 'Erro ao processar o login: ' . $e->getMessage();
        }
    }
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
</body>
</html>