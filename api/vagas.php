<?php

header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/conexao.php';
require_once __DIR__ . '/common.php';
require_once __DIR__ . '/../classes/Vaga.php';

$model = new Vaga($conn);
$entrada = lerEntrada();
$metodo = $_SERVER['REQUEST_METHOD'];
$id = isset($_GET['id']) ? (int) $_GET['id'] : (isset($entrada['id']) ? (int) $entrada['id'] : null);
$busca = trim($_GET['q'] ?? $_GET['search'] ?? '');

if ($metodo === 'GET') {
    $resultado = $model->listar($id, $busca);
    $vagas = [];
    while ($vaga = $resultado->fetch_assoc()) {
        $vaga['id_vaga'] = (int) $vaga['id_vaga'];
        $vaga['id_empresa'] = $vaga['id_empresa'] === null ? null : (int) $vaga['id_empresa'];
        $vaga['numero_vagas'] = $vaga['numero_vagas'] === null ? null : (int) $vaga['numero_vagas'];
        $vagas[] = $vaga;
    }

    if ($id !== null) {
        if (empty($vagas)) {
            responderJson(['success' => false, 'mensagem' => 'Vaga não encontrada.'], 404);
        }
        responderJson($vagas[0]);
    }

    responderJson($vagas);
}

if ($metodo === 'POST') {
    exigirUsuario($conn);
    $idEmpresa = (int) ($entrada['id_empresa'] ?? 0);
    $dados = [
        'id_empresa' => $idEmpresa > 0 ? $idEmpresa : null,
        'titulo' => trim($entrada['titulo'] ?? ''),
        'descricao' => trim($entrada['descricao'] ?? ''),
        'salario' => trim((string) ($entrada['salario'] ?? '')),
        'fechamento_vaga' => trim($entrada['fechamento_vaga'] ?? ''),
        'tipo_vaga' => trim($entrada['tipo_vaga'] ?? ''),
        'cidade' => trim($entrada['cidade'] ?? ''),
        'contato' => trim($entrada['contato'] ?? ''),
        'numero_vagas' => max(1, (int) ($entrada['numero_vagas'] ?? 1))
    ];

    if ($dados['titulo'] === '' || $dados['descricao'] === '' || $dados['salario'] === ''
        || $dados['fechamento_vaga'] === '' || $dados['tipo_vaga'] === '') {
        responderJson(['success' => false, 'mensagem' => 'Preencha todos os campos obrigatórios.'], 400);
    }

    $novoId = $model->inserir($dados);
    responderJson(['success' => true, 'mensagem' => 'Vaga cadastrada com sucesso.', 'id_vaga' => $novoId], 201);
}

if ($metodo === 'PUT') {
    exigirUsuario($conn);
    if ($id === null) {
        responderJson(['success' => false, 'mensagem' => 'Informe o id da vaga.'], 400);
    }
    if (!$model->atualizar($id, $entrada)) {
        responderJson(['success' => false, 'mensagem' => 'Nenhum campo válido foi enviado.'], 400);
    }
    responderJson(['success' => true, 'mensagem' => 'Vaga atualizada com sucesso.']);
}

if ($metodo === 'DELETE') {
    exigirUsuario($conn);
    if ($id === null) {
        responderJson(['success' => false, 'mensagem' => 'Informe o id da vaga.'], 400);
    }
    $model->excluir($id);
    responderJson(['success' => true, 'mensagem' => 'Vaga excluída com sucesso.']);
}

responderJson(['success' => false, 'mensagem' => 'Método não permitido.'], 405);
