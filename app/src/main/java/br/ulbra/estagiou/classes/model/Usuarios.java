package br.ulbra.estagiou.classes.model;

import com.google.gson.annotations.SerializedName;

public class Usuarios {

    @SerializedName("id_usuario")
    private int usuarioId;

    @SerializedName("nome")
    private String usuarioNome;

    @SerializedName("email")
    private String usuarioEmail;

    @SerializedName("senha")
    private String usuarioSenha;

    @SerializedName("foto")
    private String foto;

    public Usuarios() {
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public String getUsuarioSenha() {
        return usuarioSenha;
    }

    public void setUsuarioSenha(String usuarioSenha) {
        this.usuarioSenha = usuarioSenha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}