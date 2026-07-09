<?php
session_start();

require_once __DIR__ . '/api/conexao.php';

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
        'titulo'           => trim($_POST['titulo'] ?? ''),
        'tipo_contratacao' => trim($_POST['tipo_contratacao'] ?? ''),
        'cidade'           => trim($_POST['cidade'] ?? ''),
        'salario'          => trim($_POST['salario'] ?? ''),
        'data_limite'      => trim($_POST['data_limite'] ?? ''),
        'descricao'        => trim($_POST['descricao'] ?? ''),
    ];

    if ($dados['titulo'] === '') {
        $erros['titulo'] = 'Informe o título da vaga.';
    }

    if ($dados['tipo_contratacao'] === '') {
        $erros['tipo_contratacao'] = 'Selecione o tipo de contratação.';
    }

    if ($dados['cidade'] === '') {
        $erros['cidade'] = 'Informe a cidade da vaga.';
    }

    if ($dados['descricao'] === '') {
        $erros['descricao'] = 'Informe a descrição da vaga.';
    }

    if ($dados['data_limite'] === '') {
        $erros['data_limite'] = 'Informe a data limite para candidaturas.';
    }

    if (empty($erros)) {
        try {
            $sql = "INSERT INTO vagas (titulo, tipo_contratacao, cidade, salario, descricao, data_publicacao, data_limite, empresa_id) 
                    VALUES (:titulo, :tipo_contratacao, :cidade, :salario, :descricao, CURDATE(), :data_limite, :empresa_id)";

            $stmt = $conn->prepare($sql);
            $stmt->execute([
                ':titulo'           => $dados['titulo'],
                ':tipo_contratacao' => $dados['tipo_contratacao'],
                ':cidade'           => $dados['cidade'],
                ':salario'          => $dados['salario'] !== '' ? $dados['salario'] : null,
                ':descricao'        => $dados['descricao'],
                ':data_limite'      => $dados['data_limite'],
                ':empresa_id'       => $_SESSION['empresa_id'] ?? null
            ]);

            $_SESSION['sucesso'] = 'Concluído com Sucesso!';
            header('Location: listavagas.php');
            exit;
        } catch (Exception $e) {
            $erros['geral'] = 'Não foi possível publicar a vaga. Tente novamente.';
        }
    }

    $_SESSION['erros']   = $erros;
    $_SESSION['antigos'] = $dados;
    header('Location: criarvagas.php');
    exit;
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Anunciar Nova Vaga</title>
    <link rel="stylesheet" href="criarvagas.css">
</head>
<body>
<div class="tela">

    <div class="lado-marca">
        <div class="marca-texto">
            <h1>Encontre talentos</h1>
            <p>Preencha os detalhes e publique uma nova oportunidade no mural do site</p>
        </div>
    </div>

    <div class="lado-formulario">
        <div class="conteudo">
            
            <div class="cabecalho-conteudo">
                <div class="textos-cabecalho">
                    <h2>Publicar Vaga</h2>
                </div>
                <img src="img/logo.png" alt="Logo" class="logo-topo">
            </div>

            <?php if (!empty($erros['geral'])): ?>
                <div class="alerta alerta-erro"><?= htmlspecialchars($erros['geral'], ENT_QUOTES, 'UTF-8') ?></div>
            <?php endif; ?>

            <form action="criarvagas.php" method="POST" novalidate>
                <div class="grade">
                    
                    <div class="campo campo-largo">
                        <label>Título da Vaga *</label>
                        <input 
                            type="text" 
                            name="titulo" 
                            placeholder="Ex: Desenvolvedor Front-End Júnior"
                            value="<?= old($antigos, 'titulo') ?>"
                            required
                        >
                        <?= erro($erros, 'titulo') ?>
                    </div>

                    <div class="campo">
                        <label>Tipo de Contratação *</label>
                        <select name="tipo_contratacao" required>
                            <option value="">Selecione...</option>
                            <option value="estagio" <?= old($antigos, 'tipo_contratacao') === 'estagio' ? 'selected' : '' ?>>Estágio</option>
                            <option value="jovem_aprendiz" <?= old($antigos, 'tipo_contratacao') === 'jovem_aprendiz' ? 'selected' : '' ?>>Jovem Aprendiz</option>
                            <option value="clt" <?= old($antigos, 'tipo_contratacao') === 'clt' ? 'selected' : '' ?>>CLT</option>
                            <option value="freelancer" <?= old($antigos, 'tipo_contratacao') === 'freelancer' ? 'selected' : '' ?>>Freelancer</option>
                        </select>
                        <?= erro($erros, 'tipo_contratacao') ?>
                    </div>

                    <div class="campo">
                        <label>Cidade *</label>
                        <input 
                            type="text" 
                            name="cidade" 
                            placeholder="Ex: São Paulo - SP"
                            value="<?= old($antigos, 'cidade') ?>"
                            required
                        >
                        <?= erro($erros, 'cidade') ?>
                    </div>

                    <div class="campo">
                        <label>Salário / Remuneração</label>
                        <input 
                            type="text" 
                            name="salario" 
                            placeholder="Ex: R$ 2.500,00 ou A Combinar"
                            value="<?= old($antigos, 'salario') ?>"
                        >
                    </div>

                    <div class="campo">
                        <label>Data Limite para Inscrição *</label>
                        <input 
                            type="date" 
                            name="data_limite" 
                            value="<?= old($antigos, 'data_limite') ?>"
                            required
                        >
                        <?= erro($erros, 'data_limite') ?>
                    </div>

                    <div class="campo campo-largo">
                        <label>Descrição da Vaga *</label>
                        <input 
                            type="text" 
                            name="descricao" 
                            placeholder="Ex: Atuar no desenvolvimento de interfaces utilizando HTML, CSS e JavaScript..." 
                            value="<?= old($antigos, 'descricao') ?>"
                            required
                        >
                        <?= erro($erros, 'descricao') ?>
                    </div>

                </div>

                <div class="botoes-acoes" style="display: flex; gap: 16px; margin-top: 20px; width: 100%; max-width: 656px;">
                    <button type="submit" style="margin-top: 0;">Publicar Vaga</button>
                    
                    <a href="listavagas.php" style="text-decoration: none; width: 100%; max-width: 320px;">
                        <button type="button" style="margin-top: 0; width: 100%;">Voltar para a Lista</button>
                    </a>
                </div>

            </form>
            
        </div>
    </div>
</div>
</body>
</html>