package br.com.contas.utils;

import android.content.Context;

import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;

public class UtilsConta {
    public static void atualizaSaldoUsuario(Context context, Double saldo, Usuario usuario){
        UsuarioDatabase database = UsuarioDatabase.getDatabase(context);
        usuario.setSaldo(usuario.getSaldo() - saldo);
        database.usuarioDao().update(usuario);
    }
}
