<?php require_once __DIR__ . '/classes/empresas.php'; ?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Empresas</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        form { max-width: 420px; display: grid; gap: 10px; }
        input, button { padding: 8px; }
        .mensagem { margin-top: 15px; padding: 10px; background: #eef8ee; border: 1px solid #b8e0b8; }
    </style>
</head>
<body>
    <h1>Página de Empresas</h1>
    <p>Preencha os dados da empresa abaixo.</p>

    <form method="post">
        <label>Nome</label>
        <input type="text" name="nome" required>

        <label>Email</label>
        <input type="email" name="email" required>

        <label>Senha</label>
        <input type="password" name="senha" required>

        <label>Endereço</label>
        <input type="text" name="endereco">

        <label>Telefone</label>
        <input type="text" name="telefone">

        <label>Logo</label>
        <input type="text" name="logo" placeholder="URL ou nome do arquivo">

        <button type="submit">Salvar</button>
    </form>

    <?php if ($_SERVER['REQUEST_METHOD'] === 'POST') : ?>
        <div class="mensagem">
            <strong>Dados enviados:</strong><br>
            Nome: <?= htmlspecialchars($_POST['nome'] ?? '') ?><br>
            Email: <?= htmlspecialchars($_POST['email'] ?? '') ?><br>
            Endereço: <?= htmlspecialchars($_POST['endereco'] ?? '') ?><br>
            Telefone: <?= htmlspecialchars($_POST['telefone'] ?? '') ?><br>
            Logo: <?= htmlspecialchars($_POST['logo'] ?? '') ?>
        </div>
    <?php endif; ?>

    <p><a href="index.php">Voltar ao início</a></p>
</body>
</html>
