package br.edu.utfpr.eduardomelentovytch.contas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Optional;

import br.edu.utfpr.eduardomelentovytch.contas.R;
import br.edu.utfpr.eduardomelentovytch.contas.adapter.ContaAdapter;
import br.edu.utfpr.eduardomelentovytch.contas.custom.CustomTextView;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Conta;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Usuario;
import br.edu.utfpr.eduardomelentovytch.contas.persistence.UsuarioDatabase;

import br.edu.utfpr.eduardomelentovytch.contas.utils.UtilsGUI;

public class ActivityTelaIncialListaConta extends AppCompatActivity {

    private CustomTextView textViewSaldoUsuarioTelaList;
    private ListView listViewContas;
    private ContaAdapter contaAdapter;
    private List<Conta> lista;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_incial_lista_conta);

        inicializaComponentes();
        verificaSeUsuarioExiste();

        registerForContextMenu(listViewContas);
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
        getMenuInflater().inflate(R.menu.menu_acao_tela_lista_conta, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (id == R.id.menuItemEditarConta) {
            mudarParaTelaDeNovaContaParaEditar(info.position);
        } else if (id == R.id.menuItemDeletarConta) {
            deletarConta(info.position);
        } else {
            return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
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
        lista = database.contaDao().getListaContasUsuarioOrderByData(usuario.getId());
        if (lista != null) {
            atualizarLista();
        }
    }

    public void atualizarLista() {
        contaAdapter = new ContaAdapter(this, lista);
        contaAdapter.notifyDataSetChanged();
        listViewContas.setAdapter(contaAdapter);
    }

    public void adicionarNovaConta(View view){
        mudarParaTelaDeNovaConta();
    }

    public void deletarConta(int posicao){
        String mensagem = getResources().getString(R.string.mensagemAvisoApagar) + "\n" + lista.get(posicao).getNomeConta() + " ?";
        Double valorContaExcluida = lista.get(posicao).getValor();
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
                    database.contaDao().delete(lista.get(posicao));
                    lista.remove(posicao);
                    contaAdapter.notifyDataSetChanged();

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
        Intent intent = new Intent(this, ActivityTelaSalvarListaDeContaNoCelular.class);
        startActivity(intent);
    }
}