package br.com.contas.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import br.com.contas.entities.Usuario;
import br.com.contas.repository.ContasRepository;

public class UsuarioViewModel extends AndroidViewModel {

    private final ContasRepository repository;
    private final MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();

    public UsuarioViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ContasRepository(application);
    }

    public LiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public void loadUsuario() {
        repository.getUsuario(usuarioLiveData::setValue);
    }

    public void salvarUsuario(String nome, Double saldo, boolean editar, ContasRepository.SaveUsuarioCallback callback) {
        repository.saveUsuario(nome, saldo, editar, callback);
    }
}
