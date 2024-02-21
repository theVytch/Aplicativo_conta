package br.edu.utfpr.eduardomelentovytch.contas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import br.edu.utfpr.eduardomelentovytch.contas.R;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Conta;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Usuario;
import br.edu.utfpr.eduardomelentovytch.contas.persistence.UsuarioDatabase;
import br.edu.utfpr.eduardomelentovytch.contas.persistence.converters.DateConverter;
import br.edu.utfpr.eduardomelentovytch.contas.utils.UtilsValida;

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
        iniciaEditTextValorConta();
    }

    private void iniciaEditTextValorConta(){
        editTextValorContaFormatoLista = findViewById(R.id.editTextValorContaFormatoLista);
        editTextValorContaFormatoLista.addTextChangedListener(new TextWatcher() {
            DecimalFormat format = new DecimalFormat("#,##0.00");
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editTextValorContaFormatoLista.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    String formatted = format.format((parsed / 100));
                    current = formatted;
                    editTextValorContaFormatoLista.setText(formatted);
                    editTextValorContaFormatoLista.setSelection(formatted.length());
                    editTextValorContaFormatoLista.addTextChangedListener(this);
                }
            }
        });
        editTextValorContaFormatoLista.setText("0,00");
    }

    private void limparCampos(){
        editTextNomeDaContaFormatoLista.setText("");
        editTextValorContaFormatoLista.setText("0,00");
    }

    public void salvarNovaConta(View view) {
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        Usuario usuario = optionalUsuario.get();

        String nomeConta = editTextNomeDaContaFormatoLista.getText().toString().trim();
        Double valor = Double.parseDouble(editTextValorContaFormatoLista.getText().toString()
                                                        .replaceAll("[^\\d,]", "")
                                                        .replace(",", "."));
        Date dataAtual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);
        String dataConta = sdf.format(dataAtual);

        if(UtilsValida.validaCampoPreenchido(nomeConta, valor)) {
            conta = new Conta(nomeConta, valor, DateConverter.stringToDate(dataConta), usuario.getId());
            database.contaDao().insert(conta);
            atualizaSaldoUsuario(conta.getValor(), usuario);
            limparCampos();
        }else{
            Toast.makeText(this, R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
        }
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