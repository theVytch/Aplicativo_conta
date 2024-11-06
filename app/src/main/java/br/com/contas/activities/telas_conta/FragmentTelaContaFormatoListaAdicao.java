package br.com.contas.activities.telas_conta;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import br.com.contas.R;
import br.com.contas.activities.ActivityTelaIncialListaConta;
import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.persistence.converters.DateConverter;
import br.com.contas.utils.DecimalDigits;
import br.com.contas.utils.UtilsValida;

public class FragmentTelaContaFormatoListaAdicao extends Fragment {
    private EditText editTextNomeDaContaFormatoListaAdicao, editTextValorContaFormatoListaAdicao;
    private Conta conta;
    private final String FORMAT_DATA = "dd/MM/yyyy";
    private final String TIPO = "ENTRADA";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tela_conta_formato_lista_adicao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        iniciarComponentes(view);

        botaoSalvar(view);
    }

    private void botaoSalvar(View view) {
        Button btnSalvar = view.findViewById(R.id.btnSalvarContaFormatoListaAdicao);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarNovaAdicaoConta(view);
            }
        });
    }

    private void iniciarComponentes(View view){
        editTextNomeDaContaFormatoListaAdicao = view.findViewById(R.id.editTextNomeDaContaFormatoListaAdicao);
        iniciaEditTextValorConta(view);
    }

    private void iniciaEditTextValorConta(View view){
        editTextValorContaFormatoListaAdicao = view.findViewById(R.id.editTextValorContaFormatoListaAdicao);
        editTextValorContaFormatoListaAdicao.addTextChangedListener(new TextWatcher() {
            //DecimalFormat format = new DecimalFormat("#,##0.00");
            DecimalFormat format = new DecimalFormat("#,##0.00");
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editTextValorContaFormatoListaAdicao.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    String formatted = format.format((parsed / 100));
                    current = formatted;
                    editTextValorContaFormatoListaAdicao.setText(formatted);
                    editTextValorContaFormatoListaAdicao.setSelection(formatted.length());
                    editTextValorContaFormatoListaAdicao.addTextChangedListener(this);
                }
            }
        });
        editTextValorContaFormatoListaAdicao.setText("0,00");
    }

    private void limparCampos(){
        editTextNomeDaContaFormatoListaAdicao.setText("");
        editTextValorContaFormatoListaAdicao.setText("0,00");
    }

    public void salvarNovaAdicaoConta(View view) {
        UsuarioDatabase database = UsuarioDatabase.getDatabase(getContext());
        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        Usuario usuario = optionalUsuario.get();

        String nomeConta = editTextNomeDaContaFormatoListaAdicao.getText().toString().trim();
        Double valor = Double.parseDouble(getNumeroParaString());

        Date dataAtual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);
        String dataConta = sdf.format(dataAtual);

        if(UtilsValida.validaCampoPreenchido(nomeConta, valor)) {
            conta = new Conta(nomeConta, valor, DateConverter.stringToDate(dataConta), usuario.getId());
            conta.setTipo(TIPO);
            database.contaDao().insert(conta);
            atualizaSaldoUsuario(conta.getValor(), usuario);
            limparCampos();
        }else{
            Toast.makeText(getContext(), R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizaSaldoUsuario(Double saldo, Usuario usuario){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(getContext());
        usuario.setSaldo(usuario.getSaldo() + saldo);
        database.usuarioDao().update(usuario);
    }

    @NonNull
    private String getNumeroParaString() {
        String contaStr;
        if (DecimalDigits.idiomaCelular.equals("en")) {
            // Formato americano: 1,000.00
            contaStr = editTextValorContaFormatoListaAdicao.getText().toString();
            contaStr = contaStr.replace(",", "");
        } else {
            // Formato brasileiro: 1.000,00
            contaStr = editTextValorContaFormatoListaAdicao.getText().toString().replaceAll("[^\\d,]", "");
            contaStr = contaStr.replace(",", ".");
        }

        return contaStr;
    }

    private void mudarTelaInicial(){
        Intent intent = new Intent(getContext(), ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}
