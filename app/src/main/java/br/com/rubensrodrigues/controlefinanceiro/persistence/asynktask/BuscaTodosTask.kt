package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

class BuscaTodosTask(private val dao: TransacaoDAO,
                     private val listener: OnPostExecuteListener) : AsyncTask<Unit, Unit, List<Transacao>>(){

    override fun doInBackground(vararg params: Unit?): List<Transacao> {
        return dao.todos()
    }

    override fun onPostExecute(result: List<Transacao>) {
        super.onPostExecute(result)
        listener.posThread(result)
    }

    interface OnPostExecuteListener{
        fun posThread(listaTransacoes: List<Transacao>)
    }

}
