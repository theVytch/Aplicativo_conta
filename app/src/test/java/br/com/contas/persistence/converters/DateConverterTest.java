package br.com.contas.persistence.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateConverterTest {

    private Locale defaultLocale;

    @Before
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("pt", "BR"));
    }

    @After
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void toString_deveFormatarDataNoPadraoEsperado() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.MARCH, 28, 10, 30, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String dataFormatada = DateConverter.toString(calendar.getTime());

        assertEquals("28/03/2026", dataFormatada);
    }

    @Test
    public void fromString_deveConverterTextoEmData() {
        Date data = DateConverter.fromString("05/04/2026");

        assertNotNull(data);
        assertEquals("05/04/2026", DateConverter.toString(data));
    }

    @Test
    public void fromString_deveRetornarNullQuandoEntradaForInvalida() {
        assertNull(DateConverter.fromString("data-invalida"));
    }
}
