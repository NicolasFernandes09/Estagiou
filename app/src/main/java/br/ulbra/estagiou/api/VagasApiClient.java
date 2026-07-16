package br.ulbra.estagiou.api;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.repository.SessaoManager;

public class VagasApiClient {
    public interface Callback {
        void onSuccess(List<VagaDados> vagas);

        void onError(String mensagem);
    }

    public void buscarVagas(Context context, Callback callback) {
        SessaoManager.inicializar(context);
        Call<ResponseBody> chamada = RetrofitClient.getApiService().buscarVagas();
        chamada.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Resposta inválida da API");
                    return;
                }

                try {
                    List<VagaDados> vagas = converterResposta(response.body().string());
                    callback.onSuccess(vagas);
                } catch (Exception e) {
                    callback.onError("Resposta inválida da API");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Não foi possível conectar à API");
            }
        });
    }

    List<VagaDados> converterResposta(String response) throws Exception {
        List<VagaDados> vagas = new ArrayList<>();
        String texto = response == null ? "" : response.trim();

        if (texto.equals("")) {
            return vagas;
        }

        JSONArray array;
        if (texto.startsWith("[")) {
            array = new JSONArray(texto);
        } else {
            JSONObject objeto = new JSONObject(texto);
            if (objeto.has("success") && !objeto.optBoolean("success", false)) {
                throw new IllegalArgumentException(objeto.optString("mensagem", "Resposta inválida da API"));
            }
            array = objeto.optJSONArray("vagas");
            if (array == null) {
                array = objeto.optJSONArray("dados");
            }
            if (array == null) {
                array = objeto.optJSONArray("data");
            }
            if (array == null) {
                array = new JSONArray();
                if (!objeto.has("success")) {
                    array.put(objeto);
                }
            }
        }

        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.optJSONObject(i);
            if (item != null) {
                vagas.add(criarVaga(item));
            }
        }

        return vagas;
    }

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

        return new VagaDados(
                id,
                sigla,
                empresa,
                titulo,
                cidade,
                tipo,
                descricao,
                salario,
                contato,
                telefone,
                dataLimite,
                candidatura,
                ApiConfig.resolverUrlArquivo(fotoEmpresa)
        );
    }

    private String primeiroTexto(JSONObject item, String... nomes) {
        for (String nome : nomes) {
            String valor = item.optString(nome, "").trim();
            if (!valor.equals("") && !valor.equalsIgnoreCase("null")) {
                return valor;
            }
        }
        return "";
    }

    private String salarioFormatado(String salario, JSONObject item) {
        if (salario != null && !salario.trim().equals("")) {
            String valor = salario.trim();
            if (valor.toLowerCase(Locale.ROOT).contains("r$")
                    || valor.toLowerCase(Locale.ROOT).startsWith("sal")) {
                return valor;
            }
            if (valor.matches("^[0-9]+([,.][0-9]+)?$")) {
                double numero = Double.parseDouble(valor.replace(",", "."));
                return formatarSalario(numero);
            }
            return "Salário: " + valor;
        }

        double valor = item.optDouble("salario", Double.NaN);
        if (Double.isNaN(valor)) {
            valor = item.optDouble("salário", Double.NaN);
        }
        if (Double.isNaN(valor)) {
            return "";
        }
        return formatarSalario(valor);
    }

    private String formatarSalario(double valor) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
        DecimalFormat formato = new DecimalFormat("#,##0.00", simbolos);
        return "Salário: R$ " + formato.format(valor);
    }

    private String gerarSigla(String empresa) {
        String[] partes = empresa.trim().split("\\s+");
        StringBuilder sigla = new StringBuilder();
        for (String parte : partes) {
            if (!parte.equals("")) {
                sigla.append(parte.substring(0, 1).toUpperCase(Locale.ROOT));
            }
            if (sigla.length() == 2) {
                break;
            }
        }
        if (sigla.length() == 0) {
            return "VG";
        }
        return sigla.toString();
    }

    private String gerarId(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String id = semAcentos.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        return id.equals("") ? "vaga" : id;
    }
}
