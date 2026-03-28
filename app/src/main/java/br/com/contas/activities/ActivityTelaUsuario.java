package br.com.contas.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;

import br.com.contas.R;
import br.com.contas.entities.Usuario;
import br.com.contas.utils.DecimalDigits;
import br.com.contas.viewmodel.UsuarioViewModel;

public class ActivityTelaUsuario extends AppCompatActivity {

    private EditText editTextNomeUsuario;
    private EditText editTextSaldoUsuario;
    private Button btnSalvarUsuario;
    private UsuarioViewModel usuarioViewModel;
    private boolean usuarioExiste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_usuario);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iniciarComponentes();
        configurarViewModel();
        exibirBotaoVoltar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usuarioViewModel.loadUsuario();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mudarTelaInicial();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarViewModel() {
        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
        usuarioViewModel.getUsuarioLiveData().observe(this, this::renderizarUsuario);
    }

    private void renderizarUsuario(Usuario usuario) {
        if (usuario != null) {
            editTextNomeUsuario.setText(usuario.getNome());
            editTextSaldoUsuario.setText(DecimalDigits.formatarNumero(usuario.getSaldo()));
            btnSalvarUsuario.setText(R.string.atualizar);
            usuarioExiste = true;
            return;
        }

        editTextNomeUsuario.setText("");
        editTextSaldoUsuario.setText("");
        btnSalvarUsuario.setText(R.string.salvar);
        usuarioExiste = false;
    }

    private void exibirBotaoVoltar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void iniciarComponentes(){
        editTextNomeUsuario = findViewById(R.id.editTextNomeUsuario);
        iniciaEditTextValorConta();
        btnSalvarUsuario = findViewById(R.id.btnSalvarUsuario);
    }

    private void iniciaEditTextValorConta(){
        editTextSaldoUsuario = findViewById(R.id.editTextSaldoUsuario);
        editTextSaldoUsuario.addTextChangedListener(new TextWatcher() {
            DecimalFormat format = new DecimalFormat(DecimalDigits.modeloFormatPattern);
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editTextSaldoUsuario.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    String formatted = format.format((parsed / 100));
                    current = formatted;
                    editTextSaldoUsuario.setText(formatted);
                    editTextSaldoUsuario.setSelection(formatted.length());
                    editTextSaldoUsuario.addTextChangedListener(this);
                }
            }
        });
    }

    public void salvarUsuario(View view){
        String nome = editTextNomeUsuario.getText().toString().trim();
        if (nome.isEmpty()) {
            Toast.makeText(this, R.string.mensagemNomeUsuarioObrigatorio, Toast.LENGTH_SHORT).show();
            return;
        }

        String saldoStr = getNumeroParaString();
        if (saldoStr.isEmpty()) {
            Toast.makeText(this, R.string.mensagemSaldoUsuarioObrigatorio, Toast.LENGTH_SHORT).show();
            return;
        }

        Double saldo = Double.parseDouble(saldoStr);
        usuarioViewModel.salvarUsuario(nome, saldo, usuarioExiste, success -> {
            if (success) {
                mudarTelaInicial();
            } else {
                Toast.makeText(this, R.string.mensagemErroCriarUsuario, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getNumeroParaString() {
        String saldoStr;
        if (DecimalDigits.idiomaCelular.equals("en")) {
            saldoStr = editTextSaldoUsuario.getText().toString().replace(",", "");
        } else {
            saldoStr = editTextSaldoUsuario.getText().toString().replaceAll("[^\\d,]", "");
            saldoStr = saldoStr.replace(",", ".");
        }

        return saldoStr;
    }

    private void mudarTelaInicial(){
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}
