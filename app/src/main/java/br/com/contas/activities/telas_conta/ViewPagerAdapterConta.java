package br.com.contas.activities.telas_conta;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import br.com.contas.entities.Conta;

public class ViewPagerAdapterConta extends FragmentStateAdapter {

    private Conta contaParaEditar;
    private FragmentTelaContaSubtracao fragmentSubtracao;
    private FragmentTelaContaAdicao fragmentAdicao;

    private Fragment currentFragment;


    public ViewPagerAdapterConta(@NonNull FragmentActivity fa, Conta contaParaEditar) {
        super(fa);
        if(contaParaEditar != null){
            this.contaParaEditar = contaParaEditar;
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            if (fragmentSubtracao == null) {
                fragmentSubtracao = new FragmentTelaContaSubtracao();
                Bundle args = new Bundle();
                args.putSerializable("conta_para_editar", contaParaEditar);
                fragmentSubtracao.setArguments(args);
            }
            return fragmentSubtracao;
        } else {
            if (fragmentAdicao == null) {
                fragmentAdicao = new FragmentTelaContaAdicao();
                Bundle args = new Bundle();
                args.putSerializable("conta_para_editar", contaParaEditar);
                fragmentAdicao.setArguments(args);
            }
            return fragmentAdicao;
        }
    }


    @Override
    public int getItemCount() {
        return 2; // Número de Fragments
    }
}
