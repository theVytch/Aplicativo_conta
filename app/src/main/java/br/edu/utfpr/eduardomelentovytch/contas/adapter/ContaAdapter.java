package br.edu.utfpr.eduardomelentovytch.contas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.eduardomelentovytch.contas.R;
import br.edu.utfpr.eduardomelentovytch.contas.custom.CustomTextView;
import br.edu.utfpr.eduardomelentovytch.contas.entities.Conta;
import br.edu.utfpr.eduardomelentovytch.contas.persistence.converters.DateConverter;

public class ContaAdapter extends BaseAdapter {

    private Context context;
    private List<Conta> contas;

    private static class ContaHolder{
        public TextView textViewNomeConta, textViewData;
        public CustomTextView textViewValorConta;
    }

    public ContaAdapter(Context context, List<Conta> contas){
        this.context = context;
        this.contas = contas;
    }

    @Override
    public int getCount() {
        return contas.size();
    }

    @Override
    public Object getItem(int position) {
        return contas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContaHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_linha_lista_conta, parent, false);

            holder = new ContaHolder();

            holder.textViewNomeConta = convertView.findViewById(R.id.textViewNomeContaLinha);
            holder.textViewValorConta = convertView.findViewById(R.id.textViewValorContaLinha);
            holder.textViewData = convertView.findViewById(R.id.textViewData);

            convertView.setTag(holder);
        } else {
            holder = (ContaHolder) convertView.getTag();
        }

        holder.textViewNomeConta.setText(contas.get(position).getNomeConta());
        holder.textViewValorConta.setText(contas.get(position).getValor().toString());
        holder.textViewData.setText(DateConverter.dateToString(contas.get(position).getData()));

        return convertView;
    }
}
