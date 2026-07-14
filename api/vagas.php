<?php

header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/conexao.php';
require_once __DIR__ . '/../classes/Vaga.php';

function dadosVaga($entrada, $atual = [])
{
    return [
        'id_empresa' => (int) ($entrada['id_empresa'] ?? $atual['id_empresa'] ?? 0),
        'titulo' => textoRecebido($entrada, 'titulo', $atual['titulo'] ?? ''),
        'descricao' => textoRecebido($entrada, 'descricao', $atual['descricao'] ?? ''),
        'salario' => textoRecebido($entrada, 'salario', $atual['salario'] ?? ''),
        'fechamento_vaga' => textoRecebido($entrada, 'fechamento_vaga', $atual['fechamento_vaga'] ?? ''),
        'tipo_vaga' => textoRecebido($entrada, 'tipo_vaga', $atual['tipo_vaga'] ?? ''),
        'contato' => textoRecebido($entrada, 'contato', $atual['contato'] ?? '')
    ];
}

function validarVaga($vaga)
{
    return $vaga['id_empresa'] > 0
        && $vaga['titulo'] !== ''
        && $vaga['descricao'] !== ''
        && is_numeric(str_replace(',', '.', $vaga['salario']))
        && $vaga['fechamento_vaga'] !== ''
        && $vaga['tipo_vaga'] !== '';
}

$model = new Vaga($conn);
$entrada = lerEntrada();
$metodo = strtoupper($_SERVER['REQUEST_METHOD']);
$id = (int) ($_GET['id_vaga'] ?? $_GET['id'] ?? $entrada['id_vaga'] ?? $entrada['id'] ?? 0);

try {
    if ($metodo === 'GET') {
        if ($id > 0) {
            $vaga = $model->buscarPorId($id);
            if (!$vaga) {
                responderJson(['success' => false, 'mensagem' => 'Vaga não encontrada.'], 404);
            }
            responderJson($vaga);
        }

        $busca = trim((string) ($_GET['q'] ?? $_GET['search'] ?? ''));
        responderJson($model->listar($busca));
    }

    if ($metodo === 'POST') {
        $vaga = dadosVaga($entrada);
        if (!validarVaga($vaga)) {
            responderJson(['success' => false, 'mensagem' => 'Preencha todos os campos obrigatórios da vaga.'], 400);
        }

        $idCriado = $model->cadastrar(
            $vaga['id_empresa'],
            $vaga['titulo'],
            $vaga['descricao'],
            str_replace(',', '.', $vaga['salario']),
            $vaga['fechamento_vaga'],
            $vaga['tipo_vaga'],
            $vaga['contato']
        );

        responderJson([
            'success' => true,
            'mensagem' => 'Vaga cadastrada com sucesso.',
            'id_vaga' => $idCriado
        ], 201);
    }

    if ($metodo === 'PUT') {
        $atual = $model->buscarPorId($id);
        if (!$atual) {
            responderJson(['success' => false, 'mensagem' => 'Vaga não encontrada.'], 404);
        }

        $vaga = dadosVaga($entrada, $atual);
        if (!validarVaga($vaga)) {
            responderJson(['success' => false, 'mensagem' => 'Os dados da vaga são inválidos.'], 400);
        }

        $model->atualizar(
            $id,
            $vaga['id_empresa'],
            $vaga['titulo'],
            $vaga['descricao'],
            str_replace(',', '.', $vaga['salario']),
            $vaga['fechamento_vaga'],
            $vaga['tipo_vaga'],
            $vaga['contato']
        );

        responderJson(['success' => true, 'mensagem' => 'Vaga atualizada com sucesso.']);
    }

    if ($metodo === 'DELETE') {
        if (!$model->buscarPorId($id)) {
            responderJson(['success' => false, 'mensagem' => 'Vaga não encontrada.'], 404);
        }

        $model->excluir($id);
        responderJson(['success' => true, 'mensagem' => 'Vaga excluída com sucesso.']);
    }

    responderJson(['success' => false, 'mensagem' => 'Método não permitido.'], 405);
} catch (Throwable $erro) {
    registrarErroApi($erro);
    responderJson([
        'success' => false,
        'mensagem' => 'A API encontrou um erro ao processar as vagas.'
    ], 500);
}
