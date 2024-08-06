package br.com.contas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.contas.R;
import br.com.contas.utils.Ordenar;

public class ActivityTelaSobreConta extends AppCompatActivity {

    private static final String ARQUIVO =
            "br.edu.eduardomelentovytch.aplicativoContas.PREFERENCIA_ORDENACAO";
    private static final String ORDENACAO = "ORDENACAO";
    private String opcao = Ordenar.opcaoOrdenacao;
    private TextView textViewSobre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_sobre_conta);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iniciarComponentes();

        exibirBotaoVoltar();
    }

    @Override
    public void onBackPressed() {
        mudarTelaInicial();
    }

    private void iniciarComponentes(){
        textViewSobre = findViewById(R.id.textViewSobre);
        textViewSobre.setMovementMethod(new ScrollingMovementMethod());
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

    private void mudarTelaInicial(){
        retornaPreferencia();
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}