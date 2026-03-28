package br.com.contas.api;

import com.google.gson.annotations.SerializedName;

public class Cotacao {
    @SerializedName("EURBRL")
    private InformacaoMoeda eurToBrl;

    public InformacaoMoeda getEurToBrl() {
        return eurToBrl;
    }
}
