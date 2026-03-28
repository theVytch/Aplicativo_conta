package br.com.contas.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.contas.entities.Conta;
import br.com.contas.repository.ContasRepository;
import br.com.contas.repository.DashboardData;
import br.com.contas.utils.Ordenar;

public class TelaInicialViewModel extends AndroidViewModel {

    private final ContasRepository repository;
    private final MutableLiveData<DashboardData> dashboardLiveData = new MutableLiveData<>(DashboardData.empty());

    public TelaInicialViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ContasRepository(application);
    }

    public LiveData<DashboardData> getDashboardLiveData() {
        return dashboardLiveData;
    }

    public void loadDashboard() {
        repository.loadDashboard(Ordenar.opcaoOrdenacao, dashboardLiveData::setValue);
    }

    public void deleteConta(Conta conta, double saldoDelta) {
        repository.deleteConta(conta, saldoDelta, this::loadDashboard);
    }

    public void deleteContas(List<Conta> contas, double saldoDelta) {
        repository.deleteContas(contas, saldoDelta, this::loadDashboard);
    }
}
