package br.edu.utfpr.eduardomelentovytch.contas.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import br.edu.utfpr.eduardomelentovytch.contas.R;

public class UtilsGUI {
    public static void confirmacao(Context context, String mensagem, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.confirmacao);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.sim, listener);
        builder.setNeutralButton(R.string.nao, listener);

        AlertDialog alert = builder.create();
        alert.show();
    }
}

