<?php
session_start();

// Recupera erros e valores antigos (se o cadastro anterior falhou)
$erros   = $_SESSION['erros'] ?? [];
$antigos = $_SESSION['antigos'] ?? [];
$sucesso = $_SESSION['sucesso'] ?? false;

unset($_SESSION['erros'], $_SESSION['antigos'], $_SESSION['sucesso']);

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
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro de Empresa</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<div class="tela">

    <div class="lado-marca">
        <div class="marca-texto">
            <h1>Criar conta</h1>
            <p>Cadastro simples e bonitinho da gigi</p>
        </div>
    </div>

    <div class="lado-formulario">
        <div class="conteudo">
            <h2>Dados da Empresa</h2>

            <?php if ($sucesso): ?>
                <div class="alerta alerta-sucesso">Empresa cadastrada com sucesso!</div>
            <?php endif; ?>

            <?php if (!empty($erros['geral'])): ?>
                <div class="alerta alerta-erro"><?= htmlspecialchars($erros['geral'], ENT_QUOTES, 'UTF-8') ?></div>
            <?php endif; ?>

            <form action="cadastrarEmpresa.php" method="POST" enctype="multipart/form-data" novalidate>
                <div class="grade">
                    <div class="campo">
                        <label>Nome da empresa *</label>
                        <input
                            type="text"
                            name="nome_empresa"
                            value="<?= old($antigos, 'nome_empresa') ?>"
                            required
                        >
                        <?= erro($erros, 'nome_empresa') ?>
                    </div>
                    <div class="campo">
                        <label>Telefone *</label>
                        <input
                            type="tel"
                            name="telefone"
                            id="telefone"
                            placeholder="(51) 99999-9999"
                            value="<?= old($antigos, 'telefone') ?>"
                            required
                        >
                        <?= erro($erros, 'telefone') ?>
                    </div>
                    <div class="campo">
                        <label>Email *</label>
                        <input
                            type="email"
                            name="email"
                            value="<?= old($antigos, 'email') ?>"
                            required
                        >
                        <?= erro($erros, 'email') ?>
                    </div>
                    <div class="campo">
                        <label>Senha *</label>
                        <input
                            type="password"
                            name="senha"
                            minlength="6"
                            required
                        >
                        <?= erro($erros, 'senha') ?>
                    </div>
                    <div class="campo">
                        <label>Endereço</label>
                        <input
                            type="text"
                            name="endereco"
                            value="<?= old($antigos, 'endereco') ?>"
                        >
                  
                    <div class="campo campo-largo">
                        <label>Logo da empresa *</label>
                        <input
                            type="file"
                            name="logo"
                            accept="image/*"
                            required
                        >
                        <?= erro($erros, 'logo') ?>
                    </div>
                </div>

                <button type="submit">
                    Cadastrar
                </button>
            </form>
        </div>
    </div>

</div>

<script>
    // Máscara simples de telefone: (99) 99999-9999
    const inputTelefone = document.getElementById('telefone');
    inputTelefone.addEventListener('input', function () {
        let v = this.value.replace(/\D/g, '').slice(0, 11);
        if (v.length > 10) {
            v = v.replace(/^(\d{2})(\d{5})(\d{0,4}).*/, '($1) $2-$3');
        } else if (v.length > 6) {
            v = v.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3');
        } else if (v.length > 2) {
            v = v.replace(/^(\d{2})(\d{0,5})/, '($1) $2');
        } else if (v.length > 0) {
            v = v.replace(/^(\d*)/, '($1');
        }
        this.value = v;
    });
</script>
</body>
</html>