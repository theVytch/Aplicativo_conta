package br.com.contas.activities.telas_conta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import br.com.contas.activities.ActivityTelaIncialListaConta;
import br.com.contas.entities.Conta;
import br.com.contas.R;

public class ActivityTelaNovaConta extends AppCompatActivity {

    private Conta contaParaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_nova_conta);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        contaParaEditar = (Conta) intent.getSerializableExtra("conta_para_editar");

        ViewPager2 viewPagerConta = findViewById(R.id.viewPagerConta);
        ViewPagerAdapterConta adapterConta = new ViewPagerAdapterConta(this, contaParaEditar);
        viewPagerConta.setAdapter(adapterConta);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mudarTelaInicial(); // Volta para a tela anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mudarTelaInicial(){
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}