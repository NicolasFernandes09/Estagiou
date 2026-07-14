<?php

require_once __DIR__ . '/common.php';

$host = getenv('DB_HOST') ?: '127.0.0.1';
$usuario = getenv('DB_USER') ?: 'root';
$senha = getenv('DB_PASSWORD') ?: '';
$banco = getenv('DB_NAME') ?: 'db_estagiou';
$porta = (int) (getenv('DB_PORT') ?: 3306);

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $conn = new mysqli($host, $usuario, $senha, $banco, $porta);
    $conn->set_charset('utf8mb4');
} catch (Throwable $erro) {
    registrarErroApi($erro);
    responderJson([
        'success' => false,
        'mensagem' => 'Não foi possível conectar ao banco de dados.'
    ], 500);
}
