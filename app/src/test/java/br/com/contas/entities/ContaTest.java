package br.com.contas.entities;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class ContaTest {

    @Test
    public void setUsuarioId_deveAtualizarOCampoCorretamente() {
        Conta conta = new Conta("Aluguel", 1500.0, new Date(), 1L);

        conta.setUsuarioId(42L);

        assertEquals(Long.valueOf(42L), conta.getUsuarioId());
    }
}
