package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.extensions.buscaDespesaReceitaAndTodosPorData
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.constantes.ConstantesTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.listener.OnPostExecuteTodasListasListener
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO
import java.util.*

class RemoveTransacaoTask(private val dao: TransacaoDAO,
                          private val dataInicial: Calendar,
                          private val dataFinal: Calendar,
                          private val transacao: Transacao,
                          private val listener: OnPostExecuteTodasListasListener
) : AsyncTask<Unit, Unit, HashMap<Int, MutableList<Transacao>>>() {

    override fun doInBackground(vararg params: Unit?): HashMap<Int, MutableList<Transacao>> {
        dao.remove(transacao)
        return buscaDespesaReceitaAndTodosPorData(dao, dataInicial, dataFinal)
    }

    override fun onPostExecute(result: HashMap<Int, MutableList<Transacao>>) {
        super.onPostExecute(result)
        listener.posThread(
            result[ConstantesTask.TODOS]!!,
            result[ConstantesTask.DESPESA]!!,
            result[ConstantesTask.RECEITA]!!,
            result[ConstantesTask.FUTURO]!!)
    }

}
