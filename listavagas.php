<?php
session_start();

require_once __DIR__ . '/api/conexao.php';

$sucesso = $_SESSION['sucesso'] ?? '';
unset($_SESSION['sucesso']);

$busca = $_GET['busca'] ?? '';
$tipo  = $_GET['tipo'] ?? 'todas';

$sql = "SELECT v.id, v.titulo, v.tipo_contratacao, v.cidade, v.data_publicacao, v.data_limite,
               e.nome AS empresa_nome, e.logo AS empresa_logo
        FROM vagas v
        INNER JOIN empresas e ON e.id = v.empresa_id
        WHERE v.data_limite >= CURDATE()";

$params = [];

if ($busca !== '') {
    $sql .= " AND (v.titulo LIKE :busca1 OR e.nome LIKE :busca2)";
    $params[':busca1'] = "%$busca%";
    $params[':busca2'] = "%$busca%";
}

require_once __DIR__ . '/api/conexao.php';
require_once __DIR__ . '/classes/Vaga.php';

$busca = trim($_GET['busca'] ?? '');
$tipo  = trim($_GET['tipo'] ?? 'todas');

$vagaModel   = new Vaga($conn);
$resultado   = $vagaModel->buscarVagas($busca, $tipo);
$vagas       = $resultado ? $resultado->fetch_all(MYSQLI_ASSOC) : [];

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
  <link rel="stylesheet" href="listavagas.css">
</head>
<body>
  <div class="app">

     <aside class="sidebar">
      <h1>Vagas</h1>
      <div class="subtitulo">Painel de vagas</div>
      <nav>
        <a href="index.php" class="ativo">Início</a>
        <a href="cadastro.php">Cadastro</a>
        <a href="login.php">Login</a>
        <a href="vagas.php">Vagas</a>
      </nav>
    </aside>

    <main class="conteudo">
      <h2>Feed de vagas</h2>
      <p class="descricao">Confira as oportunidades disponíveis no momento.</p>

      <form method="GET" class="busca">
        <span>🔍</span>
        <input type="text" name="busca" placeholder="Buscar vaga, empresa ou cidade"
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
                  <img src="uploads/logos/<?= htmlspecialchars($vaga['empresa_logo']) ?>" alt="">
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
</body>
</html>