package br.ulbra.estagiou.model;

public class Vagas {
    private int vagasId;
    private String titulo;
    private String descricao;
    private double salario;
    private String fechamentoData;
    private String tipoVaga;

    public Vagas() {
    }

    public Vagas(int vagasId, String titulo, String descricao, double salario, String fechamentoData, String tipoVaga) {
        this.vagasId = vagasId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.salario = salario;
        this.fechamentoData = fechamentoData;
        this.tipoVaga = tipoVaga;
    }

    public int getVagasId() {
        return vagasId;
    }

    public void setVagasId(int vagasId) {
        this.vagasId = vagasId;
    }

    public String getFechamentoData() {
        return fechamentoData;
    }

    public void setFechamentoData(String fechamentoData) {
        this.fechamentoData = fechamentoData;
    }

    public String getTipoVaga() {
        return tipoVaga;
    }

    public void setTipoVaga(String tipoVaga) {
        this.tipoVaga = tipoVaga;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}