package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.extensions.buscaDespesaReceitaAndTodos
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.constantes.ConstantesTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

class AdicionaTransacoesPorTipoTabTask(private val dao: TransacaoDAO,
                                       private val transacaoSuperfluo: Transacao?,
                                       private val transacaoImportante: Transacao?,
                                       private val listener: OnPostExecuteListener
) : AsyncTask<Unit, Unit, HashMap<Int, MutableList<Transacao>>>(){

    override fun doInBackground(vararg params: Unit?): HashMap<Int, MutableList<Transacao>> {
        if (transacaoSuperfluo != null && transacaoImportante != null){
            dao.insere(transacaoSuperfluo)
            dao.insere(transacaoImportante)
        } else if (transacaoSuperfluo != null){
            dao.insere(transacaoSuperfluo)
        } else if (transacaoImportante != null){
            dao.insere(transacaoImportante)
        }

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
