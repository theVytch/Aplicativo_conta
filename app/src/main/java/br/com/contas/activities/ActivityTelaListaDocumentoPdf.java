package br.com.contas.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.contas.R;
import br.com.contas.adapter.ListPdfAdapter;

public class ActivityTelaListaDocumentoPdf extends AppCompatActivity {

    private ListPdfAdapter listPdfAdapter;
    private ListView listViewDocumetos;
    private List<File> listaDeArquivos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_lista_documento_pdf);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializaComponentes();
        atualizarListaDePdfs();
        abrirDocumentoComUmClickNaLista();
        registerForContextMenu(listViewDocumetos);
        abrirTelaDeDeletar();
        exibirBotaoVoltar();
    }

    private void abrirTelaDeDeletar() {
        listViewDocumetos.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmarExclusaoPdf(position);
            return true; // impede que o menu de contexto padrão apareça
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_acao_tela_lista_conta, menu);

        menu.findItem(R.id.menuItemEditarConta).setVisible(false);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (id == R.id.menuItemDeletarConta) {
            //deletarDocumentoPdf(info);
            confirmarExclusaoPdf(info.position);
        } else {
            return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    /*private void deletarDocumentoPdf(AdapterView.AdapterContextMenuInfo info) {
        int position = info.position;
        File fileName = listaDeArquivos.get(position);
        deletarArquivo(this, fileName);
    }*/

    private void deletarDocumentoPdf(int position) {
        File fileName = listaDeArquivos.get(position);
        deletarArquivo(this, fileName);
    }

    private void exibirBotaoVoltar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mudarTelaSalvarListaDeContaNoCelular();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirDocumentoComUmClickNaLista() {
        listViewDocumetos.setOnItemClickListener((parent, view, position, id) -> {
            File fileSelecionado = listaDeArquivos.get(position);
            abrirPdf(fileSelecionado);
        });
    }

    private void inicializaComponentes() {
        listViewDocumetos = findViewById(R.id.listViewDocumentos);
    }

    private void atualizarListaDePdfs() {
        listaDeArquivos = listarPdfs();
        listPdfAdapter = new ListPdfAdapter(this, listaDeArquivos);
        listViewDocumetos.setAdapter(listPdfAdapter);
    }

    private List<File> listarPdfs() {
        ArrayList<File> pdfList = new ArrayList<>();
        File downloadsDirectory = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
        File[] files = downloadsDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    pdfList.add(file);
                }
            }
        }
        //return pdfList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        return pdfList.stream()
                .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
                .collect(Collectors.toList());
    }

    private void abrirPdf(File file) {
        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,
                    getResources().getString(R.string.nao_foi_possivel_abrir_o_documento),
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean deletarArquivo(Context context, File file) {
        boolean deleted = false;
        try {
            // Verifica se o arquivo existe antes de tentar deletar
            if (file.exists()) {
                deleted = file.delete(); // Tenta deletar o arquivo
                if (deleted) {
                    atualizarListaDePdfs();
                    Toast.makeText(context, context.getString(R.string.arquivo_deletado_com_sucesso), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.falha_ao_deletar_o_arquivo), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.arquivo_nao_encontrado), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.erro_ao_deletar_o_arquivo) + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return deleted;
    }

    private void confirmarExclusaoPdf(int posicao) {
        String nomeArquivo = listaDeArquivos.get(posicao).getName();
        String mensagem = "Deseja excluir o PDF \"" + nomeArquivo + "\"?";

        View dialogView = getLayoutInflater().inflate(R.layout.menu_dialog_custom_coringa, null);
        TextView textView = dialogView.findViewById(R.id.textViewMenuDialogCustomCoringa);
        Button buttonNao = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaNao);
        Button buttonSim = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaSim);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        textView.setText(mensagem);
        buttonSim.setText(R.string.sim);
        buttonNao.setText(R.string.nao);

        buttonSim.setOnClickListener(v -> {
            /*File file = new File(nomeArquivo);
            if (file.exists()) {*/
                deletarDocumentoPdf(posicao);
            //}
            dialog.dismiss();
        });

        buttonNao.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void mudarTelaSalvarListaDeContaNoCelular(){
        Intent intent = new Intent(this, ActivityTelaSalvarListaDeContaNoCelular.class);
        startActivity(intent);
    }
}
