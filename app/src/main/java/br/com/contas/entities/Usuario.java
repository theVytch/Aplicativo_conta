package br.com.contas.entities;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Usuario implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    @NotNull
    private String nome;
    @NotNull
    private Double saldo;

    public Usuario(String nome, Double saldo) {
        this.nome = nome;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
}
