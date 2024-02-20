package br.edu.utfpr.eduardomelentovytch.contas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import br.edu.utfpr.eduardomelentovytch.contas.R;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Conta;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Usuario;
import br.edu.utfpr.eduardomelentovytch.contas.persistence.UsuarioDatabase;
import br.edu.utfpr.eduardomelentovytch.contas.utils.PdfGenerator;
import br.edu.utfpr.eduardomelentovytch.contas.utils.UtilsGUI;

public class ActivityTelaSalvarListaDeContaNoCelular extends AppCompatActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_salvar_lista_de_conta_no_celular);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
            } else {
                // Permissão negada
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

    public void deletarTodasContas(View view){
        String mensagem = getResources().getString(R.string.mensagemAvisoApagarTodasContas);
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
                    database.contaDao().deleteAll();
                    database.usuarioDao().deleteAllUsuario();
                    Toast.makeText(this,
                            R.string.mensagemAvisoTodasContasExcluida,
                            Toast.LENGTH_LONG).show();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    // Não fazer nada
                    break;
            }
        };
        UtilsGUI.confirmacao(this, mensagem, listener);
    }

    public void criarPdf(View view){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(this);
        Usuario usuario = database.usuarioDao().getUsuario().get();
        List<Conta> contas = database.contaDao().getListaContasUsuarioOrderByData(usuario.getId());
        PdfGenerator.gerarPdf(contas);
        Toast.makeText(this,
                R.string.mensagemAvisoPdfCriado,
                Toast.LENGTH_LONG).show();
    }

    private void mudarTelaInicial(){
        Intent intent = new Intent(this, ActivityTelaIncialListaConta.class);
        startActivity(intent);
    }
}