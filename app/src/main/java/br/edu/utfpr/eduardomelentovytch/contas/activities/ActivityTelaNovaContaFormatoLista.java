package br.edu.utfpr.eduardomelentovytch.contas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import br.edu.utfpr.eduardomelentovytch.contas.R;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Conta;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Usuario;
import br.edu.utfpr.eduardomelentovytch.contas.persistence.UsuarioDatabase;
import br.edu.utfpr.eduardomelentovytch.contas.persistence.converters.DateConverter;

public class ActivityTelaNovaContaFormatoLista extends AppCompatActivity {

    private EditText editTextNomeDaContaFormatoLista, editTextValorContaFormatoLista;
    private Conta conta;
    private final String FORMAT_DATA = "dd/MM/yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_nova_conta_formato_lista);

        iniciarComponentes();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mudarTelaInicial();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void iniciarComponentes(){
        editTextNomeDaContaFormatoLista = findViewById(R.id.editTextNomeDaContaFormatoLista);
        editTextValorContaFormatoLista = findViewById(R.id.editTextValorContaFormatoLista);
    }

    private void limparCampos(){
        editTextNomeDaContaFormatoLista.setText("");
        editTextValorContaFormatoLista.setText("");
    }

    public void salvarNovaConta(View view) {
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        Usuario usuario = optionalUsuario.get();

        String nomeConta = editTextNomeDaContaFormatoLista.getText().toString().trim();
        Double valor = Double.valueOf(editTextValorContaFormatoLista.getText().toString());
        Date dataAtual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);
        String dataConta = sdf.format(dataAtual);

        conta = new Conta(nomeConta, valor, DateConverter.stringToDate(dataConta), usuario.getId());
        database.contaDao().insert(conta);
        atualizaSaldoUsuario(conta.getValor(), usuario);
        limparCampos();
    }

    private void atualizaSaldoUsuario(Double saldo, Usuario usuario){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        usuario.setSaldo(usuario.getSaldo() - saldo);
        database.usuarioDao().update(usuario);
    }

    private void mudarTelaInicial(){
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}