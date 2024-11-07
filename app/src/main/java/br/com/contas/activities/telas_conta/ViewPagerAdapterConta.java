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

    private int numeroDeFragments = 2;


    public ViewPagerAdapterConta(@NonNull FragmentActivity fa, Conta contaParaEditar) {
        super(fa);
        if(contaParaEditar != null){
            this.contaParaEditar = contaParaEditar;
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (contaParaEditar != null){
            if(contaParaEditar.getTipo().equals("ENTRADA")){
                numeroDeFragments = 1;
                if (position == 0) {
                    if (fragmentAdicao == null) {
                        fragmentAdicao = new FragmentTelaContaAdicao();
                        Bundle args = new Bundle();
                        args.putSerializable("conta_para_editar", contaParaEditar);
                        fragmentAdicao.setArguments(args);
                    }
                    return fragmentAdicao;
                } else {
                    if (fragmentSubtracao == null) {
                        fragmentSubtracao = new FragmentTelaContaSubtracao();
                        Bundle args = new Bundle();
                        args.putSerializable("conta_para_editar", contaParaEditar);
                        fragmentSubtracao.setArguments(args);
                    }
                    return fragmentSubtracao;
                }
            }else {
                numeroDeFragments = 1;
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
        }else{
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
    }

    @Override
    public int getItemCount() {
        return numeroDeFragments; // Número de Fragments
    }
}
