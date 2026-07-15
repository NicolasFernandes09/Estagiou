<?php
session_start();

require_once __DIR__ . '/../api/conexao.php';
require_once __DIR__ . '/../classes/Vaga.php';
require_once __DIR__ . '/../classes/Empresas.php';

if (!isset($_SESSION['usuario_tipo'])) {
    header('Location: login.php');
    exit;
}

$id = (int) ($_GET['id'] ?? 0);

$vagaModel = new Vaga($conn);
$vaga = $id > 0 ? $vagaModel->buscarPorId($id) : null;

$empresa = null;
if ($vaga && !empty($vaga['id_empresa'])) {
    $empresaModel = new Empresas($conn);
    $empresa = $empresaModel->buscarPorId($vaga['id_empresa']);
}

function iniciais(string $nome): string
{
    $palavras = explode(' ', trim($nome));
    $ini = strtoupper(substr($palavras[0], 0, 1));
    if (count($palavras) > 1) {
        $ini .= strtoupper(substr(end($palavras), 0, 1));
    }
    return $ini;
}

$opcoesTipo = [
    'estagio'        => 'Estágio',
    'jovem_aprendiz' => 'Jovem Aprendiz',
    'clt'            => 'CLT',
    'freelancer'     => 'Freelancer',
];

$empresaNome = $empresa['nome'] ?? 'Empresa não informada';

require_once('./dompdf/autoload.inc.php');

use Dompdf\Dompdf;

// Verificar se foi solicitado exportar para PDF
if (isset($_GET['export']) && $_GET['export'] === 'pdf') {
    $html = <<<'HTML'
    <!DOCTYPE html>
    <html lang="pt-br">
    <head>
        <meta charset="UTF-8">
        <title></title>
        <style>
            body { font-family: Arial, sans-serif; padding: 20px; }
            .card-vaga { border: 1px solid #ccc; padding: 20px; border-radius: 8px; }
            .topo { display: flex; gap: 15px; margin-bottom: 20px; }
            .avatar { font-size: 32px; font-weight: bold; min-width: 60px; }
            .empresa { font-size: 14px; color: #666; }
            .cargo { font-size: 20px; font-weight: bold; }
            .badge { display: inline-block; background: #f0f0f0; padding: 8px 12px; border-radius: 4px; margin: 5px 5px 5px 0; font-size: 14px; }
            .rodape-card { margin: 15px 0; }
        </style>
    </head>
    <body>
HTML;

    if ($vaga) {
        $html .= '<div class="card-vaga">';
        $html .= '<div class="topo">';
        
        if (!empty($empresa['logo'])) {
            $html .= '<div class="avatar"><img src="' . htmlspecialchars($empresa['logo']) . '" alt="" style="width: 60px; height: 60px; border-radius: 4px;"></div>';
        } else {
            $html .= '<div class="avatar">' . htmlspecialchars(iniciais($empresaNome)) . '</div>';
        }
        
        $html .= '<div>';
        $html .= '<div class="empresa">' . htmlspecialchars($empresaNome) . '</div>';
        $html .= '<div class="cargo">' . htmlspecialchars($vaga['titulo']) . '</div>';
        $html .= '</div></div>';
        
        $html .= '<p style="line-height: 1.5;">' . nl2br(htmlspecialchars($vaga['descricao'])) . '</p>';
        
        $html .= '<div class="rodape-card">';
        $html .= '<span class="badge">' . htmlspecialchars($opcoesTipo[$vaga['tipo_vaga']] ?? $vaga['tipo_vaga']) . '</span>';
        $html .= '<span class="badge">R$ ' . number_format((float) $vaga['salario'], 2, ',', '.') . '</span>';
        $html .= '<span class="badge">Até ' . htmlspecialchars(date('d/m/Y H:i', strtotime($vaga['fechamento_vaga']))) . '</span>';
        $html .= '</div>';
        
        if (!empty($vaga['contato'])) {
            $html .= '<div style="padding-top: 12px; border-top: 1px solid #ccc; font-size: 14px; color: #666;">';
            $html .= '<strong>Contato:</strong> ' . htmlspecialchars($vaga['contato']);
            $html .= '</div>';
        }
        
        $html .= '</div>';
    }
    
    $html .= '</body></html>';

    $dompdf = new Dompdf();
    $dompdf->loadHtml($html);
    $dompdf->setPaper('A4', 'portrait');
    $dompdf->render();
    $dompdf->stream('documento.pdf');
    exit;
}

// Mostrar página normal
?>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8">
  <title><?= $vaga ? htmlspecialchars($vaga['titulo']) : 'Vaga não encontrada' ?> — Vagas</title>
  <link rel="stylesheet" href="../assets/css/base.css">
  <link rel="stylesheet" href="../assets/css/appShell.css">
  <link rel="stylesheet" href="../assets/css/cards.css">
</head>
<body>
  <div class="app">

    <aside class="sidebar">
      <h1>Vagas</h1>
      <div class="subtitulo">Painel de vagas</div>
      <nav>
        <a href="listarVagas.php">Início</a>
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
      <a class="link-detalhes" href="listarVagas.php">‹ Voltar para o feed</a>

      <?php if (!$vaga): ?>
        <h2 style="margin-top:16px;">Vaga não encontrada</h2>
        <p class="subtitulo">A vaga que você procura não existe ou foi removida.</p>
      <?php else: ?>
        <div class="card-vaga" style="margin-top:16px; max-width:720px;">
          <div class="topo">
            <div class="avatar">
              <?php if (!empty($empresa['logo'])): ?>
                <img src="../<?= htmlspecialchars($empresa['logo']) ?>" alt="">
              <?php else: ?>
                <?= htmlspecialchars(iniciais($empresaNome)) ?>
              <?php endif; ?>
            </div>
            <div>
              <div class="empresa"><?= htmlspecialchars($empresaNome) ?></div>
              <div class="cargo" style="font-size:20px;"><?= htmlspecialchars($vaga['titulo']) ?></div>
            </div>
          </div>

          <p style="color:var(--text-principal); line-height:1.5;">
            <?= nl2br(htmlspecialchars($vaga['descricao'])) ?>
          </p>

          <div class="rodape-card" style="flex-wrap:wrap; gap:10px;">
            <span class="badge"><?= htmlspecialchars($opcoesTipo[$vaga['tipo_vaga']] ?? $vaga['tipo_vaga']) ?></span>
            <span class="badge">R$ <?= number_format((float) $vaga['salario'], 2, ',', '.') ?></span>
            <span class="badge">Até <?= htmlspecialchars(date('d/m/Y H:i', strtotime($vaga['fechamento_vaga']))) ?></span>
          </div>

          <?php if (!empty($vaga['contato'])): ?>
            <div style="padding-top:12px; border-top:1px solid var(--borda); font-size:14px; color:var(--text-secundario);">
              <strong style="color:var(--text-principal);">Contato:</strong> <?= htmlspecialchars($vaga['contato']) ?>
            </div>
          <?php endif; ?>

          <div style="margin-top:16px; padding-top:16px; border-top:1px solid var(--borda);">
            <a href="?id=<?= $id ?>&export=pdf" class="btn-pdf" style="display:inline-block; padding:10px 20px; background-color:#0066cc; color:white; text-decoration:none; border-radius:4px; font-weight:bold;">
               Converter para PDF
            </a>
          </div>
        </div>
      <?php endif; ?>
    </main>
  </div>
</body>
</html>
