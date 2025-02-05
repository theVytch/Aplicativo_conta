package br.com.contas.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("EUR-BRL")
    Call<Cotacao> getCotacao();
}
