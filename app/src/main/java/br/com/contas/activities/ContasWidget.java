package br.com.contas.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import br.com.contas.R;
import br.com.contas.entities.Usuario;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.utils.DecimalDigits;

/**
 * Implementation of App Widget functionality.
 */
public class ContasWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widget = new ComponentName(context, ContasWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.contas_widget);


        DecimalFormat df = new DecimalFormat(DecimalDigits.modeloFormatPattern);
        String formattedValue = df.format(Double.parseDouble(getSaldo(context)));

        //String saldo = DecimalDigits.formatarNumero(Double.parseDouble(getSaldo(context)));
        String cifra = context.getString(R.string.cifra);
        views.setTextViewText(R.id.appwidget_textConta, cifra + formattedValue + " ");

        //abrir aplicativo com clicl no widget
        abrirAplicativoAoClicarNoWidget(context, views);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void abrirAplicativoAoClicarNoWidget(Context context, RemoteViews views) {
        Intent intent = new Intent(context, ActivityTelaIncialListaConta.class); // Substitua MainActivity pela atividade desejada
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.appwidget_textConta, pendingIntent);
    }

    private static String getSaldo(Context context) {
        UsuarioDatabase database = UsuarioDatabase.getDatabase(context);
        Usuario usuario = null;
        try {
            // Utilize um FutureTask para executar a consulta na thread principal
            Callable<Usuario> callable = () -> database.usuarioDao().getUsuario().get();
            FutureTask<Usuario> futureTask = new FutureTask<>(callable);
            new Thread(futureTask).start();
            usuario = futureTask.get(); // Pode lançar InterruptedException ou ExecutionException
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (usuario != null) {
            return getNumeroParaString(usuario.getSaldo());
        } else {
            return "0.00"; // Mensagem de erro padrão
        }
    }

    private static String getNumeroParaString(Double saldo) {
        String saldoStr;
        if (DecimalDigits.idiomaCelular.equals("en")) {
            // Formato americano: 1,000.00
            saldoStr = saldo.toString();
            saldoStr = saldoStr.replace(",", "");
        } else {
            // Formato brasileiro: 1.000,00
            saldoStr = saldo.toString();
            saldoStr = saldoStr.replace(",", ".");
        }
        return saldoStr;
    }
}