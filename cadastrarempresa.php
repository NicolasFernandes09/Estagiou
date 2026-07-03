<?php
session_start();

/**
 * Redireciona de volta para o formulário levando os erros
 * e os valores digitados (exceto senha e arquivo).
 */
function voltarComErro(array $erros, array $antigos): void
{
    $_SESSION['erros']   = $erros;
    $_SESSION['antigos'] = $antigos;
    header('Location: index.php');
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Location: index.php');
    exit;
}

// ---------- 1. Captura dos dados enviados ----------
$nome      = trim($_POST['nome_empresa'] ?? '');
$telefone  = trim($_POST['telefone'] ?? '');
$email     = trim($_POST['email'] ?? '');
$senha     = $_POST['senha'] ?? '';
$endereco  = trim($_POST['endereco'] ?? '');


$antigos = [
    'nome_empresa' => $nome,
    'telefone'     => $telefone,
    'email'        => $email,
    'endereco'     => $endereco,
 
];

$erros = [];

// ---------- 2. Validação dos campos obrigatórios ----------
// (endereco e contato_rh são opcionais, todos os demais são obrigatórios)
if ($nome === '') {
    $erros['nome_empresa'] = 'Informe o nome da empresa.';
}

if ($telefone === '') {
    $erros['telefone'] = 'Informe o telefone.';
} else {
    // Remove tudo que não for dígito e valida um telefone brasileiro válido:
    // DDD (2 dígitos, não pode começar em 0) + fixo (8 dígitos) ou celular (9 dígitos, começando em 9)
    $telefoneDigitos = preg_replace('/\D/', '', $telefone);
    if (!preg_match('/^[1-9]{2}(9\d{8}|[2-8]\d{7})$/', $telefoneDigitos)) {
        $erros['telefone'] = 'Telefone inválido. Use um número válido com DDD, ex: (51) 99999-9999.';
    }
}

if ($email === '') {
    $erros['email'] = 'Informe o e-mail.';
} elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $erros['email'] = 'Informe um e-mail válido.';
}

if ($senha === '') {
    $erros['senha'] = 'Informe uma senha.';
} elseif (strlen($senha) < 6) {
    $erros['senha'] = 'A senha deve ter pelo menos 6 caracteres.';
}

// ---------- 3. Upload de imagem (obrigatório) ----------
$logoConteudo = null;

if (!isset($_FILES['logo']) || $_FILES['logo']['error'] === UPLOAD_ERR_NO_FILE) {
    $erros['logo'] = 'O logo da empresa é obrigatório.';
} elseif ($_FILES['logo']['error'] !== UPLOAD_ERR_OK) {
    $erros['logo'] = 'Ocorreu um erro no upload da imagem. Tente novamente.';
} else {
    $tiposPermitidos = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
    $tamanhoMaximo   = 5 * 1024 * 1024; // 5 MB
    $mimeReal        = mime_content_type($_FILES['logo']['tmp_name']);

    if (!in_array($mimeReal, $tiposPermitidos, true)) {
        $erros['logo'] = 'Formato de imagem inválido. Envie um arquivo JPG, PNG, GIF ou WEBP.';
    } elseif ($_FILES['logo']['size'] > $tamanhoMaximo) {
        $erros['logo'] = 'A imagem deve ter no máximo 5MB.';
    } else {
        $logoConteudo = file_get_contents($_FILES['logo']['tmp_name']);
    }
}

if (!empty($erros)) {
    voltarComErro($erros, $antigos);
}

// ---------- 4. Conexão com o banco ----------
$conexao = new mysqli(
    "LugarDoBancoDeDados",
    "LugarDoBancoDeDados",
    "LugarDoBancoDeDados",
    "LugarDoBancoDeDados"
);

if ($conexao->connect_error) {
    voltarComErro(['geral' => 'Não foi possível conectar ao banco de dados no momento.'], $antigos);
}

$conexao->set_charset('utf8mb4');

// ---------- 5. Verifica se já existe empresa com o mesmo nome ----------
$sql = $conexao->prepare('SELECT id FROM empresas WHERE nome_empresa = ? LIMIT 1');
$sql->bind_param('s', $nome);
$sql->execute();
$sql->store_result();

if ($sql->num_rows > 0) {
    $sql->close();
    $conexao->close();
    voltarComErro(['nome_empresa' => 'Já existe uma empresa cadastrada com este nome.'], $antigos);
}
$sql->close();

// ---------- 6. Insere no banco ----------
$senhaHash = password_hash($senha, PASSWORD_DEFAULT);

$insert = $conexao->prepare('
    INSERT INTO empresas(
        nome_empresa,
        telefone,
        email,
        logo_empresa,
        senha,
        endereco,
        
    )
    VALUES (?,?,?,?,?,?)
');

// Envia a imagem como "blob" para suportar arquivos maiores com segurança
$null            = null;
$enderecoValor   = $endereco !== '' ? $endereco : null;


$insert->bind_param(
    'sssbsss',
    $nome,
    $telefone,
    $email,
    $null,
    $senhaHash,
    $enderecoValor,
    
);
$insert->send_long_data(3, $logoConteudo);

if ($insert->execute()) {
    $insert->close();
    $conexao->close();
    $_SESSION['sucesso'] = true;
    header('Location: index.php');
    exit;
}

// Trata erro de duplicidade (condição de corrida) ou outro erro do banco
if ($conexao->errno === 1062) {
    $erros = ['nome_empresa' => 'Já existe uma empresa cadastrada com este nome ou e-mail.'];
} else {
    $erros = ['geral' => 'Não foi possível concluir o cadastro no momento. Tente novamente mais tarde.'];
}

$insert->close();
$conexao->close();
voltarComErro($erros, $antigos);