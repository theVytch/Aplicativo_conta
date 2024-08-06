package br.com.contas.utils;

import java.util.List;

import br.com.contas.entities.Conta;
import br.com.contas.persistence.UsuarioDatabase;

public class Ordenar {

    public static String opcaoOrdenacao = "orderDataDesc";

    public static List<Conta> retornaListaOrdenada(Long usuarioId, String ordenar, UsuarioDatabase database){
        List<Conta> listaOrdenada;
        if(ordenar.equals("orderDataDesc")){
            listaOrdenada = database.contaDao().getListaContasUsuarioOrderByDataDescAndContaIdDesc(usuarioId);
        } else if (ordenar.equals("orderDataAsc")) {
            listaOrdenada = database.contaDao().getListaContasUsuarioOrderByDataAscAndContaIdAsc(usuarioId);
        } else if (ordenar.equals("orderNomeDesc")) {
            listaOrdenada = database.contaDao().getListaContasUsuarioOrderByNomeContaZA(usuarioId);
        } else if (ordenar.equals("orderNomeAsc")) {
            listaOrdenada = database.contaDao().getListaContasUsuarioOrderByNomeContaAZ(usuarioId);
        } else if (ordenar.equals("orderValorDesc")) {
            listaOrdenada = database.contaDao().getListaContasUsuarioOrderByValorMaiorMenor(usuarioId);
        }else {
            listaOrdenada = database.contaDao().getListaContasUsuarioOrderByValorMenorMaior(usuarioId);
        }
        return listaOrdenada;
    }
}
