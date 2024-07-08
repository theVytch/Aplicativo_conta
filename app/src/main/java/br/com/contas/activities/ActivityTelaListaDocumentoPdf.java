package br.com.contas.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.contas.R;

public class ActivityTelaListaDocumentoPdf extends AppCompatActivity {

    private ListView listViewDocumetos;
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private ArrayList<File> listaDeArquivos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_lista_documento_pdf);

        inicializaComponentes();
        listarArquivosPdf();
        abrirDocumentoComUmClickNaLista();
        registerForContextMenu(listViewDocumetos);

        exibirBotaoVoltar();
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
            deletarDocumentoPdf(info);
        } else {
            return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    private void deletarDocumentoPdf(AdapterView.AdapterContextMenuInfo info) {
        int position = info.position;
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
        listaDeArquivos = listarPdfs();

        listViewDocumetos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File fileSelecionado = listaDeArquivos.get(position);
                abrirPdf(fileSelecionado);
            }
        });
    }

    private void listarArquivosPdf() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            listPdfFiles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listPdfFiles();
            }
        }
    }

    private void inicializaComponentes() {
        listViewDocumetos = findViewById(R.id.listViewDocumentos);
    }

    private void listPdfFiles() {
        // Diretório onde os arquivos PDF estão localizados Android/data/br.com.contas/files/Download/
        File downloadsDirectory = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)));

        List<String> pdfFiles = new ArrayList<>();

        File[] files = downloadsDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    pdfFiles.add(file.getName());
                }
            }
        }

        Collections.sort(pdfFiles,  Collections.reverseOrder());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pdfFiles);
        listViewDocumetos.setAdapter(adapter);
    }

    private ArrayList<File> listarPdfs() {
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
        return pdfList;
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
                    "Não foi possivel abrir o documento!",
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
                    listPdfFiles();
                    Toast.makeText(context, "Arquivo deletado com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Falha ao deletar o arquivo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Arquivo não encontrado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao deletar o arquivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return deleted;
    }

    private void mudarTelaSalvarListaDeContaNoCelular(){
        Intent intent = new Intent(this, ActivityTelaSalvarListaDeContaNoCelular.class);
        startActivity(intent);
    }
}
