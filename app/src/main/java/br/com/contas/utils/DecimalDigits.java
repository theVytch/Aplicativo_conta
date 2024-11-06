package br.com.contas.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DecimalDigits{

    public static String modeloFormatPattern;
    public static String idiomaCelular;


    public static String formatarNumero(Double numero) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        //DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        DecimalFormat df = new DecimalFormat(modeloFormatPattern, symbols);

        return df.format(numero);
    }

    public static void formatPattern(String idioma){
        if(idioma.equals("en")){
            modeloFormatPattern = "#,###0.00";
            idiomaCelular = idioma;
            return;
        }
        idiomaCelular = idioma;
        modeloFormatPattern = "#,##0.00";
    }
}

