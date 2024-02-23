package br.com.contas.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import br.com.contas.DAO.ContaDao;
import br.com.contas.DAO.UsuarioDao;
import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;


@Database(entities = {Usuario.class, Conta.class}, version = 2, exportSchema = false)
public abstract class UsuarioDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();
    public abstract ContaDao contaDao();

    private static UsuarioDatabase instance;

    public static UsuarioDatabase getDatabase(final Context context){
        if(instance == null){
            synchronized (UsuarioDatabase.class){
                if(instance == null){
                    instance = Room.databaseBuilder(context,
                                                    UsuarioDatabase.class,
                                                    "usuarios.db")
                                                    .fallbackToDestructiveMigration()
                                                    .allowMainThreadQueries()
                                                    .build();
                }
            }
        }
        return instance;
    }
}
