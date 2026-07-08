<?php
require_once "configadmin.php";

$abasPermitidas = ["vagas", "usuarios", "empresas"];
$aba = $_GET["aba"] ?? "vagas";
if (!in_array($aba, $abasPermitidas)) {
    $aba = "vagas";
}

if (isset($_GET["excluir"]) && isset($_GET["id"])) {
    $id = (int) $_GET["id"];

    if ($aba === "vagas") {
        $stmt = $pdo->prepare("DELETE FROM vaga WHERE id_vaga = ?");
        $stmt->execute([$id]);
    } elseif ($aba === "usuarios") {
        $stmt = $pdo->prepare("DELETE FROM usuarios WHERE id_usuario = ?");
        $stmt->execute([$id]);
    } elseif ($aba === "empresas") {
        $stmt = $pdo->prepare("DELETE FROM empresas WHERE id_empresa = ?");
        $stmt->execute([$id]);
    }

    header("Location: index.php?aba=" . $aba);
    exit;
}

if ($aba === "vagas") {
    $sql = "SELECT vaga.id_vaga, vaga.titulo, vaga.tipo_vaga, vaga.salario,
                   vaga.fechamento_vaga, empresas.nome AS nome_empresa
            FROM vaga
            LEFT JOIN empresas ON empresas.id_empresa = vaga.id_empresa
            ORDER BY vaga.id_vaga DESC";
} elseif ($aba === "usuarios") {
    $sql = "SELECT id_usuario, nome, email FROM usuarios ORDER BY id_usuario DESC";
    $dados = $pdo->query($sql)->fetchAll(PDO::FETCH_ASSOC);
} elseif ($aba === "empresas") {
    $sql = "SELECT id_empresa, nome, email, telefone, endereco FROM empresas ORDER BY id_empresa DESC";
    $dados = $pdo->query($sql)->fetchAll(PDO::FETCH_ASSOC);
}
?>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Painel Admin - Vagas</title>
<link rel="stylesheet" href="administrador.css">
</head>
<body>


<button id="btnMenu" class="btn-hamburguer" aria-label="Abrir menu">
    <span></span>
    <span></span>
    <span></span>
</button>

<div class="layout">

    <aside id="sidebar" class="sidebar">
        <div class="sidebar-topo">
            <h1>Admin</h1>
            <p>Painel de controle</p>
        </div>

        <nav class="menu">
            <a href="index.php?aba=vagas" class="menu-item <?= $aba === 'vagas' ? 'ativo' : '' ?>">
                Vagas
            </a>
            <a href="index.php?aba=usuarios" class="menu-item <?= $aba === 'usuarios' ? 'ativo' : '' ?>">
                Usuários
            </a>
            <a href="index.php?aba=empresas" class="menu-item <?= $aba === 'empresas' ? 'ativo' : '' ?>">
                Empresas
            </a>
        </nav>
    </aside>

    <main class="conteudo">

        <?php if ($aba === "vagas"): ?>

            <h2>Todas as vagas</h2>
            <p class="subtitulo">Visualize e exclua as vagas cadastradas no sistema.</p>

            <div class="tabela-wrapper">
                <table class="tabela">
                    <thead>
                        <tr>
                            <th>Título</th>
                            <th>Empresa</th>
                            <th>Tipo</th>
                            <th>Salário</th>
                            <th>Fecha em</th>
                            <th>Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php if (empty($dados)): ?>
                            <tr><td colspan="6" class="vazio">Nenhuma vaga cadastrada.</td></tr>
                        <?php else: ?>
                            <?php foreach ($dados as $vaga): ?>
                                <tr>
                                    <td><?= htmlspecialchars($vaga['titulo']) ?></td>
                                    <td><?= htmlspecialchars($vaga['nome_empresa'] ?? '-') ?></td>
                                    <td><span class="tag"><?= htmlspecialchars($vaga['tipo_vaga']) ?></span></td>
                                    <td>R$ <?= number_format($vaga['salario'], 2, ',', '.') ?></td>
                                    <td><?= $vaga['fechamento_vaga'] ? date('d/m/Y', strtotime($vaga['fechamento_vaga'])) : '-' ?></td>
                                    <td>
                                        <a class="btn-excluir"
                                           href="index.php?aba=vagas&excluir=1&id=<?= $vaga['id_vaga'] ?>"
                                           onclick="return confirm('Excluir esta vaga?');">
                                           Excluir
                                        </a>
                                    </td>
                                </tr>
                            <?php endforeach; ?>
                        <?php endif; ?>
                    </tbody>
                </table>
            </div>

        <?php elseif ($aba === "usuarios"): ?>

            <h2>Todos os usuários</h2>
            <p class="subtitulo">Visualize e exclua os usuários cadastrados no sistema.</p>

            <div class="tabela-wrapper">
                <table class="tabela">
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php if (empty($dados)): ?>
                            <tr><td colspan="3" class="vazio">Nenhum usuário cadastrado.</td></tr>
                        <?php else: ?>
                            <?php foreach ($dados as $usuario): ?>
                                <tr>
                                    <td><?= htmlspecialchars($usuario['nome']) ?></td>
                                    <td><?= htmlspecialchars($usuario['email']) ?></td>
                                    <td>
                                        <a class="btn-excluir"
                                           href="index.php?aba=usuarios&excluir=1&id=<?= $usuario['id_usuario'] ?>"
                                           onclick="return confirm('Excluir este usuário?');">
                                           Excluir
                                        </a>
                                    </td>
                                </tr>
                            <?php endforeach; ?>
                        <?php endif; ?>
                    </tbody>
                </table>
            </div>

        <?php elseif ($aba === "empresas"): ?>

            <h2>Todas as empresas</h2>
            <p class="subtitulo">Visualize e exclua as empresas cadastradas no sistema.</p>

            <div class="tabela-wrapper">
                <table class="tabela">
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Telefone</th>
                            <th>Endereço</th>
                            <th>Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php if (empty($dados)): ?>
                            <tr><td colspan="5" class="vazio">Nenhuma empresa cadastrada.</td></tr>
                        <?php else: ?>
                            <?php foreach ($dados as $empresa): ?>
                                <tr>
                                    <td><?= htmlspecialchars($empresa['nome']) ?></td>
                                    <td><?= htmlspecialchars($empresa['email']) ?></td>
                                    <td><?= htmlspecialchars($empresa['telefone'] ?? '-') ?></td>
                                    <td><?= htmlspecialchars($empresa['endereco'] ?? '-') ?></td>
                                    <td>
                                        <a class="btn-excluir"
                                           href="index.php?aba=empresas&excluir=1&id=<?= $empresa['id_empresa'] ?>"
                                           onclick="return confirm('Excluir esta empresa?');">
                                           Excluir
                                        </a>
                                    </td>
                                </tr>
                            <?php endforeach; ?>
                        <?php endif; ?>
                    </tbody>
                </table>
            </div>

        <?php endif; ?>

    </main>
</div>

<script src="administrador.js"></script>
</body>
</html>