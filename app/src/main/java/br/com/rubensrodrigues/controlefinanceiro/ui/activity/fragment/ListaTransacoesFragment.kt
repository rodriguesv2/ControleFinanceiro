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

class ListaTransacoesFragment(var listaTransacoes: MutableList<Transacao>) : Fragment() {

    private val listaTransacoesAdapter:ListaTransacoesAdapter? by lazy { pegaListaTransacoesAdapter() }
    var cliqueItem: (transacao: Transacao) -> Unit = {}


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
        if (listaTransacoesAdapter != null) {
            view!!.fragmentListaRecyclerview.adapter = listaTransacoesAdapter

            listaTransacoesAdapter?.setOnItemClickListener(object :
                ListaTransacoesAdapter.ListaTransacoesAdapterListener {
                override fun simplesCliqueItem(transacao: Transacao) {
                    cliqueItem(transacao)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        listaTransacoesAdapter!!.atualizaLista(listaTransacoes)
    }

    fun atualizarLista(){
        if (listaTransacoesAdapter != null)
            listaTransacoesAdapter?.atualizaLista(listaTransacoes)
    }

    private fun pegaListaTransacoesAdapter(): ListaTransacoesAdapter?{
        return if (context != null)
            ListaTransacoesAdapter(context!!, listaTransacoes)
        else
            null
    }

    fun getTransacaoParaRemover(): Transacao{
        return listaTransacoesAdapter!!.transacaoParaRemover
    }

    fun removerItemAnimacaoSuave(transacoes: MutableList<Transacao>){
        listaTransacoes = transacoes
        listaTransacoesAdapter!!.remove(transacoes)
    }

    fun removerTransferenciaAnimacaoSuave(transacoes: MutableList<Transacao>, transacaoPressionada: Transacao){
        listaTransacoes = transacoes
        listaTransacoesAdapter!!.removeTransferencias(transacoes, transacaoPressionada)
    }
}
