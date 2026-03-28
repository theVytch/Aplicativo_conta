package br.com.contas.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsValidaTest {

    @Test
    public void validaCampoPreenchido_deveRetornarTrueQuandoNomeEValorForemValidos() {
        assertTrue(UtilsValida.validaCampoPreenchido("Mercado", 25.0));
    }

    @Test
    public void validaCampoPreenchido_deveRetornarFalseQuandoNomeForNuloOuVazio() {
        assertFalse(UtilsValida.validaCampoPreenchido(null, 25.0));
        assertFalse(UtilsValida.validaCampoPreenchido("", 25.0));
        assertFalse(UtilsValida.validaCampoPreenchido("   ", 25.0));
    }

    @Test
    public void validaCampoPreenchido_deveRetornarFalseQuandoValorForNuloOuMenorIgualAZero() {
        assertFalse(UtilsValida.validaCampoPreenchido("Mercado", null));
        assertFalse(UtilsValida.validaCampoPreenchido("Mercado", 0.0));
        assertFalse(UtilsValida.validaCampoPreenchido("Mercado", -1.0));
    }
}
