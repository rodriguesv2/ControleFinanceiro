package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

class AdicionaTransacoesTask(private val dao: TransacaoDAO,
                             private val listener: OnPostExecuteListener,
                             vararg transacoes: Transacao) : AsyncTask<Unit, Unit, List<Transacao>>(){

    val listaTransacoes = transacoes.toList()

    override fun doInBackground(vararg params: Unit?): List<Transacao> {
        dao.insereVarios(listaTransacoes)
        return dao.todos()
    }

    override fun onPostExecute(result: List<Transacao>) {
        super.onPostExecute(result)
        listener.porThread(result)
    }

    interface OnPostExecuteListener{
        fun porThread(transacoes: List<Transacao>)
    }

}
