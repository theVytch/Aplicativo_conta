package br.com.contas.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

import br.com.contas.persistence.converters.DateConverter;

@Entity
@TypeConverters(DateConverter.class)
public class Conta implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "conta_id")
    private Long id;
    @NotNull
    private String nomeConta;
    @NotNull
    private Double valor;
    @NotNull
    private Date data;
    private Long usuarioId;
    private String tipo;
    private String necessidadeGasto;

    public Conta(String nomeConta, Double valor, Date data, Long usuarioId) {
        this.nomeConta = nomeConta;
        this.valor = valor;
        this.data = data;
        this.usuarioId = usuarioId;
        this.tipo = "SAIDA";
        this.necessidadeGasto = "NECESSARIO";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getNomeConta() {
        return nomeConta;
    }

    public void setNomeConta(@NotNull String nome) {
        this.nomeConta = nome;
    }

    @NotNull
    public Double getValor() {
        return valor;
    }

    public void setValor(@NotNull Double valor) {
        this.valor = valor;
    }

    @NotNull
    public Date getData() {
        return data;
    }

    public void setData(@NotNull Date data) {
        this.data = data;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuario) {
        this.usuarioId = usuarioId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNecessidadeGasto() {
        return necessidadeGasto;
    }

    public void setNecessidadeGasto(String necessidadeGasto) {
        this.necessidadeGasto = necessidadeGasto;
    }

    @Override
    public String toString() {
        return "Nome: " + nomeConta + ",           Valor: " + valor +", Data: " + DateConverter.dateToString(data);
    }
}
