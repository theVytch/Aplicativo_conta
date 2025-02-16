package br.com.contas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.contas.R;
import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.utils.Ordenar;
import br.com.contas.utils.PdfGenerator;
import br.com.contas.utils.UtilsGUI;

public class ActivityTelaSalvarListaDeContaNoCelular extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private TextView textViewSobre;
    private TextView textViewLocalSalvoPdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_salvar_lista_de_conta_no_celular);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iniciaComponente();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            requestPermissionsIfNecessary(permissions);
        }

        exibirBotaoVoltar();
    }

    private void iniciaComponente(){
        textViewLocalSalvoPdf = findViewById(R.id.textViewLocalSalvoPdf);
        textViewSobre = findViewById(R.id.textViewSobre);
        textViewSobre.setMovementMethod(new ScrollingMovementMethod());
    }

    private void exibirBotaoVoltar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        boolean permissionsNeeded = false;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded = true;
                break;
            }
        }

        if (permissionsNeeded) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mudarTelaInicial();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deletarTodasContas(View view) {
        String mensagem = getResources().getString(R.string.mensagemAvisoApagarTodasContas);

        // Infla o layout customizado para o diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.menu_dialog_custom_coringa, null);
        TextView textView = dialogView.findViewById(R.id.textViewMenuDialogCustomCoringa);
        Button buttonNao = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaNao);
        Button buttonSim = dialogView.findViewById(R.id.buttonContaMenuDialogCustomCoringaSim);

        // Configura o diálogo customizado
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        textView.setText(mensagem);
        buttonSim.setText(R.string.sim);
        buttonNao.setText(R.string.nao);

        // Aqui você define o tamanho do LinearLayout
        LinearLayout linearLayout = dialogView.findViewById(R.id.linearLayoutDialog); // Certifique-se de que o LinearLayout tenha esse ID no XML
        linearLayout.setMinimumHeight(400);

        buttonSim.setOnClickListener(v -> {
            UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
            database.contaDao().deleteAll();
            database.usuarioDao().deleteAllUsuario();

            //Toast.makeText(this, R.string.mensagemAvisoTodasContasExcluida, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            mudarTelaInicial();
        });

        buttonNao.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void criarPdf(View view){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        Usuario usuario = database.usuarioDao().getUsuario().get();
        //List<Conta> contas = database.contaDao().getListaContasUsuarioOrderByDataDescAndContaIdDesc(usuario.getId());
        List<Conta> contas = Ordenar.retornaListaOrdenada(usuario.getId(), Ordenar.opcaoOrdenacao, database);
        PdfGenerator pdfGen = new PdfGenerator(view.getContext());
        if(pdfGen.gerarPdf(contas, this, usuario)) {

            Toast.makeText(this,
                    R.string.mensagemAvisoPdfCriado,
                    Toast.LENGTH_LONG).show();
            textViewLocalSalvoPdf.setText(PdfGenerator.localSalvoArquivo);
        }else{
            Toast.makeText(this,
                    R.string.mensagemAvisoPdfErro,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void mudarTelaInicial(){
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }

    public void abrirTelaDoc(View view){
        Intent intent = new Intent(this, ActivityTelaListaDocumentoPdf.class);
        startActivity(intent);
    }
}