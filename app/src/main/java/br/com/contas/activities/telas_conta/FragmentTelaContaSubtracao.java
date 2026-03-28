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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.contas.R;
import br.com.contas.activities.ActivityTelaIncialListaConta;
import br.com.contas.entities.Conta;
import br.com.contas.entities.ContaTipo;
import br.com.contas.entities.NecessidadeGasto;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.converters.DateConverter;
import br.com.contas.repository.ContasRepository;
import br.com.contas.utils.DecimalDigits;
import br.com.contas.utils.UtilsDateMaskWatcher;
import br.com.contas.utils.UtilsValida;

public class FragmentTelaContaSubtracao extends Fragment {
    private EditText editTextNomeContaSubtracao, editTextValorContaSubtracao, editTextDateSubtracao;
    private Conta conta;
    private Usuario usuario;
    private Conta contaParaEditar;
    private final String FORMAT_DATA = "dd/MM/yyyy";
    private Date dataAtual = new Date();
    private SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);
    private ToggleButton toggle;
    private String NECESSIDADE_GASTO = NecessidadeGasto.NECESSARIO;
    private ContasRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tela_conta_subtracao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);


        iniciarComponentes(view);
        repository = new ContasRepository(requireContext());
        buscaUsuario();

        if (getArguments() != null) {
            contaParaEditar = (Conta) getArguments().getSerializable("conta_para_editar");
        }


        if (contaParaEditar != null) {
            if(ContaTipo.SAIDA.equals(contaParaEditar.getTipo())) {
                adicionaContaParaEditarNosCampos(contaParaEditar);
            }
        }


        botaoNecessarioDesnecessario();

    }

    private void botaoNecessarioDesnecessario() {
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    NECESSIDADE_GASTO = NecessidadeGasto.DESNECESSARIO;
                    toggle.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_desnecessario_tela_usuario_conta));
                } else {
                    NECESSIDADE_GASTO = NecessidadeGasto.NECESSARIO;
                    toggle.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_necessario_tela_usuario_conta));
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Infla o menu que você deseja usar
        inflater.inflate(R.menu.menu_nova_conta, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Verifica o ID do item do menu clicado
        if (item.getItemId() == R.id.menuItemSalvar) {
            // Ação para o item de menu "Salvar"
            if(contaParaEditar != null){
                salvarEdicaoConta();
            }else{
                salvarNovaContaSubtracao();
            }

            return true;
        } else if (item.getItemId() == R.id.menuItemCancelar) {
            // Ação para o item de menu "Cancelar"
            limparCampo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void iniciarComponentes(View view){
        editTextNomeContaSubtracao = view.findViewById(R.id.editTextNomeContaSubtracao);
        iniciaEditTextValorConta(view);
        editTextDateSubtracao = view.findViewById(R.id.editTextDateSubtracao);
        editTextDateSubtracao.addTextChangedListener(new UtilsDateMaskWatcher(editTextDateSubtracao));
        toggle = view.findViewById(R.id.toggleButtonNecessarioDesnecessario);
    }

    private void adicionaContaParaEditarNosCampos(Conta contaParaEditar) {
        editTextNomeContaSubtracao.setText(contaParaEditar.getNomeConta());
        editTextValorContaSubtracao.setText(DecimalDigits.formatarNumero(contaParaEditar.getValor()));
        editTextDateSubtracao.setText(DateConverter.dateToString(contaParaEditar.getData()));
        NECESSIDADE_GASTO = contaParaEditar.getNecessidadeGasto();
        if (NecessidadeGasto.NECESSARIO.equals(contaParaEditar.getNecessidadeGasto())){
            toggle.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_necessario_tela_usuario_conta));
            toggle.setChecked(false);
        } else {
            toggle.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_desnecessario_tela_usuario_conta));
            toggle.setChecked(true);
        }
    }

    private void buscaUsuario(){
        repository.getUsuario(usuarioEncontrado -> usuario = usuarioEncontrado);
    }

    public void limparCampo(){
        editTextNomeContaSubtracao.setText("");
        editTextValorContaSubtracao.setText(String.valueOf(""));
        editTextDateSubtracao.setText("");
    }

    private void iniciaEditTextValorConta(View view){
        editTextValorContaSubtracao = view.findViewById(R.id.editTextValorContaSubtracao);
        editTextValorContaSubtracao.addTextChangedListener(new TextWatcher() {
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
                    editTextValorContaSubtracao.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    String formatted = format.format((parsed / 100));
                    current = formatted;
                    editTextValorContaSubtracao.setText(formatted);
                    editTextValorContaSubtracao.setSelection(formatted.length());
                    editTextValorContaSubtracao.addTextChangedListener(this);
                }
            }
        });
        editTextValorContaSubtracao.setText("0,00");
    }

    public void salvarEdicaoConta(){
        Double valorContaNovo = Double.parseDouble(getNumeroParaString());

        String nomeConta = editTextNomeContaSubtracao.getText().toString().trim();
        String dataConta = (editTextDateSubtracao.getText().toString().isEmpty()) ? sdf.format(dataAtual) : editTextDateSubtracao.getText().toString().trim();

        if(validarFormatoData(dataConta)) {
            if(UtilsValida.validaCampoPreenchido(nomeConta, valorContaNovo)) {
                repository.getContaById(contaParaEditar.getId(), contaAtual -> {
                    if (contaAtual == null) {
                        Toast.makeText(getContext(), R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    contaAtual.setNomeConta(nomeConta);
                    contaAtual.setValor(valorContaNovo);
                    contaAtual.setData(DateConverter.stringToDate(dataConta));
                    contaAtual.setNecessidadeGasto(NECESSIDADE_GASTO);
                    contaAtual.setUsuarioId(contaParaEditar.getUsuarioId());

                    double saldoDelta = contaParaEditar.getValor() - valorContaNovo;
                    repository.updateConta(contaAtual, saldoDelta, this::mudarTelaInicial);
                });
            }else{
                Toast.makeText(getContext(), R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.mensagemDataEstaErrada, Toast.LENGTH_SHORT).show();
        }
    }

    public void salvarNovaContaSubtracao() {
        String nomeConta = editTextNomeContaSubtracao.getText().toString().trim();
        Double valor = Double.parseDouble(getNumeroParaString());
        String dataConta = (editTextDateSubtracao.getText().toString().isEmpty()) ? sdf.format(dataAtual) : editTextDateSubtracao.getText().toString().trim();

        if(UtilsValida.validaCampoPreenchido(nomeConta, valor)) {
            repository.getUsuario(usuarioEncontrado -> {
                if (usuarioEncontrado == null) {
                    Toast.makeText(getContext(), R.string.mensagemCrieUsuarioParaAdicionarConta, Toast.LENGTH_LONG).show();
                    return;
                }

                conta = new Conta(nomeConta, valor, DateConverter.stringToDate(dataConta), usuarioEncontrado.getId());
                conta.setNecessidadeGasto(NECESSIDADE_GASTO);
                repository.insertConta(conta, -conta.getValor(), this::mudarTelaInicial);
            });
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

    @NonNull
    private String getNumeroParaString() {
        String contaStr;
        if (DecimalDigits.idiomaCelular.equals("en")) {
            // Formato americano: 1,000.00
            contaStr = editTextValorContaSubtracao.getText().toString();
            contaStr = contaStr.replace(",", "");
        } else {
            // Formato brasileiro: 1.000,00
            contaStr = editTextValorContaSubtracao.getText().toString().replaceAll("[^\\d,]", "");
            contaStr = contaStr.replace(",", ".");
        }

        return contaStr;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setHasOptionsMenu(false); // Desabilitar o menu ao sair do fragmento
    }

    private void mudarTelaInicial() {
        Intent intent = new Intent(requireActivity(), ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}
