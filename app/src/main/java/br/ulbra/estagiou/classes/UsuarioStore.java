package br.ulbra.estagiou.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class UsuarioStore {
    private static final String PREFS_NAME = "usuarios_estagiou";

    public static class UsuarioDados {
        public final String nome;
        public final String usuario;
        public final String email;
        public final String descricaoProfissional;
        public final String descricaoPessoal;
        public final String foto;

        public UsuarioDados(String nome, String usuario, String email,
                            String descricaoProfissional, String descricaoPessoal, String foto) {
            this.nome = nome;
            this.usuario = usuario;
            this.email = email;
            this.descricaoProfissional = descricaoProfissional;
            this.descricaoPessoal = descricaoPessoal;
            this.foto = foto;
        }
    }

    public static void salvarUsuario(Context context, String nome, String usuario, String email,
                                     String descricaoProfissional, String descricaoPessoal, String foto) {
        SharedPreferences.Editor editor = prefs(context).edit();
        String chave = chave(usuario);
        editor.putString(chave + "nome", nome);
        editor.putString(chave + "email", email);
        editor.putString(chave + "descricao_profissional", descricaoProfissional);
        editor.putString(chave + "descricao_pessoal", descricaoPessoal);
        editor.putString(chave + "foto", foto);
        editor.apply();
    }

    public static void salvarUsuarioSeAusente(Context context, String usuario, String email) {
        UsuarioDados dados = buscarUsuario(context, usuario);
        if (dados.nome.equals("") && dados.email.equals("")) {
            salvarUsuario(context, usuario, usuario, email, "", "", "");
        }
    }

    public static UsuarioDados buscarUsuario(Context context, String usuario) {
        SharedPreferences preferences = prefs(context);
        String chave = chave(usuario);
        return new UsuarioDados(
                preferences.getString(chave + "nome", ""),
                usuario,
                preferences.getString(chave + "email", ""),
                preferences.getString(chave + "descricao_profissional", ""),
                preferences.getString(chave + "descricao_pessoal", ""),
                preferences.getString(chave + "foto", "")
        );
    }

    public static void atualizarPerfil(Context context, String usuario, String nome, String email,
                                       String descricaoProfissional, String descricaoPessoal, String foto) {
        salvarUsuario(context, nome, usuario, email, descricaoProfissional, descricaoPessoal, foto);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String chave(String usuario) {
        return "usuario_" + usuario + "_";
    }
}
