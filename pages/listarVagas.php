<?php
session_start();

require_once __DIR__ . '/../api/conexao.php';
require_once __DIR__ . '/../classes/Vaga.php';

if (!isset($_SESSION['usuario_tipo'])) {
    header('Location: login.php');
    exit;
}

$sucesso = $_SESSION['sucesso'] ?? '';
unset($_SESSION['sucesso']);

$busca = trim($_GET['busca'] ?? '');
$tipo  = trim($_GET['tipo'] ?? 'todas');

$idEmpresaFiltro = $_SESSION['usuario_tipo'] === 'empresa' ? (int) $_SESSION['empresa_id'] : 0;

$vagaModel = new Vaga($conn);
$resultado = $vagaModel->buscarVagas($busca, $tipo, $idEmpresaFiltro);
$vagas     = $resultado ? $resultado->fetch_all(MYSQLI_ASSOC) : [];

function iniciais($nome) {
    $palavras = explode(' ', trim($nome));
    $ini = strtoupper(substr($palavras[0], 0, 1));
    if (count($palavras) > 1) {
        $ini .= strtoupper(substr(end($palavras), 0, 1));
    }
    return $ini;
}
?>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8">
  <title>Mural de Oportunidades — Vagas</title>
  <link rel="stylesheet" href="../assets/css/base.css">
  <link rel="stylesheet" href="../assets/css/appShell.css">
  <link rel="stylesheet" href="../assets/css/cards.css">
</head>
<body>

<button id="btnMenu" class="btn-hamburguer" aria-label="Abrir menu">
    <span></span>
    <span></span>
    <span></span>
</button>

<div class="app">

    <div id="overlay" class="overlay"></div>

     <aside id="sidebar" class="sidebar">
      <h1>Vagas</h1>
      <div class="subtitulo">Painel de vagas</div>
      <nav>
        <a href="listarVagas.php" class="ativo">Início</a>
        <?php if ($_SESSION['usuario_tipo'] === 'empresa'): ?>
          <a href="postarVaga.php">Postar vaga</a>
        <?php endif; ?>
        <?php if ($_SESSION['usuario_tipo'] === 'admin'): ?>
          <a href="administrador.php">Painel Admin</a>
        <?php endif; ?>
        <a href="logout.php">Sair</a>
      </nav>
    </aside>

    <main class="conteudo">
      <?php if ($_SESSION['usuario_tipo'] === 'empresa'): ?>
        <h2>Minhas vagas</h2>
        <p class="subtitulo">Vagas publicadas pela sua empresa.</p>
      <?php else: ?>
        <h2>Feed de vagas</h2>
        <p class="subtitulo">Confira as oportunidades disponíveis no momento.</p>
      <?php endif; ?>

      <form method="GET" class="busca">
        <span>🔍</span>
        <input type="text" name="busca" placeholder="Buscar vaga ou empresa"
               value="<?= htmlspecialchars($busca) ?>">
      </form>

      <div class="filtros">
        <?php
        $opcoes = [
            'todas' => 'Todas',
            'estagio' => 'Estágio',
            'jovem_aprendiz' => 'Jovem Aprendiz',
            'clt' => 'CLT',
            'freelancer' => 'Freelancer',
        ];
        foreach ($opcoes as $valor => $rotulo):
            $ativo = $tipo === $valor ? 'ativo' : '';
        ?>
          <a href="?tipo=<?= urlencode($valor) ?>&busca=<?= urlencode($busca) ?>"
             class="pill <?= $ativo ?>" style="text-decoration:none;">
            <?= htmlspecialchars($rotulo) ?>
          </a>
        <?php endforeach; ?>
      </div>

      <div class="grid-vagas">
        <?php if (empty($vagas)): ?>
          <p>Nenhuma vaga encontrada.</p>
        <?php endif; ?>

        <?php foreach ($vagas as $vaga): ?>
          <div class="card-vaga">
            <div class="topo">
              <div class="avatar">
                <?php if (!empty($vaga['empresa_logo'])): ?>
                  <img src="../<?= htmlspecialchars($vaga['empresa_logo']) ?>" alt="">
                <?php else: ?>
                  <?= htmlspecialchars(iniciais($vaga['empresa_nome'])) ?>
                <?php endif; ?>
              </div>
              <div>
                <div class="empresa"><?= htmlspecialchars($vaga['empresa_nome']) ?></div>
                <div class="cargo"><?= htmlspecialchars($vaga['titulo']) ?></div>
              </div>
            </div>

            <div class="rodape-card">
              <span class="badge"><?= htmlspecialchars($opcoes[$vaga['tipo_vaga']] ?? $vaga['tipo_vaga']) ?></span>
              <a class="link-detalhes" href="vaga.php?id=<?= (int) $vaga['id_vaga'] ?>">Ver detalhes ›</a>
            </div>
          </div>
        <?php endforeach; ?>
      </div>

    </main>
  </div>

<script src="../assets/js/adminSidebar.js"></script>
</body>
</html>
