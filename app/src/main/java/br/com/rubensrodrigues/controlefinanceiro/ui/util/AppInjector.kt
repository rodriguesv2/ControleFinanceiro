package br.com.rubensrodrigues.controlefinanceiro.ui.util

import br.com.rubensrodrigues.controlefinanceiro.ui.activity.MainViewModel

object AppInjector {

    fun getMainViewModel() = MainViewModel.Factory()
}