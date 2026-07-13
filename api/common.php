<?php

function responderJson($dados, $status = 200)
{
    http_response_code($status);
    echo json_encode($dados, JSON_UNESCAPED_UNICODE);
    exit;
}

function lerEntrada()
{
    $entrada = json_decode(file_get_contents('php://input'), true);
    if (!is_array($entrada)) {
        $entrada = [];
    }
    if (empty($entrada) && !empty($_POST)) {
        $entrada = $_POST;
    }
    return $entrada;
}

function garantirTabelaTokens($conn)
{
    $conn->query("CREATE TABLE IF NOT EXISTS api_tokens (
        id_token BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
        id_usuario INT NOT NULL,
        token_hash CHAR(64) NOT NULL UNIQUE,
        criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        expira_em DATETIME NOT NULL,
        INDEX idx_token_usuario (id_usuario),
        INDEX idx_token_expiracao (expira_em)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
}

function criarToken($conn, $idUsuario)
{
    garantirTabelaTokens($conn);
    $conn->query("DELETE FROM api_tokens WHERE expira_em <= NOW()");
    $removerAnteriores = $conn->prepare('DELETE FROM api_tokens WHERE id_usuario = ?');
    $removerAnteriores->bind_param('i', $idUsuario);
    $removerAnteriores->execute();
    $token = bin2hex(random_bytes(32));
    $hash = hash('sha256', $token);
    $stmt = $conn->prepare("INSERT INTO api_tokens (id_usuario, token_hash, expira_em) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 30 DAY))");
    $stmt->bind_param('is', $idUsuario, $hash);
    $stmt->execute();
    return $token;
}

function tokenBearer()
{
    $authorization = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    if ($authorization === '' && function_exists('getallheaders')) {
        $headers = getallheaders();
        $authorization = $headers['Authorization'] ?? $headers['authorization'] ?? '';
    }
    if (preg_match('/^Bearer\s+(.+)$/i', trim($authorization), $matches)) {
        return trim($matches[1]);
    }
    return '';
}

function exigirUsuario($conn)
{
    garantirTabelaTokens($conn);
    $token = tokenBearer();
    if ($token === '') {
        responderJson(['success' => false, 'mensagem' => 'Autenticação necessária.'], 401);
    }

    $hash = hash('sha256', $token);
    $stmt = $conn->prepare("SELECT id_usuario FROM api_tokens WHERE token_hash = ? AND expira_em > NOW() LIMIT 1");
    $stmt->bind_param('s', $hash);
    $stmt->execute();
    $resultado = $stmt->get_result()->fetch_assoc();

    if (!$resultado) {
        responderJson(['success' => false, 'mensagem' => 'Sessão inválida ou expirada.'], 401);
    }

    return (int) $resultado['id_usuario'];
}

function revogarToken($conn)
{
    $token = tokenBearer();
    if ($token === '') {
        return;
    }
    $hash = hash('sha256', $token);
    $stmt = $conn->prepare("DELETE FROM api_tokens WHERE token_hash = ?");
    $stmt->bind_param('s', $hash);
    $stmt->execute();
}
