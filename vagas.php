<?php require_once __DIR__ . '/classes/vaga.php'; ?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vagas</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        form { max-width: 420px; display: grid; gap: 10px; }
        input, textarea, select, button { padding: 8px; }
        textarea { min-height: 90px; resize: vertical; }
        .mensagem { margin-top: 15px; padding: 10px; background: #fff7e8; border: 1px solid #f2d69b; }
    </style>
</head>
<body>
    <h1>Página de Vagas</h1>
    <p>Cadastre uma vaga abaixo.</p>

    <form method="post">
        <label>Título</label>
        <input type="text" name="titulo" required>

        <label>Descrição</label>
        <textarea name="descricao" required></textarea>

        <label>Salário</label>
        <input type="text" name="salario" placeholder="Ex.: R$ 3.500,00">

        <label>Data de fechamento</label>
        <input type="date" name="fechamento">

        <label>Tipo de vaga</label>
        <select name="tipo">
            <option value="CLT">CLT</option>
            <option value="PJ">PJ</option>
            <option value="Estágio">Estágio</option>
        </select>

        <button type="submit">Salvar</button>
    </form>

    <?php if ($_SERVER['REQUEST_METHOD'] === 'POST') : ?>
        <div class="mensagem">
            <strong>Dados enviados:</strong><br>
            Título: <?= htmlspecialchars($_POST['titulo'] ?? '') ?><br>
            Descrição: <?= htmlspecialchars($_POST['descricao'] ?? '') ?><br>
            Salário: <?= htmlspecialchars($_POST['salario'] ?? '') ?><br>
            Fechamento: <?= htmlspecialchars($_POST['fechamento'] ?? '') ?><br>
            Tipo: <?= htmlspecialchars($_POST['tipo'] ?? '') ?>
        </div>
    <?php endif; ?>

    <p><a href="index.php">Voltar ao início</a></p>
</body>
</html>
