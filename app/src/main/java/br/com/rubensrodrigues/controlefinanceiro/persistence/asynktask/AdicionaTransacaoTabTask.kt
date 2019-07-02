package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.extensions.buscaDespesaReceitaAndTodos
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.constantes.ConstantesTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

class AdicionaTransacaoTabTask(private val dao: TransacaoDAO,
                               private val transacao: Transacao,
                               private val listener: OnPostExecuteListener
) : AsyncTask<Unit, Unit, HashMap<Int, MutableList<Transacao>>>(){

    override fun doInBackground(vararg params: Unit?): HashMap<Int, MutableList<Transacao>> {
        dao.insere(transacao)
        return buscaDespesaReceitaAndTodos(dao)
    }

    override fun onPostExecute(result: HashMap<Int, MutableList<Transacao>>) {
        super.onPostExecute(result)
        listener.posThread(
            result[ConstantesTask.TODOS]!!,
            result[ConstantesTask.DESPESA]!!,
            result[ConstantesTask.RECEITA]!!)
    }

    interface OnPostExecuteListener {
        fun posThread(
            listaTodos: MutableList<Transacao>,
            listaDespesa: MutableList<Transacao>,
            listaReceita: MutableList<Transacao>
        )
    }
}
