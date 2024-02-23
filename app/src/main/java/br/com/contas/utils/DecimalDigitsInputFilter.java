package br.com.contas.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.text.DecimalFormat;

public class DecimalDigitsInputFilter implements InputFilter {

    private final int decimalDigits;

    public DecimalDigitsInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String destText = dest.toString();
        String sourceText = source.toString();

        // Verifica se a entrada é um número decimal
        if (TextUtils.isEmpty(sourceText) || sourceText.matches("[0-9.,]+")) {
            // Verifica se já existe um ponto decimal
            if (destText.contains(".")) {
                int indexOfDot = destText.indexOf(".");
                // Verifica se a posição do ponto decimal está dentro do limite de dígitos decimais permitidos
                if (dend - indexOfDot > decimalDigits) {
                    return "";
                }
            }
            return null; // Aceita a entrada
        } else {
            return ""; // Rejeita a entrada
        }
    }
}

