package br.com.contas.utils;

import java.util.List;

import br.com.contas.entities.Conta;
import br.com.contas.persistence.UsuarioDatabase;

public class Ordenar {

    public static String radio_size = "Blind";
    public static int layout_size = 100;

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

    public static int retornaTamanhoEscolhido(){
        if (radio_size.equals("Small")){
            layout_size = 70;
        } else if (radio_size.equals("Middle")){
            layout_size = 80;
        } else if (radio_size.equals("Large")) {
            layout_size = 90;
        } else {
            layout_size = 100;
        }
        return layout_size;
    }
}
