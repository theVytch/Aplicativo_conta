package br.com.contas.utils;

public class UtilsValida {

    public static boolean validaCampoPreenchido(String nome, Double valor){
        if(nome.isEmpty() || valor <= 0){
            return false;
        }else{
            return true;
        }
    }
}
