package br.com.contas.api;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;

public class Cotacao {
    @SerializedName("EURBRL")
    private InformacaoMoeda eurToBrl;

    public InformacaoMoeda getEurToBrl() {
        return eurToBrl;
    }
}
