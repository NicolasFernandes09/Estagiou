package br.ulbra.estagiou.classes;

import androidx.appcompat.app.AppCompatActivity;

public class Vagas {
 private String titulo;
 private String descricao;
 private double salario;
 private String fechamentoData;
 private String tipoVaga;

    public Vagas() {
    }

    public Vagas(String titulo, String descricao, double salario, String fechamentoData, String tipoVaga) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.salario = salario;
        this.fechamentoData = fechamentoData;
        this.tipoVaga = tipoVaga;
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
}
