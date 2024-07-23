package br.com.contas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import br.com.contas.R;

import java.util.List;

public class ListPdfAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> pdfFiles;

    public ListPdfAdapter(Context context, List<String> pdfFiles) {
        super(context, R.layout.activity_linha_lista_documentos, pdfFiles);
        this.context = context;
        this.pdfFiles = pdfFiles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_linha_lista_documentos, parent, false);
        }

        String currentPdf = pdfFiles.get(position);
        TextView textViewPdfName = convertView.findViewById(R.id.textViewPdfName);
        textViewPdfName.setText(currentPdf);

        return convertView;
    }
}
