package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

class RemoveTransacaoTask(private val dao: TransacaoDAO,
                          private val transacao: Transacao,
                          private val listener: OnPostExecuteListener) : AsyncTask<Unit, Unit, List<Transacao>>() {

    override fun doInBackground(vararg params: Unit?): List<Transacao> {
        dao.remove(transacao)
        return dao.todos()
    }

    override fun onPostExecute(transacoes: List<Transacao>) {
        super.onPostExecute(transacoes)
        listener.posThread(transacoes)
    }


    interface OnPostExecuteListener{
        fun posThread(transacoes: List<Transacao>)
    }
}
