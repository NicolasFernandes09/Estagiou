<?php
// config.php - conexão com o banco de dados

$host = "localhost";
$dbname = "vagas_db";
$usuario = "root";
$senha = "";

try {
    $pdo = new PDO(
        "mysql:host=$host;dbname=$dbname;charset=utf8",
        $usuario,
        $senha
    );
    // Faz o PDO avisar quando der erro, em vez de falhar silenciosamente
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Erro ao conectar no banco: " . $e->getMessage());
}