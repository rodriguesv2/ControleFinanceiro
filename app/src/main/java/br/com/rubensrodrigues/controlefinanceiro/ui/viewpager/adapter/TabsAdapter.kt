package br.com.rubensrodrigues.controlefinanceiro.ui.viewpager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val listFragment = mutableListOf<Fragment>()
    private val listTitulo = mutableListOf<String>()

    fun add(frag: Fragment, titulo: String){
        this.listFragment.add(frag)
        this.listTitulo.add(titulo)
    }

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return listTitulo[position]
    }


}