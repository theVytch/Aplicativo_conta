package br.com.contas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Set;

import br.com.contas.R;
import br.com.contas.custom.CustomTextView;
import br.com.contas.entities.Conta;
import br.com.contas.persistence.converters.DateConverter;
import br.com.contas.utils.Ordenar;

public class ContaAdapter extends BaseAdapter {

    private Context context;
    private List<Conta> contas;
    private Set<Integer> posicaoSelecionada;

    private static class ContaHolder{
        public TextView textViewNomeConta, textViewData;
        public CustomTextView textViewValorConta;
    }

    public ContaAdapter(Context context, List<Conta> contas, Set<Integer> posicaoSelecionada){
        this.context = context;
        this.contas = contas;
        this.posicaoSelecionada = posicaoSelecionada;
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

            //Ajustar tamanho da linha
            defineTamanhoDoLayoutDaLinhaNaLista(convertView);

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


        if (contas.get(position).getTipo().equals("ENTRADA")) {
            convertView.setBackgroundResource(R.drawable.linha_lista_background_adicao_saldo);
        } else {
            convertView.setBackgroundResource(R.drawable.linha_lista_background);
        }

        convertView.setActivated(posicaoSelecionada.contains(position));
        return convertView;
    }

    private void defineTamanhoDoLayoutDaLinhaNaLista(View convertView) {
        ConstraintLayout constraintLayout = convertView.findViewById(R.id.constraintLayoutExemplo);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
        float density = context.getResources().getDisplayMetrics().density;
        int heightInPixels = (int) (Ordenar.retornaTamanhoEscolhido() * density); // 90dp para pixels

        // Define a altura da ConstraintLayout para 90dp (convertido para pixels)
        layoutParams.height = heightInPixels;
        // Aplica mudanças no layout
        constraintLayout.setLayoutParams(layoutParams);
    }
}
