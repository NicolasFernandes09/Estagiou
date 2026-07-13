<?php
$host = getenv('DB_HOST') ?: 'localhost';
$usuario = getenv('DB_USER') ?: 'root';
$senha = getenv('DB_PASSWORD') ?: '';
$banco = getenv('DB_NAME') ?: 'db_estagiou';
$porta = (int) (getenv('DB_PORT') ?: 3306);

mysqli_report(MYSQLI_REPORT_OFF);
$conn = @new mysqli($host, $usuario, $senha, $banco, $porta);

if ($conn->connect_error) {
    http_response_code(500);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(['success' => false, 'mensagem' => 'Não foi possível conectar ao banco de dados.'], JSON_UNESCAPED_UNICODE);
    exit;
}

$conn->set_charset('utf8mb4');
