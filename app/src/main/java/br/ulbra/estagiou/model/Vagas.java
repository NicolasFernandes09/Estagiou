package br.ulbra.estagiou.model;

public class Vagas {
    private int vagasId;
    private String titulo;
    private String descricao;
    private double salario;
    private String fechamento_vaga;
    private String tipo_vaga;
    private String empresa;
    private String telefone;

    public Vagas() {
    }

    public Vagas(int vagasId, String titulo, String descricao, double salario, String fechamento_vaga, String tipo_vaga, String empresa, String telefone) {
        this.vagasId = vagasId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.salario = salario;
        this.fechamento_vaga = fechamento_vaga;
        this.tipo_vaga = tipo_vaga;
        this.empresa = empresa;
        this.telefone = telefone;
    }

    public int getVagasId() {
        return vagasId;
    }

    public void setVagasId(int vagasId) {
        this.vagasId = vagasId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getFechamento_vaga() {
        return fechamento_vaga;
    }

    public void setFechamento_vaga(String fechamento_vaga) {
        this.fechamento_vaga = fechamento_vaga;
    }

    public String getTipo_vaga() {
        return tipo_vaga;
    }

    public void setTipo_vaga(String tipo_Vaga) {
        this.tipo_vaga = tipo_Vaga;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}