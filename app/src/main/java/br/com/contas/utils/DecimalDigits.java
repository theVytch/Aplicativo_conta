package br.com.contas.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DecimalDigits{

    public static String modeloFormatPattern = "#,##0.00";
    public static String idiomaCelular = "pt";


    public static String formatarNumero(Double numero) {
        DecimalFormatSymbols symbols = "en".equals(idiomaCelular)
                ? new DecimalFormatSymbols(Locale.US)
                : new DecimalFormatSymbols(new Locale("pt", "BR"));

        DecimalFormat df = new DecimalFormat(modeloFormatPattern, symbols);

        return df.format(numero);
    }

    public static void formatPattern(String idioma){
        if(idioma.equals("en")){
            modeloFormatPattern = "#,##0.00";
            idiomaCelular = idioma;
            return;
        }
        idiomaCelular = idioma;
        modeloFormatPattern = "#,##0.00";
    }
}
