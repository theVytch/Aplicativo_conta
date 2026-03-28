package br.com.contas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

import br.com.contas.R;

public class ListPdfAdapter extends ArrayAdapter<File> {
    private static final Pattern PDF_NAME_PATTERN = Pattern.compile("^(.*)_(\\d{2}-\\d{2}-\\d{4}-\\d{2}:\\d{2}:\\d{2})\\.pdf$");

    private final Context context;
    private final List<File> pdfFiles;

    private static class DocumentoHolder{
        public TextView textViewPdfName;
        public TextView textViewPdfData;
    }

    public ListPdfAdapter(Context context, List<File> pdfFiles) {
        super(context, R.layout.activity_linha_lista_documentos, pdfFiles);
        this.context = context;
        this.pdfFiles = pdfFiles;
    }

    @Override
    public int getCount() {
        return pdfFiles.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DocumentoHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_linha_lista_documentos, parent, false);

            holder = new DocumentoHolder();
            holder.textViewPdfName = convertView.findViewById(R.id.textViewPdfName);
            holder.textViewPdfData = convertView.findViewById(R.id.textViewPdfData);

            convertView.setTag(holder);
        } else {
            holder = (DocumentoHolder) convertView.getTag();
        }

        convertView.setBackgroundResource(R.drawable.linha_lista_background_documento);

        File currentPdf = pdfFiles.get(position);
        String currentPdfName = currentPdf.getName();
        holder.textViewPdfName.setText(retornarTituloDocumento(currentPdfName));
        holder.textViewPdfData.setText(retornarDataDocumento(currentPdfName));

        return convertView;
    }

    private String retornarTituloDocumento(String fileName) {
        Matcher matcher = PDF_NAME_PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        return removerExtensaoPdf(fileName);
    }

    private String retornarDataDocumento(String fileName) {
        Matcher matcher = PDF_NAME_PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return matcher.group(2);
        }

        return "";
    }

    private String removerExtensaoPdf(String fileName) {
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }
}
