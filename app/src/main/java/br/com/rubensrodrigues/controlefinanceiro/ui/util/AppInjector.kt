package br.com.rubensrodrigues.controlefinanceiro.ui.util

import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.ui.activity.MainViewModel

object AppInjector {

    fun getMainViewModel(dao: TransacaoDAO) = MainViewModel.Factory(dao)
}