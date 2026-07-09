<?php
session_start();

require_once __DIR__ . '/api/conexao.php';
require_once __DIR__ . '/classes/Vaga.php';
require_once __DIR__ . '/classes/Usuario.php';
require_once __DIR__ . '/classes/Empresas.php';

if (($_SESSION['usuario_tipo'] ?? null) !== 'admin') {
    header('Location: login.php');
    exit;
}

$vagaModel = new Vaga($conn);
$usuarioModel = new Usuario($conn);
$empresaModel = new Empresas($conn);

$abasPermitidas = ["vagas", "usuarios", "empresas"];
$aba = $_GET["aba"] ?? "vagas";
if (!in_array($aba, $abasPermitidas)) {
    $aba = "vagas";
}

$erros = [];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_POST['editar_vaga_id'])) {
        $id = (int) $_POST['editar_vaga_id'];
        $titulo = trim($_POST['titulo'] ?? '');
        $descricao = trim($_POST['descricao'] ?? '');
        $salario = trim($_POST['salario'] ?? '');
        $fechamento = trim($_POST['fechamento_vaga'] ?? '');
        $tipo = trim($_POST['tipo_vaga'] ?? '');
        $contato = trim($_POST['contato'] ?? '');

        if ($titulo === '' || $descricao === '' || $salario === '' || $fechamento === '' || $tipo === '' || $contato === '') {
            $erros['geral'] = 'Preencha todos os campos da vaga.';
        } else {
            $vagaModel->atualizar($id, $titulo, $descricao, $salario, $fechamento, $tipo, $contato);
            header('Location: administrador.php?aba=vagas');
            exit;
        }
    } elseif (isset($_POST['editar_usuario_id'])) {
        $id = (int) $_POST['editar_usuario_id'];
        $nome = trim($_POST['nome'] ?? '');
        $email = trim($_POST['email'] ?? '');

        if ($nome === '' || $email === '' || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $erros['geral'] = 'Preencha nome e e-mail válidos para o usuário.';
        } else {
            $usuarioModel->atualizarUsuario($id, $nome, $email);
            header('Location: administrador.php?aba=usuarios');
            exit;
        }
    }
}

if (isset($_GET["excluir"]) && isset($_GET["id"])) {
    $id = (int) $_GET["id"];

    if ($aba === "vagas") {
        $vagaModel->deletar($id);
    } elseif ($aba === "usuarios") {
        $usuarioModel->deletarUsuario($id);
    } elseif ($aba === "empresas") {
        $empresaModel->deletar($id);
    }

    header("Location: administrador.php?aba=" . $aba);
    exit;
}

$editando = null;
if (isset($_GET["editar"])) {
    $idEditar = (int) $_GET["editar"];

    if ($aba === "vagas") {
        $editando = $vagaModel->buscarPorId($idEditar);
    } elseif ($aba === "usuarios") {
        $editando = $usuarioModel->lerPorIdUsuario($idEditar);
    }
}

