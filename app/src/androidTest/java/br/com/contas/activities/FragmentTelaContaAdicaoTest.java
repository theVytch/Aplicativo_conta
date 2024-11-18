package br.com.contas.activities;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
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

import java.util.Date;

public class FragmentTelaContaAdicaoTest {


    private FragmentScenario<FragmentTelaContaAdicao> fragmentScenario;
    private UsuarioDatabase db;
    private UsuarioDao usuarioDao;
    private ContaDao contaDao;

    private Usuario usuario;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UsuarioDatabase.class)
                .allowMainThreadQueries() // Permite executar em thread principal, útil para testes
                .build();
        usuarioDao = db.usuarioDao();
        contaDao = db.contaDao();

        salvar_usuario_para_test();

        fragmentScenario = FragmentScenario.launchInContainer(
                FragmentTelaContaAdicao.class,
                null,
                R.style.Theme_Contas_Light
        );

        fragmentScenario.moveToState(Lifecycle.State.STARTED);
    }

    @After
    public void tearDown() {
        db.close(); // Fecha o banco de dados após cada teste
    }

    @Test
    public void salvar_adicao_conta_test() {
        DecimalDigits.modeloFormatPattern = "#,###0.00";
        DecimalDigits.idiomaCelular = "en";

        //salvar_usuario_para_test();

        fragmentScenario.onFragment(fragment -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            executor.execute(() -> {
                try {
                    Usuario usuario = usuarioDao.getNomeUsuario("Eduardo");
                    //Usuario usuario = new Usuario("Eduado", 1000.0);
                    String nomeDaConta = "Pizza";
                    Double valorDaConta = 200.0;
                    String data = "12/11/2024";

                    fragment.salvarConta(nomeDaConta, valorDaConta, data, usuario, db);

                    List<Conta> listaConta = contaDao.getListaContasUsuario(usuario.getId());
                    Conta conta = listaConta.stream()
                            .filter(c -> c.getNomeConta().equals(nomeDaConta))
                            .findAny()
                            .get();
                    assertNotNull(conta);
                    assertEquals(nomeDaConta, conta.getNomeConta());
                    assertEquals(valorDaConta, conta.getValor(), 1);
                    assertEquals(data, DateConverter.dateToString(conta.getData()));
                    assertEquals("SAIDA", conta.getTipo());
                    Usuario usu = usuarioDao.getUsuario().get();
                    assertEquals(1001.0, usu.getSaldo(), 1);
                    Log.e("Usuario: ", usu.getNome() + " - " + usu.getSaldo());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    executor.shutdownNow(); // Encerra o executor após a execução
                }
            });
        });
    }

    public void salvar_usuario_para_test() {
        usuarioDao.insert(new Usuario("Eduardo", 1000.0));

        // Verifique se o usuário foi inserido corretamente
        Optional<Usuario> optionalUsuario = usuarioDao.getUsuario();
        if (optionalUsuario.isPresent()) {
            usuario = optionalUsuario.get();
        } else {
            throw new AssertionError("Usuário não encontrado após inserção.");
        }
    }

}