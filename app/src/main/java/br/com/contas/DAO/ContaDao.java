package br.com.contas.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import br.com.contas.entities.Conta;

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

    @Query("SELECT * FROM Conta Where usuarioId = :id ORDER BY data desc, conta_id DESC")
    List<Conta> getListaContasUsuarioOrderByDataDescAndContaIdDesc(Long id); // ordenar por mais novo adicionado

    @Query("SELECT * FROM Conta Where usuarioId = :id ORDER BY nomeConta ASC")
    List<Conta> getListaContasUsuarioOrderByNomeContaAZ(Long id);

    @Query("SELECT * FROM Conta Where usuarioId = :id ORDER BY nomeConta DESC")
    List<Conta> getListaContasUsuarioOrderByNomeContaZA(Long id);

    @Query("SELECT * FROM Conta Where usuarioId = :id ORDER BY valor DESC")
    List<Conta> getListaContasUsuarioOrderByValorMaiorMenor(Long id);

    @Query("SELECT * FROM Conta Where usuarioId = :id ORDER BY valor ASC")
    List<Conta> getListaContasUsuarioOrderByValorMenorMenor(Long id);

    @Query("SELECT * FROM Conta Where conta_id = :id")
    Optional<Conta> getContaById(Long id);

    @Query("DELETE FROM Conta")
    void deleteAll();

}
