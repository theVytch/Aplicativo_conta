package br.com.contas.activities.telas_conta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import br.com.contas.activities.ActivityTelaIncialListaConta;
import br.com.contas.entities.Conta;
import br.com.contas.R;

public class ActivityTelaNovaContaFormatoLista extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_nova_conta_formato_lista);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        exibirBotaoVoltar();

        ViewPager2 viewPagerListaConta = findViewById(R.id.viewPagerListaConta);
        ViewPagerAdapterListaConta adapterConta = new ViewPagerAdapterListaConta(this);
        viewPagerListaConta.setAdapter(adapterConta);
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