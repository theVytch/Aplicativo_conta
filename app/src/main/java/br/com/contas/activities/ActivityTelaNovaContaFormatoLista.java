package br.com.contas.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import br.com.contas.entities.Conta;
import br.com.contas.R;

public class ActivityTelaNovaContaFormatoLista extends AppCompatActivity {

    private EditText editTextNomeDaContaFormatoLista, editTextValorContaFormatoLista;
    private Conta conta;
    private final String FORMAT_DATA = "dd/MM/yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_nova_conta_formato_lista);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //iniciarComponentes();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        exibirBotaoVoltar();

        ViewPager2 viewPagerSubtracao = findViewById(R.id.viewPagerSubtracao);
        ViewPagerAdapter adapterSubtracao = new ViewPagerAdapter(this);
        viewPagerSubtracao.setAdapter(adapterSubtracao);

        ViewPager2 viewPagerAdicao = findViewById(R.id.viewPagerAdicao);
        ViewPagerAdapter adapterAdicao = new ViewPagerAdapter(this);
        viewPagerAdicao.setAdapter(adapterAdicao);
    }


    @Override
    public void onBackPressed() {
        mudarTelaInicial();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mudarTelaInicial();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exibirBotaoVoltar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void mudarTelaInicial(){
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}