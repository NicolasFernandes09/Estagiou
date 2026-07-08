<?php
/**
 * Arquivo de conexão com o banco de dados.
 * Ajuste host, nome do banco, usuário e senha para os valores reais do seu ambiente.
 */

$host   = 'localhost';
$dbname = 'nome_do_banco';
$user   = 'usuario_do_banco';
$pass   = 'senha_do_banco';

$dsn = "mysql:host=$host;dbname=$dbname;charset=utf8mb4";

$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false, // mais seguro/rápido; por isso evitamos placeholder repetido no index.php
];

try {
    $pdo = new PDO($dsn, $user, $pass, $options);
} catch (PDOException $e) {
    // Em produção, não exiba o erro real na tela — logue e mostre uma mensagem genérica.
    die('Erro ao conectar ao banco de dados: ' . $e->getMessage());
}