package br.com.contas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.TextView;

import br.com.contas.R;
import br.com.contas.api.ApiClient;
import br.com.contas.api.ApiService;
import br.com.contas.api.Cotacao;
import br.com.contas.utils.Ordenar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityTelaSobreConta extends AppCompatActivity {

    private static final String ARQUIVO =
            "br.edu.eduardomelentovytch.aplicativoContas.PREFERENCIA_ORDENACAO";
    private static final String ARQUIVO_SIZE =
            "br.edu.eduardomelentovytch.aplicativoContas.PREFERENCIA_ORDENACAO_SIZE";
    private static final String ORDENACAO = "ORDENACAO";
    private static final String ORDENACAO_SIZE = "ORDENACAO_SIZE";
    private String opcao = Ordenar.opcaoOrdenacao;
    private TextView textViewSobre;
    //private static TextView textViewCotacao;
    private RadioButton radioButtonSmall;
    private RadioButton radioButtonMiddle;
    private RadioButton radioButtonLarge;
    private RadioButton radioButtonBlind;
    private String tamanhoSelecionado = Ordenar.radio_size;
    //private static Cotacao cotacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_sobre_conta);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iniciarComponentes();
        retornaPreferenciaSize();

        //chamaApi(getResources());

        exibirBotaoVoltar();
    }



    /*private static void chamaApi(Resources resources) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        apiService.getCotacao().enqueue(new Callback<Cotacao>() {
            @Override
            public void onResponse(Call<Cotacao> call, Response<Cotacao> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //Cotacao cotacao = response.body();
                    cotacao = response.body();
                    String linha = "     " + cotacao.getEurToBrl().getName()
                            + " - " + resources.getString(R.string.oferta) + " " + cotacao.getEurToBrl().getBid()
                            + " - " +  resources.getString(R.string.alta) + " " + cotacao.getEurToBrl().getHigh()
                            + " - " +  resources.getString(R.string.baixa) + " " + cotacao.getEurToBrl().getLow();
                    textViewCotacao.setText(linha);
                    textViewCotacao.setSelected(true);
                } else {
                    Log.e("API_RESPONSE", "Erro: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Cotacao> call, Throwable t) {
                textViewCotacao.setText(R.string.api_erro);
                textViewCotacao.setSelected(true);
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        mudarTelaInicial();
    }

    private void iniciarComponentes(){
        textViewSobre = findViewById(R.id.textViewSobre);
        textViewSobre.setMovementMethod(new ScrollingMovementMethod());

        radioButtonSmall = findViewById(R.id.radioButtonSmall);
        radioButtonMiddle = findViewById(R.id.radioButtonMiddle);
        radioButtonLarge = findViewById(R.id.radioButtonLarge);
        radioButtonBlind = findViewById(R.id.radioButtonBlind);

        //textViewCotacao = findViewById(R.id.textViewCotacao);
    }

    private void exibirBotaoVoltar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ordenacao, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (opcao) {
            case "orderDataDesc":
                menu.findItem(R.id.itemOrdenarDataDesc).setChecked(true);
                return true;
            case "orderDataAsc":
                menu.findItem(R.id.itemOrdenarDataAsc).setChecked(true);
                return true;
            case "orderNomeDesc":
                menu.findItem(R.id.itemOrdenarNomeDesc).setChecked(true);
                return true;
            case "orderNomeAsc":
                menu.findItem(R.id.itemOrdenarNomeAsc).setChecked(true);
                return true;
            case "orderValorDesc":
                menu.findItem(R.id.itemOrdenarValorDesc).setChecked(true);
                return true;
            case "orderValorAsc":
                menu.findItem(R.id.itemOrdenarValorAsc).setChecked(true);
                return true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.itemOrdenarDataDesc) {
            salvarPreferencia("orderDataDesc");
        } else if (id == R.id.itemOrdenarDataAsc) {
            salvarPreferencia("orderDataAsc");
        } else if (id == R.id.itemOrdenarNomeDesc) {
            salvarPreferencia("orderNomeDesc");
        } else if (id == R.id.itemOrdenarNomeAsc) {
            salvarPreferencia("orderNomeAsc");
        } else if (id == R.id.itemOrdenarValorDesc) {
            salvarPreferencia("orderValorDesc");
        } else if (id == R.id.itemOrdenarValorAsc) {
            salvarPreferencia("orderValorAsc");
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void salvarPreferencia(String novaOpcao) {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(ORDENACAO, novaOpcao);
        editor.commit();
        opcao = novaOpcao;
    }

    private void retornaPreferencia() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        Ordenar.opcaoOrdenacao = shared.getString(ORDENACAO, opcao);
    }

    private void salvarPreferenciaSize() {
        if (radioButtonSmall.isChecked()) {
            tamanhoSelecionado = "Small";
        } else if (radioButtonMiddle.isChecked()) {
            tamanhoSelecionado = "Middle";
        } else if (radioButtonLarge.isChecked()) {
            tamanhoSelecionado = "Large";
        } else {
            tamanhoSelecionado = "Blind";
        }

        SharedPreferences shared = getSharedPreferences(ARQUIVO_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(ORDENACAO_SIZE, tamanhoSelecionado);
        editor.commit();
        Ordenar.radio_size = tamanhoSelecionado;
    }

    private void retornaPreferenciaSize() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO_SIZE, Context.MODE_PRIVATE);
        tamanhoSelecionado = shared.getString(ORDENACAO_SIZE, tamanhoSelecionado);

        if (tamanhoSelecionado.equals("Small")) {
            radioButtonSmall.setChecked(true);
        } else if (tamanhoSelecionado.equals("Middle")) {
            radioButtonMiddle.setChecked(true);
        } else if (tamanhoSelecionado.equals("Large")) {
            radioButtonLarge.setChecked(true);
        } else {
            radioButtonBlind.setChecked(true);
        }
    }

    private void mudarTelaInicial(){
        salvarPreferenciaSize();
        retornaPreferencia();
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}