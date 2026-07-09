<?php
session_start();

require_once __DIR__ . '/api/conexao.php';
require_once __DIR__ . '/classes/Vaga.php';

if (($_SESSION['usuario_tipo'] ?? null) !== 'empresa') {
    header('Location: login.php');
    exit;
}

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

function validarVaga(array $dados, array $tiposValidos): array
{
    $erros = [];

    if ($dados['titulo'] === '') {
        $erros['titulo'] = 'Informe o título da vaga.';
    }

    if ($dados['descricao'] === '') {
        $erros['descricao'] = 'Informe a descrição da vaga.';
    }

    if ($dados['salario'] === '' || !is_numeric($dados['salario']) || (float) $dados['salario'] < 0) {
        $erros['salario'] = 'Informe um salário válido.';
    }

    if (!array_key_exists($dados['tipo_vaga'], $tiposValidos)) {
        $erros['tipo_vaga'] = 'Selecione o tipo da vaga.';
    }

    if ($dados['contato'] === '') {
        $erros['contato'] = 'Informe o contato do responsável.';
    }

    if ($dados['fechamento_vaga'] === '') {
        $erros['fechamento_vaga'] = 'Informe a data limite para candidatura.';
    } else {
        $data = date_create_from_format('Y-m-d\TH:i', $dados['fechamento_vaga']);
        if ($data === false) {
            $erros['fechamento_vaga'] = 'Informe uma data e hora válidas.';
        } elseif ($data < new DateTime()) {
            $erros['fechamento_vaga'] = 'A data limite deve estar no futuro.';
        }
    }

    return $erros;
}

$opcoesTipo = [
    'estagio'        => 'Estágio',
    'jovem_aprendiz' => 'Jovem Aprendiz',
    'clt'            => 'CLT',
    'freelancer'     => 'Freelancer',
];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $dados = [
        'titulo'          => trim($_POST['titulo'] ?? ''),
        'descricao'       => trim($_POST['descricao'] ?? ''),
        'salario'         => trim($_POST['salario'] ?? ''),
        'tipo_vaga'       => trim($_POST['tipo_vaga'] ?? ''),
        'contato'         => trim($_POST['contato'] ?? ''),
        'fechamento_vaga' => trim($_POST['fechamento_vaga'] ?? ''),
    ];

    $erros = validarVaga($dados, $opcoesTipo);

    if (empty($erros)) {
        try {
            $vagaModel = new Vaga($conn);
            $vagaModel->cadastrar(
                $dados['titulo'],
                $dados['descricao'],
                $dados['salario'],
                str_replace('T', ' ', $dados['fechamento_vaga']),
                $dados['tipo_vaga'],
                $dados['contato'],
                $_SESSION['empresa_id']
            );

            $_SESSION['sucesso'] = true;
            header('Location: postar-vaga.php');
            exit;
        } catch (Exception $e) {
            $erros['geral'] = 'Não foi possível publicar a vaga.';
        }
    }

    $_SESSION['erros']   = $erros;
    $_SESSION['antigos'] = $dados;
    $_SESSION['sucesso'] = false;
    header('Location: postar-vaga.php');
    exit;
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Postar Vaga</title>
    <link rel="stylesheet" href="cadastro.css">
</head>
<body>
<div class="tela">

    <div class="lado-marca">
        <div class="marca-texto">
            <h1>Nova vaga</h1>
            <p>Publique uma oportunidade para os candidatos.</p>
        </div>
    </div>

    <div class="lado-formulario">
        <div class="conteudo">
            <h2>Dados da vaga</h2>

            <?php if ($sucesso): ?>
                <div class="alerta alerta-sucesso">Vaga publicada com sucesso!</div>
            <?php endif; ?>

            <?php if (!empty($erros['geral'])): ?>
                <div class="alerta alerta-erro"><?= htmlspecialchars($erros['geral'], ENT_QUOTES, 'UTF-8') ?></div>
            <?php endif; ?>

            <form action="postar-vaga.php" method="POST" novalidate>
                <div class="grade">
                    <div class="campo campo-largo">
                        <label>Título *</label>
                        <input
                            type="text"
                            name="titulo"
                            value="<?= old($antigos, 'titulo') ?>"
                            required
                        >
                        <?= erro($erros, 'titulo') ?>
                    </div>

                    <div class="campo campo-largo">
                        <label>Descrição *</label>
                        <input
                            type="text"
                            name="descricao"
                            value="<?= old($antigos, 'descricao') ?>"
                            required
                        >
                        <?= erro($erros, 'descricao') ?>
                    </div>

                    <div class="campo">
                        <label>Salário *</label>
                        <input
                            type="number"
                            step="0.01"
                            name="salario"
                            value="<?= old($antigos, 'salario') ?>"
                            required
                        >
                        <?= erro($erros, 'salario') ?>
                    </div>

                    <div class="campo">
                        <label>Tipo *</label>
                        <select name="tipo_vaga" required>
                            <option value="">Selecione</option>
                            <?php foreach ($opcoesTipo as $valor => $rotulo): ?>
                                <option value="<?= $valor ?>" <?= ($antigos['tipo_vaga'] ?? '') === $valor ? 'selected' : '' ?>>
                                    <?= htmlspecialchars($rotulo) ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                        <?= erro($erros, 'tipo_vaga') ?>
                    </div>

                    <div class="campo">
                        <label>Contato do responsável *</label>
                        <input
                            type="text"
                            name="contato"
                            placeholder="Email ou telefone"
                            value="<?= old($antigos, 'contato') ?>"
                            required
                        >
                        <?= erro($erros, 'contato') ?>
                    </div>

                    <div class="campo">
                        <label>Data limite para candidatura *</label>
                        <input
                            type="datetime-local"
                            name="fechamento_vaga"
                            value="<?= old($antigos, 'fechamento_vaga') ?>"
                            required
                        >
                        <?= erro($erros, 'fechamento_vaga') ?>
                    </div>
                </div>

                <button type="submit">
                    Publicar vaga
                </button>

                <button type="submit" formaction="listavagas.php" formmethod="get">
                    Voltar
                </button>
            </form>
        </div>
    </div>

</div>
</body>
</html>
