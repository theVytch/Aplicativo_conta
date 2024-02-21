package br.edu.utfpr.eduardomelentovytch.contas.utils;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.compose.ui.text.Paragraph;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.eduardomelentovytch.contas.R;
import br.edu.utfpr.eduardomelentovytch.contas.activities.ActivityTelaSalvarListaDeContaNoCelular;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Conta;

public class PdfGenerator {

    private static final String FORMAT_DATA = "dd-MM-yyyy-HH:mm:ss";
    private static final String TAG = "PdfGenerator";

    public static boolean gerarPdf(List<Conta> contas) {
        try {
            Date dataAtual = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);

            Document document = new Document();
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "minha_lista_de_contas_"+sdf.format(dataAtual)+".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            Font font14 = new Font(Font.FontFamily.HELVETICA, 16);
            Font font12 = new Font(Font.FontFamily.HELVETICA, 14);
            document.open();

            // Adicione o conteúdo do PDF (por exemplo, as contas) ao documento
            document.add(new Phrase("Contas", font14));
            document.add(new Phrase("\n\n"));
            for (Conta conta : contas) {
                document.add(new Phrase(conta.toString() + "\n", font12));
            }

            // Feche o documento
            document.close();
            Log.d(TAG, "PDF gerado com sucesso: " + pdfFile.getAbsolutePath());
            return true;
        } catch (DocumentException | FileNotFoundException e) {
            Log.e(TAG, "Erro ao gerar PDF: " + e.getMessage(), e);
            return false;
        }
    }
}