if ($aba === "vagas") {
    $sql = "SELECT vaga.id_vaga, vaga.titulo, vaga.tipo_vaga, vaga.salario, vaga.contato,
                   vaga.fechamento_vaga, empresas.nome AS nome_empresa
            FROM vaga
            LEFT JOIN empresas ON empresas.ID_empresa = vaga.id_empresa
            ORDER BY vaga.id_vaga DESC";
    $dados = $conn->query($sql)->fetch_all(MYSQLI_ASSOC);
} elseif ($aba === "usuarios") {
    $sql = "SELECT ID_usuario AS id_usuario, nome, email FROM usuarios ORDER BY ID_usuario DESC";
    $dados = $conn->query($sql)->fetch_all(MYSQLI_ASSOC);
} elseif ($aba === "empresas") {
    $sql = "SELECT ID_empresa AS id_empresa, nome, email, telefone, endereco FROM empresas ORDER BY ID_empresa DESC";
    $dados = $conn->query($sql)->fetch_all(MYSQLI_ASSOC);
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

    <div id="overlay" class="overlay"></div>

    <aside id="sidebar" class="sidebar">
        <div class="sidebar-topo">
            <h1>Admin</h1>
            <p>Painel de controle</p>
        </div>

        <nav class="menu">
            <a href="administrador.php?aba=vagas" class="menu-item <?= $aba === 'vagas' ? 'ativo' : '' ?>">
                Vagas
            </a>
            <a href="administrador.php?aba=usuarios" class="menu-item <?= $aba === 'usuarios' ? 'ativo' : '' ?>">
                Usuários
            </a>
            <a href="administrador.php?aba=empresas" class="menu-item <?= $aba === 'empresas' ? 'ativo' : '' ?>">
                Empresas
            </a>
            <a href="logout.php" class="menu-item">
                Sair
            </a>
        </nav>
    </aside>

    <main class="conteudo">

        <?php if (!empty($erros['geral'])): ?>
            <div class="alerta alerta-erro"><?= htmlspecialchars($erros['geral'], ENT_QUOTES, 'UTF-8') ?></div>
        <?php endif; ?>

        <?php if ($aba === "vagas"): ?>

            <h2>Todas as vagas</h2>
            <p class="subtitulo">Visualize, edite e exclua as vagas cadastradas no sistema.</p>

            <?php if ($editando): ?>
                <div class="form-edicao">
                    <h3>Editar vaga</h3>
                    <form method="POST" action="administrador.php?aba=vagas">
                        <input type="hidden" name="editar_vaga_id" value="<?= (int) $editando['id_vaga'] ?>">

                        <label>Título</label>
                        <input type="text" name="titulo" value="<?= htmlspecialchars($editando['titulo'], ENT_QUOTES, 'UTF-8') ?>" required>

                        <label>Descrição</label>
                        <input type="text" name="descricao" value="<?= htmlspecialchars($editando['descricao'], ENT_QUOTES, 'UTF-8') ?>" required>

                        <label>Salário</label>
                        <input type="number" step="0.01" name="salario" value="<?= htmlspecialchars($editando['salario'], ENT_QUOTES, 'UTF-8') ?>" required>

                        <label>Fechamento</label>
                        <input type="datetime-local" name="fechamento_vaga" value="<?= date('Y-m-d\TH:i', strtotime($editando['fechamento_vaga'])) ?>" required>

                        <label>Tipo</label>
                        <input type="text" name="tipo_vaga" value="<?= htmlspecialchars($editando['tipo_vaga'], ENT_QUOTES, 'UTF-8') ?>" required>

                        <label>Contato do responsável</label>
                        <input type="text" name="contato" value="<?= htmlspecialchars($editando['contato'], ENT_QUOTES, 'UTF-8') ?>" required>

                        <div class="acoes-form">
                            <button type="submit" class="btn-salvar">Salvar</button>
                            <a href="administrador.php?aba=vagas" class="btn-cancelar">Cancelar</a>
                        </div>
                    </form>
                </div>
            <?php endif; ?>

            <div class="tabela-wrapper">
                <table class="tabela">
                    <thead>
                        <tr>
                            <th>Título</th>
                            <th>Empresa</th>
                            <th>Tipo</th>
                            <th>Salário</th>
                            <th>Contato</th>
                            <th>Fecha em</th>
                            <th>Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php if (empty($dados)): ?>
                            <tr><td colspan="7" class="vazio">Nenhuma vaga cadastrada.</td></tr>
                        <?php else: ?>
                            <?php foreach ($dados as $vaga): ?>
                                <tr>
                                    <td><?= htmlspecialchars($vaga['titulo']) ?></td>
                                    <td><?= htmlspecialchars($vaga['nome_empresa'] ?? '-') ?></td>
                                    <td><span class="tag"><?= htmlspecialchars($vaga['tipo_vaga']) ?></span></td>
                                    <td>R$ <?= number_format($vaga['salario'], 2, ',', '.') ?></td>
                                    <td><?= htmlspecialchars($vaga['contato']) ?></td>
                                    <td><?= $vaga['fechamento_vaga'] ? date('d/m/Y', strtotime($vaga['fechamento_vaga'])) : '-' ?></td>
                                    <td class="col-acoes">
                                        <a class="btn-editar"
                                           href="administrador.php?aba=vagas&editar=<?= $vaga['id_vaga'] ?>">
                                           Editar
                                        </a>
                                        <a class="btn-excluir"
                                           href="administrador.php?aba=vagas&excluir=1&id=<?= $vaga['id_vaga'] ?>"
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
            <p class="subtitulo">Visualize, edite e exclua os usuários cadastrados no sistema.</p>

            <?php if ($editando): ?>
                <div class="form-edicao">
                    <h3>Editar usuário</h3>
                    <form method="POST" action="administrador.php?aba=usuarios">
                        <input type="hidden" name="editar_usuario_id" value="<?= (int) $editando['id_usuario'] ?>">

                        <label>Nome</label>
                        <input type="text" name="nome" value="<?= htmlspecialchars($editando['nome'], ENT_QUOTES, 'UTF-8') ?>" required>

                        <label>Email</label>
                        <input type="email" name="email" value="<?= htmlspecialchars($editando['email'], ENT_QUOTES, 'UTF-8') ?>" required>

                        <div class="acoes-form">
                            <button type="submit" class="btn-salvar">Salvar</button>
                            <a href="administrador.php?aba=usuarios" class="btn-cancelar">Cancelar</a>
                        </div>
                    </form>
                </div>
            <?php endif; ?>

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
                                    <td class="col-acoes">
                                        <a class="btn-editar"
                                           href="administrador.php?aba=usuarios&editar=<?= $usuario['id_usuario'] ?>">
                                           Editar
                                        </a>
                                        <a class="btn-excluir"
                                           href="administrador.php?aba=usuarios&excluir=1&id=<?= $usuario['id_usuario'] ?>"
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
                                           href="administrador.php?aba=empresas&excluir=1&id=<?= $empresa['id_empresa'] ?>"
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

<script src="script.js"></script>
</body>
</html>
