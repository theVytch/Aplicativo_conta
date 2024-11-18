package br.com.contas.activities;


import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.contas.DAO.UsuarioDao;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.utils.DecimalDigits;

import static org.junit.Assert.*;

import android.content.Context;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class ActivityTelaUsuarioTest {

    private UsuarioDatabase db;
    private UsuarioDao usuarioDao;

    @Rule
    public ActivityScenarioRule<ActivityTelaUsuario> activityRule =
            new ActivityScenarioRule<>(ActivityTelaUsuario.class);

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UsuarioDatabase.class)
                .build();
        usuarioDao = db.usuarioDao();
    }

    @After
    public void closeDB() {
        db.close();
    }


    @Test
    public void salvar_usuario_test() {
        // Mock do valor do DecimalDigits.modeloFormatPattern para evitar o erro de nulo
        DecimalDigits.modeloFormatPattern = "#,###0.00";
        DecimalDigits.idiomaCelular = "en";

        // Inicializa a Activity com ActivityScenario
        // Inicializa a Activity com ActivityScenario
        ActivityScenario<ActivityTelaUsuario> scenario = ActivityScenario.launch(ActivityTelaUsuario.class);

        scenario.onActivity(activity -> {
            // Cria um ExecutorService para rodar a operação de banco em uma thread separada
            ExecutorService executor = Executors.newSingleThreadExecutor();

            // Executa a operação de banco de dados em uma thread separada
            executor.execute(() -> {
                try {
                    // Chama o método de salvar (que faz operações de banco de dados)
                    activity.salvar(db, "Maria", 1000.0);

                    // Verifica se o usuário foi salvo corretamente
                    Usuario usuario = usuarioDao.getNomeUsuario("Maria");
                    assertNotNull(usuario);
                    assertEquals("Maria", usuario.getNome());
                    assertEquals(1000.0, usuario.getSaldo(), 0.01);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Fechar o executor após a tarefa
            executor.shutdown();
        });
    }

    @Test
    public void editar_usuario_test() throws InterruptedException {
        // Mock do valor do DecimalDigits.modeloFormatPattern para evitar o erro de nulo
        DecimalDigits.modeloFormatPattern = "#,###0.00";
        DecimalDigits.idiomaCelular = "en";

        String nomeSalvar = "Maria";
        Double saldoSalvar = 1000.0;
        String nomeEditar = "Eduardo";
        Double saldoEditar = 600.0;

        // Inicializa a Activity com ActivityScenario
        ActivityScenario<ActivityTelaUsuario> scenario = ActivityScenario.launch(ActivityTelaUsuario.class);

        // Usando CountDownLatch para aguardar a conclusão das operações assíncronas
        CountDownLatch latchSalvar = new CountDownLatch(1); // Para salvar
        CountDownLatch latchEditar = new CountDownLatch(1); // Para editar

        // Executa a operação de salvar
        scenario.onActivity(activity -> {
            // Executa a operação de banco de dados em uma thread separada
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // Chama o método de salvar (que faz operações de banco de dados)
                    activity.salvar(db, nomeSalvar, saldoSalvar);
                    // Aguarda a conclusão do método salvar
                    latchSalvar.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            executor.shutdown();
        });

        // Aguarda a conclusão do salvar
        latchSalvar.await();

        // Executa a operação de editar
        scenario.onActivity(activity -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // Chama o método de editar
                    activity.editar(db, "Eduardo", 600.0);
                    // Aguarda a conclusão do método editar
                    latchEditar.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            executor.shutdown();
        });

        // Aguarda a conclusão do editar
        latchEditar.await();

        // Verifica se o usuário foi salvo e editado corretamente
        Usuario usuario = usuarioDao.getNomeUsuario("Eduardo");
        assertNotNull(usuario);
        assertEquals(nomeEditar, usuario.getNome());
        assertEquals(saldoEditar, usuario.getSaldo(), 0.01);
    }
}
