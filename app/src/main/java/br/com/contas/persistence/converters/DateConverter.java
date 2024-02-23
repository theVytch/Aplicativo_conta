package br.com.contas.persistence.converters;

import androidx.room.TypeConverter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter  {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @TypeConverter
    public static Date fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            return format.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String toString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return format.format(date);
    }

    public static Date stringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Ou lançar uma exceção, dependendo do comportamento desejado
        }
    }

    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return format.format(date);
    }
}
