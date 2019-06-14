package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

class AdicionaTransacoesTask(private val dao: TransacaoDAO,
                             private val transacaoSuperfluo: Transacao?,
                             private val transacaoImportante: Transacao?,
                             private val listener: OnPostExecuteListener) : AsyncTask<Unit, Unit, List<Transacao>>(){


    override fun doInBackground(vararg params: Unit?): List<Transacao> {
        if (transacaoSuperfluo != null && transacaoImportante != null){
            dao.insere(transacaoSuperfluo)
            dao.insere(transacaoImportante)
        } else if (transacaoSuperfluo != null){
            dao.insere(transacaoSuperfluo)
        } else if (transacaoImportante != null){
            dao.insere(transacaoImportante)
        }

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
