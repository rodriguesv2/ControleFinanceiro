package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

class BuscaTodosPorTabTask(
    private val dao: TransacaoDAO,
    private val listener: OnPostExecuteListener
) : AsyncTask<Unit, Unit, HashMap<Int, MutableList<Transacao>>>() {

    val TODOS = 1
    val DESPESA = 2
    val RECEITA = 3


    override fun doInBackground(vararg params: Unit?): HashMap<Int, MutableList<Transacao>> {
        return hashMapOf(
            TODOS to dao.todos().toMutableList(),
            DESPESA to dao.todosPor(Tipo.DESPESA).toMutableList(),
            RECEITA to dao.todosPor(Tipo.RECEITA).toMutableList()
        )
    }

    override fun onPostExecute(result: HashMap<Int, MutableList<Transacao>>) {
        super.onPostExecute(result)
        listener.posThread(result[TODOS]!!, result[DESPESA]!!, result[RECEITA]!!)
    }

    interface OnPostExecuteListener {
        fun posThread(
            listaTodos: MutableList<Transacao>,
            listaDespesa: MutableList<Transacao>,
            listaReceita: MutableList<Transacao>
        )
    }

}
