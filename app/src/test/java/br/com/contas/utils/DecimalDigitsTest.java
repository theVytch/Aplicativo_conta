package br.com.contas.utils;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

public class DecimalDigitsTest {

    @After
    public void tearDown() {
        DecimalDigits.formatPattern("pt");
    }

    @Test
    public void formatarNumero_deveUsarPadraoBrasileiroPorPadrao() {
        DecimalDigits.formatPattern("pt");

        String valorFormatado = DecimalDigits.formatarNumero(1234.56);

        assertEquals("1.234,56", valorFormatado);
    }

    @Test
    public void formatPattern_deveTrocarPadraoQuandoIdiomaForIngles() {
        DecimalDigits.formatPattern("en");

        String valorFormatado = DecimalDigits.formatarNumero(1234.56);

        assertEquals("1,234.56", valorFormatado);
    }
}
