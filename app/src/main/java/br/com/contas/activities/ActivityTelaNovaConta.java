package br.com.contas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.persistence.converters.DateConverter;
import br.com.contas.utils.DecimalDigits;
import br.com.contas.utils.UtilsDateMaskWatcher;
import br.com.contas.utils.UtilsValida;
import br.com.contas.R;

public class ActivityTelaNovaConta extends AppCompatActivity {

    private EditText editTextNomeConta, editTextValorConta, editTextDate;
    private Conta conta;
    private Usuario usuario;
    private Conta contaParaEditar;
    private final String FORMAT_DATA = "dd/MM/yyyy";
    private Date dataAtual = new Date();
    private SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_nova_conta);

        iniciarComponentes();
        buscaUsuario();

        Intent intent = getIntent();
        contaParaEditar = (Conta) intent.getSerializableExtra("conta_para_editar");

        if(contaParaEditar != null){
            edicaoDaConta(contaParaEditar);
        }

        exibirBotaoVoltar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nova_conta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            // Lidar com o clique na seta de voltar
            onBackPressed();
            return true;
        } else if(id == R.id.menuItemSalvar){
            if(contaParaEditar != null){
                salvarEdicaoConta();
            }else{
                salvarNovaConta();
            }
        }else if(id == R.id.menuItemCancelar){
            limparCampos();
        }
        return super.onOptionsItemSelected(item);
    }

    private void iniciarComponentes(){
        editTextNomeConta = findViewById(R.id.editTextNomeConta);
        iniciaEditTextValorConta();
        editTextDate = findViewById(R.id.editTextDate);
        editTextDate.addTextChangedListener(new UtilsDateMaskWatcher(editTextDate));
    }

    private void exibirBotaoVoltar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void limparCampos(){
        editTextNomeConta.setText("");
        editTextValorConta.setText("");
        editTextDate.setText("");
    }

    private void iniciaEditTextValorConta(){
        editTextValorConta = findViewById(R.id.editTextValorConta);
        editTextValorConta.addTextChangedListener(new TextWatcher() {
            DecimalFormat format = new DecimalFormat("#,##0.00");
            //DecimalFormat format = new DecimalFormat(DecimalDigits.modeloFormatPattern);
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editTextValorConta.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    String formatted = format.format((parsed / 100));
                    current = formatted;
                    editTextValorConta.setText(formatted);
                    editTextValorConta.setSelection(formatted.length());
                    editTextValorConta.addTextChangedListener(this);
                }
            }
        });
        editTextValorConta.setText("0,00");
    }

    private void buscaUsuario(){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        usuario = database.usuarioDao().getUsuario().get();
    }

    private void edicaoDaConta(Conta conta) {
        editTextNomeConta.setText(conta.getNomeConta());
        editTextValorConta.setText(DecimalDigits.formatarNumero(conta.getValor()));
        editTextDate.setText(DateConverter.dateToString(conta.getData()));
    }

    public void salvarEdicaoConta(){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        conta = database.contaDao().getContaById(contaParaEditar.getId()).get();

        Double valorContaAntigo = contaParaEditar.getValor();
        usuario.setSaldo(valorContaAntigo + usuario.getSaldo());

        Double valorContaNovo = Double.parseDouble(getNumeroParaString());

        String nomeConta = editTextNomeConta.getText().toString().trim();
        String dataConta = editTextDate.getText().toString().trim();

        if(validarFormatoData(dataConta)) {
            if(UtilsValida.validaCampoPreenchido(nomeConta, valorContaNovo)) {
                conta.setNomeConta(nomeConta);
                conta.setValor(valorContaNovo);
                conta.setData(DateConverter.stringToDate(dataConta));
                conta.setUsuarioId(contaParaEditar.getUsuarioId());

                database.contaDao().update(conta);

                atualizaSaldoUsuario(valorContaNovo, usuario);
                mudarTelaInicial();
            }else{
                Toast.makeText(this, R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.mensagemDataEstaErrada, Toast.LENGTH_SHORT).show();
        }
    }



    public boolean validarFormatoData(String data) {
        sdf.setLenient(false);

        try {
            sdf.parse(data);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void salvarNovaConta() {
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);

        String nomeConta = editTextNomeConta.getText().toString().trim();

        Double valor = Double.parseDouble(getNumeroParaString());

        String dataConta = (editTextDate.getText().toString().isEmpty()) ? sdf.format(dataAtual) : editTextDate.getText().toString().trim();

        if (validarFormatoData(dataConta)) {
            if(UtilsValida.validaCampoPreenchido(nomeConta, valor)) {
                conta = new Conta(nomeConta, valor, DateConverter.stringToDate(dataConta), usuario.getId());
                database.contaDao().insert(conta);
                atualizaSaldoUsuario(conta.getValor(), usuario);
                mudarTelaInicial();
            }else{
                Toast.makeText(this, R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.mensagemDataEstaErrada, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private String getNumeroParaString() {
        String contaStr;
        if (DecimalDigits.idiomaCelular.equals("en")) {
            // Formato americano: 1,000.00
            contaStr = editTextValorConta.getText().toString();
            contaStr = contaStr.replace(",", "");
        } else {
            // Formato brasileiro: 1.000,00
            contaStr = editTextValorConta.getText().toString().replaceAll("[^\\d,]", "");
            contaStr = contaStr.replace(",", ".");
        }

        return contaStr;
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