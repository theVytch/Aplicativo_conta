package br.com.contas.activities.telas_conta;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
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
import br.com.contas.utils.UtilsDateMaskWatcher;
import br.com.contas.utils.UtilsValida;

public class FragmentTelaContaAdicao extends Fragment {
    private EditText editTextNomeContaAdicao, editTextValorContaAdicao, editTextDateAdicao;
    private Conta conta;
    private Usuario usuario;
    private Conta contaParaEditar;
    private Date dataAtual = new Date();
    private final String TIPO = "ENTRADA";
    private final String FORMAT_DATA = "dd/MM/yyyy";
    private SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tela_conta_adicao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        iniciarComponentes(view);
        buscaUsuario();

        if (getArguments() != null) {
            contaParaEditar = (Conta) getArguments().getSerializable("conta_para_editar");
        }
        if (contaParaEditar != null) {
            if(contaParaEditar.getTipo().equals("ENTRADA")){
                adicionaContaParaEditarNosCampos(contaParaEditar);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Infla o menu que você deseja usar
        inflater.inflate(R.menu.menu_nova_conta, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItemSalvar) {
            if(contaParaEditar != null){
                salvarEdicaoConta();
            }else{
                salvarNovaContaAdicao();
            }
            return true;
        } else if (item.getItemId() == R.id.menuItemCancelar) {
            limparCampo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void limparCampo(){
        editTextNomeContaAdicao.setText("");
        editTextValorContaAdicao.setText(String.valueOf(""));
        editTextDateAdicao.setText("");
    }

    private void iniciarComponentes(View view){
        editTextNomeContaAdicao = view.findViewById(R.id.editTextNomeContaAdicao);
        iniciaEditTextValorConta(view);
        editTextDateAdicao = view.findViewById(R.id.editTextDateAdicao);
        editTextDateAdicao.addTextChangedListener(new UtilsDateMaskWatcher(editTextDateAdicao));
    }

    private void adicionaContaParaEditarNosCampos(Conta contaParaEditar) {
        editTextNomeContaAdicao.setText(contaParaEditar.getNomeConta());
        editTextValorContaAdicao.setText(DecimalDigits.formatarNumero(contaParaEditar.getValor()));
        editTextDateAdicao.setText(DateConverter.dateToString(contaParaEditar.getData()));
    }

    private void buscaUsuario(){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(getContext());
        usuario = database.usuarioDao().getUsuario().get();
    }

    private void iniciaEditTextValorConta(View view){
        editTextValorContaAdicao = view.findViewById(R.id.editTextValorContaAdicao);
        editTextValorContaAdicao.addTextChangedListener(new TextWatcher() {
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
                    editTextValorContaAdicao.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    String formatted = format.format((parsed / 100));
                    current = formatted;
                    editTextValorContaAdicao.setText(formatted);
                    editTextValorContaAdicao.setSelection(formatted.length());
                    editTextValorContaAdicao.addTextChangedListener(this);
                }
            }
        });
        editTextValorContaAdicao.setText("0,00");
    }

    public void salvarEdicaoConta(){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(getContext());
        conta = database.contaDao().getContaById(contaParaEditar.getId()).get();

        Double valorContaAntigo = contaParaEditar.getValor();
        usuario.setSaldo(usuario.getSaldo() - valorContaAntigo);


        String nomeConta = editTextNomeContaAdicao.getText().toString().trim();
        Double valorContaNovo = Double.parseDouble(getNumeroParaString());
        String dataConta = (editTextDateAdicao.getText().toString().isEmpty()) ? sdf.format(dataAtual) : editTextDateAdicao.getText().toString().trim();


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
                Toast.makeText(getContext(), R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.mensagemDataEstaErrada, Toast.LENGTH_SHORT).show();
        }
    }

    public void salvarNovaContaAdicao() {
        UsuarioDatabase database = UsuarioDatabase.getDatabase(getContext());
        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        Usuario usuario = optionalUsuario.get();

        String nomeConta = editTextNomeContaAdicao.getText().toString().trim();
        Double valor = Double.parseDouble(getNumeroParaString());

        Date dataAtual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);
        String dataConta = (editTextDateAdicao.getText().toString().isEmpty()) ? sdf.format(dataAtual) : editTextDateAdicao.getText().toString().trim();

        if(UtilsValida.validaCampoPreenchido(nomeConta, valor)) {
            conta = new Conta(nomeConta, valor, DateConverter.stringToDate(dataConta), usuario.getId());
            conta.setTipo(TIPO);
            database.contaDao().insert(conta);
            atualizaSaldoUsuario(conta.getValor(), usuario);
            mudarTelaInicial();
        }else{
            Toast.makeText(getContext(), R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
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
            contaStr = editTextValorContaAdicao.getText().toString();
            contaStr = contaStr.replace(",", "");
        } else {
            // Formato brasileiro: 1.000,00
            contaStr = editTextValorContaAdicao.getText().toString().replaceAll("[^\\d,]", "");
            contaStr = contaStr.replace(",", ".");
        }

        return contaStr;
    }

    private void mudarTelaInicial() {
        Intent intent = new Intent(requireActivity(), ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}