package br.com.rubensrodrigues.controlefinanceiro.ui.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.ui.recyclerview.adapter.ListaTransacoesAdapter
import kotlinx.android.synthetic.main.fragment_lista.view.*

class ListaTodosFragment(private val listaTransacoes: MutableList<Transacao>) : Fragment() {

    private lateinit var listaTransacoesAdapter: ListaTransacoesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lista, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuraRecyclerView()
    }

    private fun configuraRecyclerView() {
        listaTransacoesAdapter = ListaTransacoesAdapter(context!!, listaTransacoes)
        view!!.fragment_lista_recyclerview.adapter = listaTransacoesAdapter

        listaTransacoesAdapter.setOnItemClickListener(object : ListaTransacoesAdapter.ListaTransacoesAdapterListener{
            override fun simplesCliqueItem(transacao: Transacao) {

            }
        })
        //configuraCliqueItemListaTransacoes()
    }

}
