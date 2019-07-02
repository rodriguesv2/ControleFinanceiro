package br.com.rubensrodrigues.controlefinanceiro.extensions

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.constantes.ConstantesTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO

fun AsyncTask<Unit, Unit, HashMap<Int, MutableList<Transacao>>>.buscaDespesaReceitaAndTodos(dao: TransacaoDAO
): HashMap<Int, MutableList<Transacao>> {
    return hashMapOf(
        ConstantesTask.TODOS to dao.todos().toMutableList(),
        ConstantesTask.DESPESA to dao.todosPor(Tipo.DESPESA).toMutableList(),
        ConstantesTask.RECEITA to dao.todosPor(Tipo.RECEITA).toMutableList()
    )
}