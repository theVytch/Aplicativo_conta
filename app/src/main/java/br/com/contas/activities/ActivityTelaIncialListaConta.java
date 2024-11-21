package br.com.contas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;


import br.com.contas.activities.telas_conta.ActivityTelaNovaConta;
import br.com.contas.activities.telas_conta.ActivityTelaNovaContaFormatoLista;
import br.com.contas.adapter.ContaAdapter;
import br.com.contas.custom.CustomTextView;
import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;

import br.com.contas.utils.DecimalDigits;
import br.com.contas.utils.Ordenar;
import br.com.contas.utils.UtilsGUI;
import br.com.contas.R;

public class ActivityTelaIncialListaConta extends AppCompatActivity {

    private CustomTextView textViewSaldoUsuarioTelaList;
    private ListView listViewContas;
    private ContaAdapter contaAdapter;
    private List<Conta> lista;
    private Usuario usuario;
    private final String TIPO = "ENTRADA";
    private Set<Integer> posicaoSelecionada = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_incial_lista_conta);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializaComponentes();
        verificaSeUsuarioExiste();

        registerForContextMenu(listViewContas);

        marcarLinhaSelecionada();
    }

    @Override
    public void onBackPressed() {
        int selecionados = retornarNumeroDeItensSelecionados();
        if(selecionados == 0){
            exitApp();
        }
        limparSelecionadoNaLista();
    }

    private void limparSelecionadoNaLista() {
        for (int i = 0; i < listViewContas.getChildCount(); i++) {
            View view = listViewContas.getChildAt(i);
            if (view != null) {
                view.setActivated(false);
            }
        }
        posicaoSelecionada.clear();
    }

    private void exitApp() {
        finishAffinity();
    }

    private void marcarLinhaSelecionada() {
        listViewContas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (posicaoSelecionada.contains(position)) {
                    posicaoSelecionada.remove(position);
                } else {
                    posicaoSelecionada.add(position);
                }
                if(!view.isActivated()) {
                    view.setActivated(!view.isActivated());
                    return true;
                }
                if(view.isActivated()){
                    return false;
                }

                contaAdapter.notifyDataSetChanged();
                return true;
            }
        });

        listViewContas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (posicaoSelecionada.contains(position)) {
                    posicaoSelecionada.remove(position);
                } else {
                    posicaoSelecionada.add(position);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_opcoes, menu);
        return true;
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getMenuInflater().inflate(R.menu.menu_acao_tela_lista_conta, menu);
        // Obtendo a posição do item no menu de contexto
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        showCustomDialog(v, info);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        /*if (id == R.id.menuItemEditarConta) {
            mudarParaTelaDeNovaContaParaEditar(info.position);
        } else if (id == R.id.menuItemDeletarConta) {
            deletar(info);
        } else {
            return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);

         */
        return super.onContextItemSelected(item);
    }

    private void deletar(AdapterView.AdapterContextMenuInfo info) {
        if(retornarNumeroDeItensSelecionadosParaDeletar() > 1){
            deletarMaisDeUmaConta(retornarListaConta());
        }else{
            deletarConta(info.position);
        }
    }

    private int retornarNumeroDeItensSelecionados() {
        int count = 0;
        for (int i = 0; i < listViewContas.getChildCount(); i++) {
            View view = listViewContas.getChildAt(i);
            if (view != null && view.isActivated()) {
                count++;
                break;
            }
        }
        return count;
    }

    private int retornarNumeroDeItensSelecionadosParaDeletar() {
        int count = 0;
        for (int i = 0; i < listViewContas.getChildCount(); i++) {
            View view = listViewContas.getChildAt(i);
            if (view != null && view.isActivated()) {
                count++;
            }

            if(count == 2){
                break;
            }
        }
        return count;
    }

    private List<Conta> retornarListaConta() {
        List<Conta> listaItens = new ArrayList<>();
        for (int i = 0; i < listViewContas.getChildCount(); i++) {
            View view = listViewContas.getChildAt(i);
            if (view != null && view.isActivated()) {
                Conta conta = (Conta) listViewContas.getItemAtPosition(i);
                listaItens.add(conta);
                //listViewContas.getChildAt(i).setActivated(false);
            }
        }
        return listaItens;
    }

    public void mudarParaTelaDeUsuario() {
        Intent intent = new Intent(this, ActivityTelaUsuario.class);
        startActivity(intent);
    }

    private void verificaSeUsuarioExiste(){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        if(database.usuarioDao().getUsuario().isPresent()){
            adicionaOuAtualizaLista();
            colocaSaldoNaTela();
        }else{
            textViewSaldoUsuarioTelaList.setText("");
        }
    }

    private void inicializaComponentes() {
        listViewContas = findViewById(R.id.listViewContas);
        textViewSaldoUsuarioTelaList = findViewById(R.id.textViewSaldoUsuarioTelaList);
        DecimalDigits.formatPattern(idiomaCelular());
    }
    private String idiomaCelular(){
        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);
        return currentLocale.getLanguage();
    }

    private void colocaSaldoNaTela(){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        Double saldo = optionalUsuario.get().getSaldo();
        if(saldo!=null){
            textViewSaldoUsuarioTelaList.setText(saldo.toString());
        }
    }

    private void adicionaOuAtualizaLista() {
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        if(optionalUsuario.isPresent()){
            usuario = optionalUsuario.get();
        }
        lista = Ordenar.retornaListaOrdenada(usuario.getId(), Ordenar.opcaoOrdenacao, database); //ordenar
        if (lista != null) {
            atualizarLista();
        }
    }

    public void atualizarLista() {
        contaAdapter = new ContaAdapter(this, lista, posicaoSelecionada);
        contaAdapter.notifyDataSetChanged();
        listViewContas.setAdapter(contaAdapter);
    }

    public void adicionarNovaConta(View view){
        mudarParaTelaDeNovaConta();
    }

    /*public void deletarConta(int posicao){
        String mensagem = getResources().getString(R.string.mensagemAvisoApagar) + "\n" + lista.get(posicao).getNomeConta() + " ?";
        Double valorContaExcluida = lista.get(posicao).getValor();
        boolean tipoConta = lista.get(posicao).getTipo().equals(TIPO);

        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
                    database.contaDao().delete(lista.get(posicao));
                    lista.remove(posicao);
                    contaAdapter.notifyDataSetChanged();

                    if(tipoConta) {
                        removerContaAdicionalDoSaldoUsuario(valorContaExcluida);
                        colocaSaldoNaTela();
                        break;
                    }

                    DialogInterface.OnClickListener confirmarListener = (dialogInner, whichInner) -> {
                        switch (whichInner) {
                            case DialogInterface.BUTTON_POSITIVE:
                                retornaValorContaParaSaldoUsuario(valorContaExcluida);
                                colocaSaldoNaTela();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                // Não fazer nada
                                break;
                        }
                    };
                    UtilsGUI.confirmacao(this, getResources().getString(R.string.mensagemRetornarValorContaSaldo), confirmarListener);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    // Não fazer nada
                    break;
            }
        };
        UtilsGUI.confirmacao(this, mensagem, listener);
    }*/
    public void deletarConta(int posicao) {
        // Obter os dados
        String mensagemExcluir = getResources().getString(R.string.mensagemAvisoApagar) + "\n" + lista.get(posicao).getNomeConta() + " ?";
        Double valorContaExcluida = lista.get(posicao).getValor();
        boolean tipoConta = lista.get(posicao).getTipo().equals(TIPO);

        // Reutilizar o layout customizado
        View dialogView = getLayoutInflater().inflate(R.layout.menu_dialog_custom_coringa, null);
        TextView textView = dialogView.findViewById(R.id.textViewMenuDialogCustomCoringa);
        Button buttonNao = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaNao);
        Button buttonSim = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaSim);

        // Criar o diálogo
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Etapa 1: Confirmar exclusão
        textView.setText(mensagemExcluir);
        buttonSim.setText(R.string.sim);
        buttonNao.setText(R.string.nao);

        buttonSim.setOnClickListener(v -> {
            UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
            database.contaDao().delete(lista.get(posicao));
            lista.remove(posicao);
            contaAdapter.notifyDataSetChanged();

            if (tipoConta) {
                removerContaAdicionalDoSaldoUsuario(valorContaExcluida);
                colocaSaldoNaTela();
                dialog.dismiss();
                limparSelecionadoNaLista();
            } else {
                // Etapa 2: Perguntar sobre retorno do valor
                textView.setText(getResources().getString(R.string.mensagemRetornarValorContaSaldo));
                buttonSim.setText(R.string.sim);
                buttonNao.setText(R.string.nao);

                buttonSim.setOnClickListener(c -> {
                    retornaValorContaParaSaldoUsuario(valorContaExcluida);
                    colocaSaldoNaTela();
                    dialog.dismiss();
                });

                buttonNao.setOnClickListener(c -> dialog.dismiss());
            }
        });

        //buttonNao.setOnClickListener(v -> dialog.dismiss());
        buttonNao.setOnClickListener(v -> {
            dialog.dismiss();
            limparSelecionadoNaLista();
        });

        // Mostrar o diálogo
        dialog.show();
    }



    public void deletarMaisDeUmaConta(List<Conta> listaConta) {
        // Calcular os valores totais
        Double valorTodasContaExcluida = 0.0;
        Double valorTodoSaldoExcluido = 0.0;

        for (Conta conta : listaConta) {
            if (conta.getTipo().equals(TIPO)) {
                valorTodoSaldoExcluido += conta.getValor();
            } else {
                valorTodasContaExcluida += conta.getValor();
            }
        }

        // Mensagem inicial
        String mensagemExcluir = getResources().getString(R.string.mensagemAvisoApagarMaisDeUmaConta);

        // Reutilizar o layout customizado
        View dialogView = getLayoutInflater().inflate(R.layout.menu_dialog_custom_coringa, null);
        TextView textView = dialogView.findViewById(R.id.textViewMenuDialogCustomCoringa);
        Button buttonNao = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaNao);
        Button buttonSim = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaSim);

        // Criar o diálogo
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Etapa 1: Confirmar exclusão
        textView.setText(mensagemExcluir);
        buttonSim.setText(R.string.sim);
        buttonNao.setText(R.string.nao);

        Double finalValorTodasContaExcluida = valorTodasContaExcluida;
        Double finalValorTodoSaldoExcluido = valorTodoSaldoExcluido;

        buttonSim.setOnClickListener(v -> {
            UsuarioDatabase database = UsuarioDatabase.getDatabase(this);

            // Remover as contas e atualizar a lista
            for (Conta conta : listaConta) {
                database.contaDao().delete(conta);
                lista.remove(conta);
            }
            contaAdapter.notifyDataSetChanged();

            // Atualizar o saldo do usuário
            removerContaAdicionalDoSaldoUsuario(finalValorTodoSaldoExcluido);
            colocaSaldoNaTela();

            // Etapa 2: Confirmar retorno do saldo de contas "SAIDA"
            if (finalValorTodasContaExcluida > 0) {
                textView.setText(getResources().getString(R.string.mensagemRetornarValorTodasAsContaSaldo));
                buttonSim.setText(R.string.sim);
                buttonNao.setText(R.string.nao);

                buttonSim.setOnClickListener(c -> {
                    retornaValorContaParaSaldoUsuario(finalValorTodasContaExcluida);
                    colocaSaldoNaTela();
                    dialog.dismiss();
                });

                buttonNao.setOnClickListener(c -> dialog.dismiss());
            } else {
                dialog.dismiss();
            }

            limparSelecionadoNaLista();
        });

        buttonNao.setOnClickListener(v -> {
            dialog.dismiss();
            limparSelecionadoNaLista();
        });

        // Mostrar o diálogo
        dialog.show();
    }

    private void removerContaAdicionalDoSaldoUsuario(Double finalValorTodoSaldoExcluida) {
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        Usuario usuarioSaldoAtualizado = database.usuarioDao().getUsuario().get();
        usuarioSaldoAtualizado.setSaldo(usuarioSaldoAtualizado.getSaldo() - finalValorTodoSaldoExcluida);
        database.usuarioDao().update(usuarioSaldoAtualizado);
        usuario = usuarioSaldoAtualizado;
    }

    private void retornaValorContaParaSaldoUsuario(Double valorContaExcluida){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        usuario.setSaldo(usuario.getSaldo()+valorContaExcluida);
        database.usuarioDao().update(usuario);
    }

    public void mudarParaTelaDeNovaConta() {
        if(usuario != null){
            Intent intent = new Intent(this, ActivityTelaNovaConta.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,
                    R.string.mensagemCrieUsuarioParaAdicionarConta,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void mudarParaTelaDeNovaContaParaEditar(int posicao){
        Intent intent = new Intent(this, ActivityTelaNovaConta.class);
        intent.putExtra("conta_para_editar", lista.get(posicao));
        startActivity(intent);
    }

    public void mudarParaTelaDeNovaContaFormatoLista(){
        if(usuario != null) {
            Intent intent = new Intent(this, ActivityTelaNovaContaFormatoLista.class);
            startActivity(intent);
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
            Toast.makeText(this,
                    R.string.mensagemCrieUsuarioParaGerarPdf,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void showCustomDialog(View v, AdapterView.AdapterContextMenuInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.menu_dialog_custom, null);
        builder.setView(customLayout);

        Button buttonEditarConta = customLayout.findViewById(R.id.buttonEditarConta);
        Button buttonDeletarConta = customLayout.findViewById(R.id.buttonDeletarConta);


        AlertDialog dialog = builder.create();

        buttonEditarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudarParaTelaDeNovaContaParaEditar(info.position);
                dialog.dismiss();
                limparSelecionadoNaLista();
            }
        });

        buttonDeletarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletar(info);
                dialog.dismiss();  // Fecha o diálogo
            }
        });
        dialog.show();
    }
}