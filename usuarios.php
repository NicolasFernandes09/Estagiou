<?php require_once __DIR__ . '/classes/usuario.php'; ?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Usuários</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        form { max-width: 420px; display: grid; gap: 10px; }
        input, select, button { padding: 8px; }
        .mensagem { margin-top: 15px; padding: 10px; background: #eef5ff; border: 1px solid #b8d2ff; }
    </style>
</head>
<body>
    <h1>Página de Usuários</h1>
    <p>Cadastre os dados do usuário abaixo.</p>

    <form method="post">
        <label>Nome</label>
        <input type="text" name="nome" required>

        <label>Email</label>
        <input type="email" name="email" required>

        <label>Senha</label>
        <input type="password" name="senha" required>

        <label>Nível</label>
        <select name="nivel">
            <option value="user">Usuário</option>
            <option value="admin">Administrador</option>
        </select>

        <button type="submit">Salvar</button>
    </form>

    <?php if ($_SERVER['REQUEST_METHOD'] === 'POST') : ?>
        <div class="mensagem">
            <strong>Dados enviados:</strong><br>
            Nome: <?= htmlspecialchars($_POST['nome'] ?? '') ?><br>
            Email: <?= htmlspecialchars($_POST['email'] ?? '') ?><br>
            Nível: <?= htmlspecialchars($_POST['nivel'] ?? '') ?>
        </div>
    <?php endif; ?>

    <p><a href="index.php">Voltar ao início</a></p>
</body>
</html>
