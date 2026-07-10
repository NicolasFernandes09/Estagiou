package br.ulbra.estagiou.classes;

import java.util.Locale;
import java.text.Normalizer;

public class VagaDados {
    public static final String EXTRA_VAGA_ID = "extra_vaga_id";

    public final String id;
    public final String sigla;
    public final String empresa;
    public final String titulo;
    public final String cidade;
    public final String tipo;
    public final String descricao;
    public final String salario;
    public final String contato;
    public final String dataLimite;
    public final String candidatura;

    public VagaDados(String id, String sigla, String empresa, String titulo, String cidade,
                     String tipo, String descricao, String salario, String contato,
                     String dataLimite, String candidatura) {
        this.id = id;
        this.sigla = sigla;
        this.empresa = empresa;
        this.titulo = titulo;
        this.cidade = cidade;
        this.tipo = tipo;
        this.descricao = descricao;
        this.salario = salario;
        this.contato = contato;
        this.dataLimite = dataLimite;
        this.candidatura = candidatura;
    }

    public boolean combinaComFiltro(String filtro) {
        return "Todas".equals(filtro) || tipo.equalsIgnoreCase(filtro);
    }

    public boolean combinaComBusca(String busca) {
        String termo = normalizar(busca);
        if (termo.isEmpty()) {
            return true;
        }

        String texto = normalizar(titulo + " " + empresa + " " + cidade + " " + tipo);
        return texto.contains(termo);
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        String semAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return semAcentos.toLowerCase(Locale.ROOT).trim();
    }
}
