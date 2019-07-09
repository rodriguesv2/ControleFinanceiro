package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.listener

import br.com.rubensrodrigues.controlefinanceiro.model.Transacao

interface OnPostExecuteTodasListasListener {
    fun posThread(
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>,
        listaFuturo: MutableList<Transacao>
    )
}