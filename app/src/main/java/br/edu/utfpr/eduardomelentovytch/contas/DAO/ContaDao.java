package br.edu.utfpr.eduardomelentovytch.contas.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import br.edu.utfpr.eduardomelentovytch.contas.entities.Conta;

@Dao
public interface ContaDao {

    @Insert
    Long insert(Conta conta);

    @Delete
    void delete(Conta conta);

    @Update
    void update(Conta conta);

    @Query("SELECT * FROM Conta Where usuarioId = :id")
    List<Conta> getListaContasUsuario(Long id);

    @Query("SELECT * FROM Conta Where usuarioId = :id ORDER BY data desc")
    List<Conta> getListaContasUsuarioOrderByData(Long id);

    @Query("SELECT * FROM Conta Where conta_id = :id")
    Optional<Conta> getContaById(Long id);

    @Query("DELETE FROM Conta")
    void deleteAll();

}
