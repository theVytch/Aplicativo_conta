package br.com.contas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import br.com.contas.R;
import br.com.contas.custom.CustomTextView;
import br.com.contas.entities.Conta;
import br.com.contas.persistence.UsuarioDatabase;
import br.com.contas.persistence.converters.DateConverter;

public class ContaAdapter extends BaseAdapter {

    private Context context;
    private List<Conta> listaContas;

    private static class ContaHolder{
        public TextView textViewNomeConta, textViewData;
        public CustomTextView textViewValorConta;
        public ConstraintLayout constraintLinhaLista;
    }

    public ContaAdapter(Context context, List<Conta> contas){
        this.context = context;
        this.listaContas = contas;
    }

    @Override
    public int getCount() {
        return listaContas.size();
    }

    @Override
    public Object getItem(int position) {
        return listaContas.get(position);
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
            holder.constraintLinhaLista = convertView.findViewById(R.id.contraintLinhaConta);

            convertView.setTag(holder);
        } else {
            holder = (ContaHolder) convertView.getTag();
        }

        //Conta conta = listaContas.get(position);
        // Define a cor de fundo do item selecionado
        if (DateConverter.verificaDataFutura(listaContas.get(position).getData())) {
            holder.constraintLinhaLista.setBackgroundResource(R.drawable.background_futuro); // Define a cor de fundo como verde
        }


        holder.textViewNomeConta.setText(listaContas.get(position).getNomeConta());
        holder.textViewValorConta.setText(listaContas.get(position).getValor().toString());
        holder.textViewData.setText(DateConverter.dateToString(listaContas.get(position).getData()));

        return convertView;
    }
}
