<?php
//Credenciais do banco/API
$host = "172.29.20.169";
$usuario = "root";
$senha = "";
$banco = "db_estagiou";

$conn = new mysqli($host, $usuario, $senha, $banco, $porta);

if ($conn->connect_error) {
    die("Erro na conexão: " . $conn->connect_error);
}

$conn->set_charset('utf8');
?>