package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

class RemoveTransacoesPorIdsTask(private val dao: TransacaoDAO,
                               private val listener: OnPostExecuteListener,
                               vararg val ids: Long) : AsyncTask<Unit, Unit, List<Transacao>>() {

    override fun doInBackground(vararg params: Unit?): List<Transacao> {
        ids.forEach {
            dao.removePor(it)
        }

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
