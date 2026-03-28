package br.com.contas.repository;

import java.util.ArrayList;
import java.util.List;

import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;

public class DashboardData {

    private final Usuario usuario;
    private final List<Conta> contas;
    private final Double saldoNecessario;
    private final Double saldoDesnecessario;

    public DashboardData(Usuario usuario, List<Conta> contas, Double saldoNecessario, Double saldoDesnecessario) {
        this.usuario = usuario;
        this.contas = contas;
        this.saldoNecessario = saldoNecessario;
        this.saldoDesnecessario = saldoDesnecessario;
    }

    public static DashboardData empty() {
        return new DashboardData(null, new ArrayList<>(), null, null);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<Conta> getContas() {
        return contas;
    }

    public Double getSaldoNecessario() {
        return saldoNecessario;
    }

    public Double getSaldoDesnecessario() {
        return saldoDesnecessario;
    }
}
