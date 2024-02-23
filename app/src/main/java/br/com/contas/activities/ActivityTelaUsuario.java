package br.com.contas.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import java.text.DecimalFormatSymbols;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.R;

public class ActivityTelaUsuario extends AppCompatActivity {

    private EditText editTextNomeUsuario, editTextSaldoUsuario;
    private Button btnSalvarUsuario;
    private Usuario usuario;
    private int usuarioExiste = 0; // 1 - existe, 0 - nao existe


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_usuario);

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
        editTextNomeUsuario = findViewById(R.id.editTextNomeUsuario);
        iniciaEditTextValorConta();
        btnSalvarUsuario = findViewById(R.id.btnSalvarUsuario);
        verificarSeUsuarioJaExiste();
    }

    private void iniciaEditTextValorConta(){
        editTextSaldoUsuario = findViewById(R.id.editTextSaldoUsuario);
        editTextSaldoUsuario.addTextChangedListener(new TextWatcher() {
            DecimalFormat format = new DecimalFormat("#,##0.00");
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

    private void verificarSeUsuarioJaExiste(){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        if(optionalUsuario.isPresent()){
            mudarTelaParaEdicao(optionalUsuario.get());
        }else{
            mudarTelaParaCriarUsuario();
        }
    }

    private void mudarTelaParaEdicao(Usuario usuario){
        editTextNomeUsuario.setText(usuario.getNome());
        editTextSaldoUsuario.setText(formatarNumero(usuario.getSaldo()));
        btnSalvarUsuario.setText("Atualizar");
        usuarioExiste = 1;
    }

    private void mudarTelaParaCriarUsuario(){
        editTextNomeUsuario.setText("");
        editTextSaldoUsuario.setText("");
        btnSalvarUsuario.setText("Salvar");
        usuarioExiste = 0;
    }

    public void salvarUsuario(View view){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            UsuarioDatabase database = UsuarioDatabase.getDatabase(this);

            String nome = editTextNomeUsuario.getText().toString().trim();
            if (nome.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            R.string.mensagemNomeUsuarioObrigatorio,
                            Toast.LENGTH_SHORT).show();
                });
                return;
            }

            String saldoStr = editTextSaldoUsuario.getText().toString().replaceAll("[^\\d,]", "").replace(",", ".");
            if (saldoStr.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            R.string.mensagemSaldoUsuarioObrigatorio,
                            Toast.LENGTH_SHORT).show();
                });
                return;
            }

            Double saldo = Double.parseDouble(saldoStr);

            if (usuarioExiste == 0) {
                salvar(database, nome, saldo);
            } else {
                editar(database, nome, saldo);
            }
        });
    }

    private void salvar(UsuarioDatabase database, String nome, Double saldo){
        usuario = new Usuario(nome.trim(), saldo);
        database.usuarioDao().insert(usuario);
        if (nome.equals(database.usuarioDao().getNomeUsuario(nome).getNome())) {
            mudarTelaInicial();
        }else{
            Toast.makeText(this,
                    R.string.mensagemErroCriarUsuario,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void editar(UsuarioDatabase database, String nome, Double saldo){
        Optional<Usuario> usuarioAtualizado = database.usuarioDao().getUsuario();
        usuario = usuarioAtualizado.get();
        usuario.setNome(nome);
        usuario.setSaldo(saldo);
        database.usuarioDao().update(usuario);
        mudarTelaInicial();
    }

    private void mudarTelaInicial(){
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }

    public String formatarNumero(Double numero) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

        return df.format(numero);
    }
}