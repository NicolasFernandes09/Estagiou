<?php

header("Content-Type: application/json");

include "conexao.php";

$sql = "SELECT
E.nome AS empresa,
V.titulo,
V.descricao,
V.salario,
E.telefone,
V.fechamento_vaga,
E.logo,
V.tipo_vaga
FROM VAGA V
INNER JOIN Empresas E
ON E.ID_empresa = V.ID_Empresa";

$result = $conn->query($sql);

$vagas = array();

while($linha = $result->fetch_assoc()){
    $vagas[] = $linha;
}

echo json_encode($vagas, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

$conn->close();

?>