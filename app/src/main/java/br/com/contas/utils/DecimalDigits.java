package br.com.contas.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DecimalDigits{

    public static String formatarNumero(Double numero) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

        return df.format(numero);
    }
}

