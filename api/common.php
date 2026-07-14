<?php

function responderJson($dados, $status = 200)
{
    http_response_code($status);
    echo json_encode($dados, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

function lerEntrada()
{
    $entrada = [];
    $conteudo = file_get_contents('php://input');

    if ($conteudo !== false && trim($conteudo) !== '') {
        $json = json_decode($conteudo, true);
        if (is_array($json)) {
            $entrada = $json;
        }
    }

    if (!empty($_POST)) {
        $entrada = array_merge($entrada, $_POST);
    }

    return $entrada;
}

function acaoRecebida($entrada)
{
    return strtolower(trim((string) ($entrada['action'] ?? $entrada['acao'] ?? '')));
}

function textoRecebido($entrada, $chave, $padrao = '')
{
    return trim((string) ($entrada[$chave] ?? $padrao));
}

function registrarErroApi($erro)
{
    error_log('[Estagiou API] ' . $erro->getMessage());
}
