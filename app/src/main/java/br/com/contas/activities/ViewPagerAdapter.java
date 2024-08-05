package br.com.contas.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentTelaContaFormatoListaSubtracao();
            case 1:
                return new FragmentTelaContaFormatoListaAdicao();
            default:
                return new FragmentTelaContaFormatoListaSubtracao();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Número de Fragments
    }
}