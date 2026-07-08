<?php include_once(__DIR__ . '/classes/Usuario.php'); ?>
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
                
                <a href="cadastro.php" class="toggle-link">Criar conta</a>
            </div>
        </div>
    </div>

</body>
</html>