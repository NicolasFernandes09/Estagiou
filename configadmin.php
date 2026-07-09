<?php

$host = "172.29.20.89";
$usuario = "root";
$senha = "";
$banco = "db_estagiou";

try {
    $pdo = new PDO(
        "mysql:host=$host;dbname=$banco;charset=utf8",
        $usuario,
        $senha
    );
    
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Erro ao conectar no banco: " . $e->getMessage());
}