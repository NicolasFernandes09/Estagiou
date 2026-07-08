package br.ulbra.estagiou.classes.model;

public class Usuario {
    private int usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private String usuarioSenha;

    public Usuario() {
    }

    public Usuario(int usuarioId, String usuarioNome, String usuarioEmail, String usuarioSenha) {
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.usuarioEmail = usuarioEmail;
        this.usuarioSenha = usuarioSenha;
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
}
