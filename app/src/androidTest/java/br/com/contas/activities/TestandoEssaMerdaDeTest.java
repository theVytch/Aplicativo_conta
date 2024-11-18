package br.com.contas.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.contas.DAO.ContaDao;
import br.com.contas.DAO.UsuarioDao;
import br.com.contas.R;
import br.com.contas.activities.telas_conta.FragmentTelaContaAdicao;
import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.persistence.converters.DateConverter;
import br.com.contas.utils.DecimalDigits;

public class TestandoEssaMerdaDeTest {


    private FragmentScenario<FragmentTelaContaAdicao> fragmentScenario;
    private UsuarioDatabase db;
    private UsuarioDao usuarioDao;
    private ContaDao contaDao;

    private Usuario usuario;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.databaseBuilder(context, UsuarioDatabase.class, "nome_do_banco_real")
                .allowMainThreadQueries() // Permite consultas na thread principal, para testes
                .build();
        usuarioDao = db.usuarioDao();
        contaDao = db.contaDao();
    }

    @After
    public void tearDown() {
        db.close(); // Fecha o banco de dados após cada teste
    }

    @Test
    public void salvar_adicao_conta_test() {
        Usuario usuario1 = usuarioDao.getNomeUsuario("sf");
        //Usuario usuario = usuario1;
        Log.e("Usuario", usuario1.getNome() + " -- " + usuario1.getSaldo());
    }
}