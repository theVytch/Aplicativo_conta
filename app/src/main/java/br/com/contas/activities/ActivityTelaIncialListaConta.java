package br.com.contas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import br.com.contas.R;
import br.com.contas.activities.telas_conta.ActivityTelaNovaConta;
import br.com.contas.activities.telas_conta.ActivityTelaNovaContaFormatoLista;
import br.com.contas.adapter.ContaAdapter;
import br.com.contas.custom.CustomTextView;
import br.com.contas.entities.Conta;
import br.com.contas.entities.ContaTipo;
import br.com.contas.entities.Usuario;
import br.com.contas.repository.DashboardData;
import br.com.contas.utils.DecimalDigits;
import br.com.contas.viewmodel.TelaInicialViewModel;

public class ActivityTelaIncialListaConta extends AppCompatActivity {

    private CustomTextView textViewSaldoUsuarioTelaList;
    private CustomTextView textViewSaldoNecessarioUsuarioTelaList;
    private CustomTextView textViewSaldoDesnecessarioUsuarioTelaList;
    private ListView listViewContas;
    private FloatingActionButton floatingBtnAdicionarConta;
    private ContaAdapter contaAdapter;
    private List<Conta> lista = new ArrayList<>();
    private Usuario usuario;
    private final Set<Integer> posicaoSelecionada = new HashSet<>();
    private TelaInicialViewModel telaInicialViewModel;
    private boolean selectionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_incial_lista_conta);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializaComponentes();
        configurarViewModel();

        marcarLinhaSelecionada();
    }

    @Override
    protected void onResume() {
        super.onResume();
        telaInicialViewModel.loadDashboard();
    }

    @Override
    public void onBackPressed() {
        if (posicaoSelecionada.isEmpty()) {
            finishAffinity();
            return;
        }
        limparSelecionadoNaLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_opcoes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean hasContas = !lista.isEmpty();
        menu.findItem(R.id.menu_selecionar_exclusao).setVisible(hasContas && !selectionMode);
        menu.findItem(R.id.menu_excluir_selecionadas).setVisible(selectionMode && hasContas);
        menu.findItem(R.id.menu_selecionar_todos).setVisible(selectionMode && hasContas);
        menu.findItem(R.id.menu_selecionar_todos).setTitle(todasContasSelecionadas()
                ? R.string.menu_limpar_selecao
                : R.string.menu_selecionar_todos);
        menu.findItem(R.id.menu_cancelar_selecao).setVisible(selectionMode);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_usuario){
            mudarParaTelaDeUsuario();
        }else if(id == R.id.menu_nova_conta){
            mudarParaTelaDeNovaContaFormatoLista();
        }else if(id == R.id.menu_sobre){
            mudarParaTelaSobre();
        }else if(id == R.id.menu_criar_pdf){
            mudarParaTelaCriarPdf();
        }else if(id == R.id.menu_selecionar_exclusao){
            entrarModoSelecao();
        }else if(id == R.id.menu_excluir_selecionadas){
            excluirSelecionadas();
        }else if(id == R.id.menu_selecionar_todos){
            selecionarTodos();
        }else if(id == R.id.menu_cancelar_selecao){
            sairModoSelecao();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarViewModel() {
        telaInicialViewModel = new ViewModelProvider(this).get(TelaInicialViewModel.class);
        telaInicialViewModel.getDashboardLiveData().observe(this, this::renderizarDashboard);
    }

    private void renderizarDashboard(DashboardData dashboardData) {
        usuario = dashboardData.getUsuario();
        lista = new ArrayList<>(dashboardData.getContas());
        contaAdapter = new ContaAdapter(this, lista, posicaoSelecionada, selectionMode);
        listViewContas.setAdapter(contaAdapter);

        if (usuario == null) {
            textViewSaldoUsuarioTelaList.setText("");
            textViewSaldoNecessarioUsuarioTelaList.setText("");
            textViewSaldoDesnecessarioUsuarioTelaList.setText("");
            limparSelecionadoNaLista();
            atualizarTituloToolbar();
            return;
        }

        Double saldo = usuario.getSaldo();
        textViewSaldoUsuarioTelaList.setText(saldo == null ? "" : String.valueOf(saldo));
        textViewSaldoNecessarioUsuarioTelaList.setText(formatarSaldo(dashboardData.getSaldoNecessario()));
        textViewSaldoDesnecessarioUsuarioTelaList.setText(formatarSaldo(dashboardData.getSaldoDesnecessario()));
        atualizarTituloToolbar();
    }

    private String formatarSaldo(Double saldo) {
        return saldo == null ? "" : String.valueOf(saldo);
    }

    private void limparSelecionadoNaLista() {
        posicaoSelecionada.clear();
        if (contaAdapter != null) {
            contaAdapter.notifyDataSetChanged();
        }
        atualizarTituloToolbar();
    }

    private void entrarModoSelecao() {
        selectionMode = true;
        limparSelecionadoNaLista();
        invalidateOptionsMenu();
        renderizarDashboard(telaInicialViewModel.getDashboardLiveData().getValue() == null
                ? DashboardData.empty()
                : telaInicialViewModel.getDashboardLiveData().getValue());
    }

    private void sairModoSelecao() {
        selectionMode = false;
        limparSelecionadoNaLista();
        invalidateOptionsMenu();
        renderizarDashboard(telaInicialViewModel.getDashboardLiveData().getValue() == null
                ? DashboardData.empty()
                : telaInicialViewModel.getDashboardLiveData().getValue());
    }

    private void excluirSelecionadas() {
        if (posicaoSelecionada.isEmpty()) {
            Toast.makeText(this, R.string.mensagemCampoVazio, Toast.LENGTH_SHORT).show();
            return;
        }

        List<Conta> selecionadas = retornarListaContaSelecionada();
        if (selecionadas.size() == 1) {
            Integer position = posicaoSelecionada.iterator().next();
            deletarConta(position);
            return;
        }
        deletarMaisDeUmaConta(selecionadas);
    }

    private void selecionarTodos() {
        if (todasContasSelecionadas()) {
            posicaoSelecionada.clear();
        } else {
            posicaoSelecionada.clear();
            for (int i = 0; i < lista.size(); i++) {
                posicaoSelecionada.add(i);
            }
        }

        if (contaAdapter != null) {
            contaAdapter.notifyDataSetChanged();
        }
        atualizarTituloToolbar();
        invalidateOptionsMenu();
    }

    private boolean todasContasSelecionadas() {
        return !lista.isEmpty() && posicaoSelecionada.size() == lista.size();
    }

    private void atualizarTituloToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        if (selectionMode) {
            if (posicaoSelecionada.isEmpty()) {
                actionBar.setTitle(R.string.titulo_modo_selecao);
            } else {
                actionBar.setTitle(getString(R.string.titulo_selecao_contas, posicaoSelecionada.size()));
            }
            if (floatingBtnAdicionarConta != null) {
                floatingBtnAdicionarConta.hide();
            }
            return;
        }

        if (floatingBtnAdicionarConta != null) {
            floatingBtnAdicionarConta.show();
        }
        actionBar.setTitle(R.string.app_name);
    }

    private void marcarLinhaSelecionada() {
        listViewContas.setOnItemLongClickListener((parent, view, position, id) -> {
            if (selectionMode) {
                alternarSelecao(position);
            }
            return true;
        });

        listViewContas.setOnItemClickListener((adapterView, view, position, id) -> {
            if (selectionMode) {
                alternarSelecao(position);
                return;
            }
            showCustomDialog(position);
        });
    }

    private void alternarSelecao(int position) {
        if (posicaoSelecionada.contains(position)) {
            posicaoSelecionada.remove(position);
        } else {
            posicaoSelecionada.add(position);
        }

        if (contaAdapter != null) {
            contaAdapter.notifyDataSetChanged();
        }
        atualizarTituloToolbar();
        invalidateOptionsMenu();
    }

    private void inicializaComponentes() {
        listViewContas = findViewById(R.id.listViewContas);
        floatingBtnAdicionarConta = findViewById(R.id.floatingBtnAdicionarConta);
        textViewSaldoUsuarioTelaList = findViewById(R.id.textViewSaldoUsuarioTelaList);
        textViewSaldoDesnecessarioUsuarioTelaList = findViewById(R.id.textViewSaldoDesnecessarioUsuarioTelaList);
        textViewSaldoNecessarioUsuarioTelaList = findViewById(R.id.textViewSaldoNecessarioUsuarioTelaList);
        DecimalDigits.formatPattern(idiomaCelular());
    }

    private String idiomaCelular(){
        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);
        return currentLocale.getLanguage();
    }

    public void adicionarNovaConta(View view){
        mudarParaTelaDeNovaConta();
    }

    private void deletar(int position) {
        if (posicaoSelecionada.size() > 1) {
            deletarMaisDeUmaConta(retornarListaContaSelecionada());
            return;
        }
        deletarConta(position);
    }

    private List<Conta> retornarListaContaSelecionada() {
        List<Conta> listaItens = new ArrayList<>();
        for (Integer position : posicaoSelecionada) {
            if (position >= 0 && position < lista.size()) {
                listaItens.add(lista.get(position));
            }
        }
        return listaItens;
    }

    public void mudarParaTelaDeUsuario() {
        Intent intent = new Intent(this, ActivityTelaUsuario.class);
        startActivity(intent);
    }

    public void deletarConta(int posicao) {
        if (posicao < 0 || posicao >= lista.size()) {
            return;
        }

        Conta contaSelecionada = lista.get(posicao);
        String mensagemExcluir = getResources().getString(R.string.mensagemAvisoApagar) + "\n" + contaSelecionada.getNomeConta() + " ?";
        double valorContaExcluida = contaSelecionada.getValor();
        boolean tipoContaEntrada = ContaTipo.ENTRADA.equals(contaSelecionada.getTipo());

        View dialogView = getLayoutInflater().inflate(R.layout.menu_dialog_custom_coringa, null);
        TextView textView = dialogView.findViewById(R.id.textViewMenuDialogCustomCoringa);
        Button buttonNao = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaNao);
        Button buttonSim = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaSim);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        textView.setText(mensagemExcluir);
        buttonSim.setText(R.string.sim);
        buttonNao.setText(R.string.nao);

        buttonSim.setOnClickListener(v -> {
            if (tipoContaEntrada) {
                telaInicialViewModel.deleteConta(contaSelecionada, -valorContaExcluida);
                dialog.dismiss();
                limparSelecionadoNaLista();
                return;
            }

            textView.setText(getResources().getString(R.string.mensagemRetornarValorContaSaldo));
            buttonSim.setOnClickListener(c -> {
                telaInicialViewModel.deleteConta(contaSelecionada, valorContaExcluida);
                dialog.dismiss();
                limparSelecionadoNaLista();
            });

            buttonNao.setOnClickListener(c -> {
                telaInicialViewModel.deleteConta(contaSelecionada, 0);
                dialog.dismiss();
                limparSelecionadoNaLista();
            });
        });

        buttonNao.setOnClickListener(v -> {
            dialog.dismiss();
            limparSelecionadoNaLista();
        });

        dialog.show();
    }

    public void deletarMaisDeUmaConta(List<Conta> listaConta) {
        double valorSaidas = 0.0;
        double valorEntradas = 0.0;

        for (Conta conta : listaConta) {
            if (ContaTipo.ENTRADA.equals(conta.getTipo())) {
                valorEntradas += conta.getValor();
            } else {
                valorSaidas += conta.getValor();
            }
        }

        String mensagemExcluir = getResources().getString(R.string.mensagemAvisoApagarMaisDeUmaConta);

        View dialogView = getLayoutInflater().inflate(R.layout.menu_dialog_custom_coringa, null);
        TextView textView = dialogView.findViewById(R.id.textViewMenuDialogCustomCoringa);
        Button buttonNao = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaNao);
        Button buttonSim = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaSim);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        textView.setText(mensagemExcluir);
        buttonSim.setText(R.string.sim);
        buttonNao.setText(R.string.nao);

        double saldoDeltaBase = -valorEntradas;
        double finalValorSaidas = valorSaidas;

        buttonSim.setOnClickListener(v -> {
            if (finalValorSaidas > 0) {
                textView.setText(getResources().getString(R.string.mensagemRetornarValorTodasAsContaSaldo));
                buttonSim.setOnClickListener(c -> {
                    telaInicialViewModel.deleteContas(listaConta, saldoDeltaBase + finalValorSaidas);
                    dialog.dismiss();
                    limparSelecionadoNaLista();
                });

                buttonNao.setOnClickListener(c -> {
                    telaInicialViewModel.deleteContas(listaConta, saldoDeltaBase);
                    dialog.dismiss();
                    limparSelecionadoNaLista();
                });
                return;
            }

            telaInicialViewModel.deleteContas(listaConta, saldoDeltaBase);
            dialog.dismiss();
            limparSelecionadoNaLista();
        });

        buttonNao.setOnClickListener(v -> {
            dialog.dismiss();
            limparSelecionadoNaLista();
        });

        dialog.show();
    }

    public void showCustomDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.menu_dialog_custom, null);
        builder.setView(customLayout);

        Button buttonEditarConta = customLayout.findViewById(R.id.buttonEditarConta);
        Button buttonDeletarConta = customLayout.findViewById(R.id.buttonDeletarConta);

        AlertDialog dialog = builder.create();

        buttonEditarConta.setOnClickListener(v -> {
            mudarParaTelaDeNovaContaParaEditar(position);
            dialog.dismiss();
            limparSelecionadoNaLista();
        });

        buttonDeletarConta.setOnClickListener(v -> {
            deletar(position);
            dialog.dismiss();
        });
        dialog.show();
    }

    public void mudarParaTelaDeNovaConta() {
        if(usuario != null){
            Intent intent = new Intent(this, ActivityTelaNovaConta.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, R.string.mensagemCrieUsuarioParaAdicionarConta, Toast.LENGTH_LONG).show();
        }
    }

    public void mudarParaTelaDeNovaContaParaEditar(int posicao){
        if (posicao < 0 || posicao >= lista.size()) {
            return;
        }

        Intent intent = new Intent(this, ActivityTelaNovaConta.class);
        intent.putExtra("conta_para_editar", lista.get(posicao));
        startActivity(intent);
    }

    public void mudarParaTelaDeNovaContaFormatoLista(){
        if(usuario != null) {
            Intent intent = new Intent(this, ActivityTelaNovaContaFormatoLista.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, R.string.mensagemCrieUsuarioParaAdicionarConta, Toast.LENGTH_LONG).show();
        }
    }

    public void mudarParaTelaSobre(){
        Intent intent = new Intent(this, ActivityTelaSobreConta.class);
        startActivity(intent);
    }

    public void mudarParaTelaCriarPdf(){
        if(usuario != null) {
            Intent intent = new Intent(this, ActivityTelaSalvarListaDeContaNoCelular.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, R.string.mensagemCrieUsuarioParaGerarPdf, Toast.LENGTH_LONG).show();
        }
    }
}
