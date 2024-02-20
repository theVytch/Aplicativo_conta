package br.edu.utfpr.eduardomelentovytch.contas.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import br.edu.utfpr.eduardomelentovytch.contas.entities.Conta;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Usuario;

@Dao
public interface UsuarioDao {

    @Insert
    Long insert(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    @Query("DELETE FROM Usuario")
    void deleteAllUsuario();

    @Update
    void update(Usuario usuario);

    @Query("SELECT * FROM Usuario ORDER BY id ASC")
    List<Usuario> queryAll();

    @Query("SELECT * FROM Usuario LIMIT 1 ")
    Optional<Usuario> getUsuario();

    @Query("SELECT * FROM Usuario WHERE nome = :nome")
    Usuario getNomeUsuario(String nome);


}
