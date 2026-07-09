<?php
session_start();
require_once(__DIR__ . '/api/conexao.php');
require_once(__DIR__ . '/classes/Empresas.php');

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
            $empresaModel = new Empresas($conn);
            $resultado = $empresaModel->login($email, $senha);

            if ($resultado) {
                $_SESSION['usuario_id'] = $resultado['ID_empresa'] ?? $resultado['id_empresa'] ?? $resultado['id'] ?? null;
                $_SESSION['usuario_nome'] = $resultado['nome'] ?? '';
                $_SESSION['usuario_email'] = $resultado['email'] ?? '';
                $_SESSION['usuario_nivel'] = 'empresa';
                $_SESSION['empresa_id'] = $resultado['ID_empresa'] ?? $resultado['id_empresa'] ?? $resultado['id'] ?? null;
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
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
                <p class="subtitle">Use seu e-mail e senha cadastrados.</p>
                
                <?php if (!empty($mensagem_erro)): ?>
                    <div class="alerta alerta-erro" role="alert">
                        <?= htmlspecialchars($mensagem_erro) ?>
                    </div>
                <?php endif; ?>
                
                <?php if (!empty($mensagem_sucesso)): ?>
                    <div class="alerta alerta-sucesso" role="alert">
                        <?= htmlspecialchars($mensagem_sucesso) ?>
                    </div>
                <?php endif; ?>
                
                <form action="login.php" method="POST">
                    <div class="form-group">
                        <label for="web-usuario">E-mail</label>
                        <input type="email" id="web-usuario" name="usuario" class="form-control" placeholder="seu@email.com" required>
                    </div>
                
                    <div class="form-group">
                        <label for="web-senha">Senha</label>
                        <input type="password" id="web-senha" name="senha" class="form-control" placeholder="Sua senha" required>
                        <div class="input-hint">Mínimo de 6 caracteres</div>
                    </div>
                    
                    <button type="submit" class="btn-submit">Entrar</button>
                </form>
                
                <a href="cadastro.php" class="toggle-link">Criar conta</a>
            </div>
        </div>
    </div>

</body>
</html>