package br.com.contas.api;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InformacaoMoeda {
    private String name;
    private String high;
    private String low;
    private String bid;

    @SerializedName("create_date")
    private String createDate;

    public String getName() {
        return name;
    }

    public String getHigh() {
        return retornaValorFormato(high);
    }

    public String getLow() {
        return retornaValorFormato(low);
    }

    public String getBid() {
        return retornaValorFormato(bid);
    }

    @NonNull
    private String retornaValorFormato(String bidEntrada) {
        double valorDouble = Double.parseDouble(bidEntrada);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(valorDouble);
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getDataFormatada(){
        String dataFormatada;
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date data = formatoEntrada.parse(createDate);
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy");
            dataFormatada = formatoSaida.format(data);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return dataFormatada;
    }
}
