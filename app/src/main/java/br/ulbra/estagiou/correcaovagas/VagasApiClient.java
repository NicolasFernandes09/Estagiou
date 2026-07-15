package br.ulbra.estagiou.correcaovagas;

import org.json.JSONObject;

private VagaDados criarVaga(JSONObject item) {
    String empresa = primeiroTexto(item, "empresa", "nome_empresa", "nomeEmpresa", "razao_social", "razaoSocial", "nome");
    String titulo = primeiroTexto(item, "titulo", "cargo", "vaga", "nome_vaga", "nomeVaga");
    String cidade = primeiroTexto(item, "cidade", "local", "localizacao", "localização", "municipio", "município");
    String tipo = primeiroTexto(item, "tipoVaga", "tipo_vaga", "tipo", "tipo_contratacao", "contratacao", "contratação");
    String descricao = primeiroTexto(item, "descricao", "descrição", "descricao_vaga", "descricaoVaga");
    String contato = primeiroTexto(item, "contato", "email", "email_contato", "emailContato", "email_rh", "emailRh");
    String telefone = primeiroTexto(item, "telefone", "fone", "celular", "whatsapp", "telefone_rh", "telefoneRh");
    String dataLimite = primeiroTexto(item, "fechamento_vaga", "fechamentoData", "fechamento_data", "dataLimite", "data_limite", "data_limite_inscricao", "prazo");
    String candidatura = primeiroTexto(item, "candidatura", "como_candidatar", "comoCandidatar", "instrucoes", "instruções");
    String id = primeiroTexto(item, "id", "id_vaga", "idVaga", "vagasId", "vagas_id", "codigo", "código");
    String sigla = primeiroTexto(item, "sigla", "iniciais");
    String salario = primeiroTexto(item, "salario", "salário", "remuneracao", "remuneração");
    String vagasDisponiveis = primeiroTexto(item, "numero_vagas", "numeroVagas", "quantidade_vagas", "quantidadeVagas", "vagas_disponiveis", "vagasDisponiveis");
    String fotoEmpresa = primeiroTexto(item, "foto_empresa", "fotoEmpresa", "logo", "logo_empresa", "logoEmpresa", "imagem_empresa", "imagemEmpresa", "foto");

    if (empresa.equals("")) {
        empresa = "Empresa";
    }
    if (titulo.equals("")) {
        titulo = "Vaga disponível";
    }
    if (cidade.equals("")) {
        cidade = "Cidade não informada";
    }
    if (tipo.equals("")) {
        tipo = "Estágio";
    }
    if (descricao.equals("")) {
        descricao = "Descrição não informada pela empresa.";
    }
    if (!vagasDisponiveis.equals("")) {
        descricao = descricao + "\nVagas disponíveis: " + vagasDisponiveis;
    }
    salario = salarioFormatado(salario, item);
    if (salario.equals("")) {
        salario = "Salário não informado";
    }
    if (contato.equals("")) {
        contato = "Contato não informado";
    }
    if (!contato.toLowerCase(Locale.ROOT).startsWith("contato")) {
        contato = "Contato: " + contato;
    }
    if (telefone.equals("")) {
        telefone = "Telefone não informado";
    } else if (!telefone.toLowerCase(Locale.ROOT).startsWith("telefone")) {
        telefone = "Telefone: " + telefone;
    }
    if (dataLimite.equals("")) {
        dataLimite = "Data limite não informada";
    } else if (!dataLimite.toLowerCase(Locale.ROOT).startsWith("inscri")) {
        dataLimite = "Inscrições até " + dataLimite;
    }
    if (candidatura.equals("")) {
        candidatura = "Envie seu currículo conforme o contato informado pela empresa.";
    }
    if (sigla.equals("")) {
        sigla = gerarSigla(empresa);
    }
    if (id.equals("")) {
        id = gerarId(titulo + "_" + empresa + "_" + cidade + "_" + tipo);
    }

    return new VagaDados(id, sigla, empresa, titulo, cidade, tipo, descricao, salario, contato, telefone, dataLimite, candidatura, fotoEmpresa);
}