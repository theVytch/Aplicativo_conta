package br.com.contas.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.utils.Ordenar;

public class ContasRepository {

    public interface DataCallback<T> {
        void onComplete(T data);
    }

    public interface ActionCallback {
        void onComplete();
    }

    public interface SaveUsuarioCallback {
        void onComplete(boolean success);
    }

    private final UsuarioDatabase database;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public ContasRepository(Context context) {
        this.database = UsuarioDatabase.getDatabase(context.getApplicationContext());
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void loadDashboard(String ordenacao, DataCallback<DashboardData> callback) {
        executor.execute(() -> {
            DashboardData dashboardData = getDashboardData(ordenacao);
            postToMainThread(() -> callback.onComplete(dashboardData));
        });
    }

    public void getUsuario(DataCallback<Usuario> callback) {
        executor.execute(() -> {
            Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
            postToMainThread(() -> callback.onComplete(optionalUsuario.orElse(null)));
        });
    }

    public void getContaById(Long contaId, DataCallback<Conta> callback) {
        executor.execute(() -> {
            Optional<Conta> optionalConta = database.contaDao().getContaById(contaId);
            postToMainThread(() -> callback.onComplete(optionalConta.orElse(null)));
        });
    }

    public void saveUsuario(String nome, Double saldo, boolean editar, SaveUsuarioCallback callback) {
        executor.execute(() -> {
            Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
            boolean success;

            if (optionalUsuario.isPresent()) {
                Usuario usuario = optionalUsuario.get();
                usuario.setNome(nome);
                usuario.setSaldo(saldo);
                database.usuarioDao().update(usuario);
                success = true;
            } else {
                Usuario usuario = new Usuario(nome.trim(), saldo);
                Long id = database.usuarioDao().insert(usuario);
                success = id != null && id > 0;
            }

            boolean finalSuccess = success;
            postToMainThread(() -> callback.onComplete(finalSuccess));
        });
    }

    public void insertConta(Conta conta, double saldoDelta, ActionCallback callback) {
        executor.execute(() -> {
            database.runInTransaction(() -> {
                database.contaDao().insert(conta);
                updateSaldoInterno(saldoDelta);
            });
            postToMainThread(callback::onComplete);
        });
    }

    public void updateConta(Conta conta, double saldoDelta, ActionCallback callback) {
        executor.execute(() -> {
            database.runInTransaction(() -> {
                database.contaDao().update(conta);
                updateSaldoInterno(saldoDelta);
            });
            postToMainThread(callback::onComplete);
        });
    }

    public void deleteConta(Conta conta, double saldoDelta, ActionCallback callback) {
        executor.execute(() -> {
            database.runInTransaction(() -> {
                database.contaDao().delete(conta);
                updateSaldoInterno(saldoDelta);
            });
            postToMainThread(callback::onComplete);
        });
    }

    public void deleteContas(List<Conta> contas, double saldoDelta, ActionCallback callback) {
        executor.execute(() -> {
            database.runInTransaction(() -> {
                for (Conta conta : contas) {
                    database.contaDao().delete(conta);
                }
                updateSaldoInterno(saldoDelta);
            });
            postToMainThread(callback::onComplete);
        });
    }

    public void deleteAllData(ActionCallback callback) {
        executor.execute(() -> {
            database.runInTransaction(() -> {
                database.contaDao().deleteAll();
                database.usuarioDao().deleteAllUsuario();
            });
            postToMainThread(callback::onComplete);
        });
    }

    public void getOrderedContasForUsuarioAtual(String ordenacao, DataCallback<List<Conta>> callback) {
        executor.execute(() -> {
            Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
            List<Conta> contas = new ArrayList<>();
            if (optionalUsuario.isPresent()) {
                contas = Ordenar.retornaListaOrdenada(optionalUsuario.get().getId(), ordenacao, database);
            }
            List<Conta> resultado = contas;
            postToMainThread(() -> callback.onComplete(resultado));
        });
    }

    private DashboardData getDashboardData(String ordenacao) {
        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        if (!optionalUsuario.isPresent()) {
            return DashboardData.empty();
        }

        Usuario usuario = optionalUsuario.get();
        List<Conta> contas = Ordenar.retornaListaOrdenada(usuario.getId(), ordenacao, database);
        Double saldoNecessario = database.contaDao().getContaNecessario(usuario.getId());
        Double saldoDesnecessario = database.contaDao().getContaDesnecessario(usuario.getId());

        return new DashboardData(usuario, contas, saldoNecessario, saldoDesnecessario);
    }

    private void updateSaldoInterno(double saldoDelta) {
        if (saldoDelta == 0) {
            return;
        }

        Optional<Usuario> optionalUsuario = database.usuarioDao().getUsuario();
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setSaldo(usuario.getSaldo() + saldoDelta);
            database.usuarioDao().update(usuario);
        }
    }

    private void postToMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }
}
