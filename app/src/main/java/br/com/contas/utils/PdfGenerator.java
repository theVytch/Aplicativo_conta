package br.com.contas.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.contas.R;
import br.com.contas.entities.Conta;

public class PdfGenerator {

    private static final String FORMAT_DATA = "dd-MM-yyyy-HH:mm:ss";
    private static final String TAG = "PdfGenerator";
    public static String localSalvoArquivo = "";

    public static boolean gerarPdf(List<Conta> contas, Activity activity) {
        try {
            Date dataAtual = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);

            Document document = new Document();

            File pdfFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                pdfFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "minha_lista_de_contas_" + sdf.format(dataAtual) + ".pdf");
                localSalvoArquivo = R.string.msgLocalSalvo + "Android/data/br.com.contas/files/Download";
            } else {
                pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "minha_lista_de_contas_" + sdf.format(dataAtual) + ".pdf");
            }

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            document.add(new Phrase(R.string.app_name));
            document.add(new Phrase("\n\n"));
            for (Conta conta : contas) {
                document.add(new Phrase(conta.toString() + "\n"));
            }

            document.close();
            writer.close();
            Log.d(TAG, R.string.msgPdfGerado + pdfFile.getAbsolutePath());
            return true;
        } catch (DocumentException | FileNotFoundException e) {
            Log.e(TAG, R.string.msgErroPdfGerado + e.getMessage(), e);
            return false;
        }
    }
}