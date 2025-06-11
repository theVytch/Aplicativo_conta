package br.com.contas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import br.com.contas.R;
import br.com.contas.custom.CustomTextView;

import java.util.List;

public class ListPdfAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> pdfFiles;

    private static class DocumentoHolder{
        public TextView textViewPdfName;
        public TextView textViewPdfData;
    }

    public ListPdfAdapter(Context context, List<String> pdfFiles) {
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
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DocumentoHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_linha_lista_documentos, parent, false);

            holder = new DocumentoHolder();
            holder.textViewPdfName = convertView.findViewById(R.id.textViewPdfName); // use o ID correto aqui
            holder.textViewPdfData = convertView.findViewById(R.id.textViewPdfData); // use o ID correto aqui

            convertView.setTag(holder);
        }else {
            holder = (ListPdfAdapter.DocumentoHolder) convertView.getTag();
        }

        convertView.setBackgroundResource(R.drawable.linha_lista_background_documento);

        String currentPdf = pdfFiles.get(position);
        holder.textViewPdfName.setText(currentPdf.substring(0, currentPdf.length() - 24));

        String pdfInvertido = new StringBuilder(currentPdf).reverse().toString();
        String dataDoc = pdfInvertido.substring(4, 23);

        holder.textViewPdfData.setText(new StringBuffer(dataDoc).reverse().toString());

        return convertView;
    }
}
