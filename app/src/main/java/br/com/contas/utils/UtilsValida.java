package br.com.contas.utils;

public class UtilsValida {

    public static boolean validaCampoPreenchido(String nome, Double valor){
        return nome != null && !nome.trim().isEmpty() && valor != null && valor > 0;
    }
}
