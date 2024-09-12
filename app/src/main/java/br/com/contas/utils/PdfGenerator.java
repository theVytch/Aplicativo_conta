package br.com.contas.utils;

import static br.com.contas.utils.DecimalDigits.formatarNumero;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.contas.R;
import br.com.contas.entities.Conta;
import br.com.contas.entities.Usuario;

public class PdfGenerator {
    private static final String FORMAT_DATA = "dd-MM-yyyy-HH:mm:ss";
    private static final String FORMAT_DATA_DOCUMENT = "dd/MM/yyyy";
    private static final String TAG = "PdfGenerator";
    public static String localSalvoArquivo = "";

    private Context context;

    // Constructor
    public PdfGenerator(Context context) {
        this.context = context;
    }

    public boolean gerarPdf(List<Conta> contas, Activity activity, Usuario usuario) {
        try {
            Date dataAtual = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);
            SimpleDateFormat sdfDocument = new SimpleDateFormat(FORMAT_DATA_DOCUMENT);

            Document document = new Document();

            File pdfFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                pdfFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.minha_lista_de_contas_) + sdf.format(dataAtual) + context.getString(R.string.pdf));
                localSalvoArquivo = context.getString(R.string.msgLocalSalvo) + " " + pdfFile.getPath();
            } else {
                pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.minha_lista_de_contas_) + sdf.format(dataAtual) + context.getString(R.string.pdf));
            }

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // começa aqui

            // Definir fonte
            Font font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font font14 = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);
            Font font15 = new Font(Font.FontFamily.HELVETICA, 15, Font.NORMAL);

            // Adicionar título HEAD
            // Cabecalho com o titulo e com cor
            PdfPTable tableHead = new PdfPTable(1); // Número de colunas
            tableHead.setWidthPercentage(100);
            BaseColor customColor = new BaseColor(116, 190, 225);
            BaseColor customColorGreen = new BaseColor(144, 238, 144);
            //String titulo = "CONTAS"; -- teste
            String titulo = context.getString(R.string.app_name);
            String dataGerada = sdfDocument.format(dataAtual);

            PdfPCell cellHeadDate = new PdfPCell(new Phrase(dataGerada, font12));
            cellHeadDate.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellHeadDate.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellHeadDate.setBorder(PdfPCell.NO_BORDER);
            tableHead.addCell(cellHeadDate);

            PdfPCell cellHead = new PdfPCell(new Phrase(titulo, font15));
            cellHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellHead.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellHead.setBackgroundColor(customColor);
            tableHead.addCell(cellHead);

            // Espaco em branco
            Paragraph espacoBranco = new Paragraph();
            espacoBranco.setSpacingBefore(10);

            // Criar tabela Body
            PdfPTable tableBody = new PdfPTable(3);
            tableBody.setWidthPercentage(100);

            // Cabeçalhos da tabela
            criandoCabecacolhoDaTabelaBody(font14, tableBody);

            // Adicionar linhas alternadas entre branco e cinza
            boolean alternateColor = false;
            double resultadoFinal = 0.0;
            for (Conta conta : contas) {
                BaseColor backgroundColor = "ENTRADA".equals(conta.getTipo()) ? customColorGreen : (alternateColor ? BaseColor.WHITE : BaseColor.LIGHT_GRAY);

                adicionandoInformacoesNasColuna(conta, font12, backgroundColor, tableBody, sdfDocument);
                if(conta.getTipo().equals("SAIDA")){
                    resultadoFinal += conta.getValor();
                }
                alternateColor = !alternateColor;
            }

            // Tabela Resultado Final
            PdfPTable tableBodyResult = new PdfPTable(3);
            tableBodyResult.setWidthPercentage(100);
            adicionandoInformacoesNaColunaFinal(tableBodyResult, resultadoFinal, font14, usuario);

            //Adiciona tudo e reza
            document.add(tableHead);
            document.add(espacoBranco);
            document.add(tableBody);
            document.add(espacoBranco);
            document.add(tableBodyResult);
            document.close();
            writer.close();

            Log.d(TAG, context.getString(R.string.pdf_gerado_com_sucesso_em) + pdfFile.getAbsolutePath());
            return true;
        } catch (DocumentException | FileNotFoundException e) {
            Log.e(TAG, context.getString(R.string.erro_ao_gerar_pdf) + e.getMessage(), e);
            return false;
        }
    }

    // teste ee-----------------------------------
    private void criandoCabecacolhoDaTabelaBody(Font font14, PdfPTable tableBody) {
        String[] cabecalhos = {context.getString(R.string.Data),context.getString(R.string.Nome),context.getString(R.string.Preco)};

        PdfPCell cellData = new PdfPCell(new Phrase(cabecalhos[0], font14));
        cellData.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellData.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellData.setBackgroundColor(BaseColor.WHITE);
        cellData.setBorder(PdfPCell.NO_BORDER);
        tableBody.addCell(cellData);

        PdfPCell cellNome = new PdfPCell(new Phrase(cabecalhos[1], font14));
        cellNome.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellNome.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellNome.setBackgroundColor(BaseColor.WHITE);
        cellNome.setBorder(PdfPCell.NO_BORDER);
        tableBody.addCell(cellNome);

        PdfPCell cellValor = new PdfPCell(new Phrase(cabecalhos[2], font14));
        cellValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValor.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellValor.setBackgroundColor(BaseColor.WHITE);
        cellValor.setBorder(PdfPCell.NO_BORDER);
        tableBody.addCell(cellValor);
    }

    private void adicionandoInformacoesNasColuna(Conta conta, Font font12, BaseColor backgroundColor, PdfPTable table, SimpleDateFormat sdfDocument) {

        PdfPCell cellData = new PdfPCell(new Phrase(sdfDocument.format(conta.getData()), font12));
        cellData.setHorizontalAlignment(Element.ALIGN_CENTER); // Alinhar horizontalmente ao centro
        cellData.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellData.setBorder(PdfPCell.NO_BORDER);
        cellData.setBackgroundColor(backgroundColor);
        table.addCell(cellData);

        PdfPCell cellNome = new PdfPCell(new Phrase(conta.getNomeConta(), font12));
        cellNome.setBackgroundColor(backgroundColor);
        cellNome.setBorder(PdfPCell.NO_BORDER);
        cellNome.setHorizontalAlignment(Element.ALIGN_LEFT); // Alinhar horizontalmente ao centro
        cellNome.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellNome);

        String cifra = context.getString(R.string.cifra_com_espaco_e_sinal_de_subtracao);
        if(conta.getTipo().equals("ENTRADA")){
            cifra = context.getString(R.string.cifra_com_espaco_e_sinal_de_adicao);
        }
        PdfPCell cellValor = new PdfPCell(new Phrase(cifra + formatarNumero(conta.getValor()), font12));
        cellValor.setBackgroundColor(backgroundColor);
        cellValor.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alinhar horizontalmente ao centro
        cellValor.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellValor.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cellValor);

    }

    private void adicionandoInformacoesNaColunaFinal(PdfPTable tableBodyResult, Double resultadoTotal, Font font14, Usuario usuario){
        //String resultadoString = context.getString(R.string.total_com_espaco) + context.getString(R.string.cifra_com_espaco) + formatarNumero(resultadoTotal);
        String resultadoString = context.getString(R.string.total_com_espaco) + "  " + context.getString(R.string.cifra_com_espaco) + " " + formatarNumero(resultadoTotal);
        String usuarioInfo = context.getString(R.string.Entrada_tres_espaco_com_cifra) + "   " + context.getString(R.string.cifra) + formatarNumero(resultadoTotal + usuario.getSaldo()) + "\n" +
                context.getString(R.string.Restante_espaco_com_cifra) + "  " + context.getString(R.string.cifra) +  formatarNumero(usuario.getSaldo());
        String[] lista = {usuarioInfo,"", resultadoString};


        PdfPCell cellZero = new PdfPCell(new Phrase(lista[0], font14));
        cellZero.setBorder(PdfPCell.NO_BORDER);
        cellZero.setBorderWidthTop(1f);
        cellZero.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellZero.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellZero.setBackgroundColor(BaseColor.WHITE);
        tableBodyResult.addCell(cellZero);

        //eXtreme Go Horse--------------
        gambiarraParaDeixarApenasUmaColunaComResultadoEnquantoAsOutrasTresFicamEmBranco(tableBodyResult, font14, lista);
        //------------------------------

        // Coluna resultado final
        PdfPCell cellDoisResult = new PdfPCell(new Phrase(lista[2], font14));
        cellDoisResult.setBorder(PdfPCell.NO_BORDER);
        cellDoisResult.setBorderWidthTop(1f);
        cellDoisResult.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellDoisResult.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellDoisResult.setBackgroundColor(BaseColor.YELLOW);
        tableBodyResult.addCell(cellDoisResult);
    }

    private void gambiarraParaDeixarApenasUmaColunaComResultadoEnquantoAsOutrasTresFicamEmBranco(PdfPTable tableBodyResult, Font font14, String[] lista) {
        PdfPCell cellUm = new PdfPCell(new Phrase(lista[1], font14));
        cellUm.setBorder(PdfPCell.NO_BORDER);
        cellUm.setBackgroundColor(BaseColor.WHITE);
        tableBodyResult.addCell(cellUm);
    }

}