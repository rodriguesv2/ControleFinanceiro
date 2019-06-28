package br.com.rubensrodrigues.controlefinanceiro.ui.util

import com.google.android.material.textfield.TextInputEditText
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiro
import java.util.*

object DateUtil {

    fun setaDataAtualNoCampoData(campoData: TextInputEditText){
        val hoje = Calendar.getInstance()
        campoData.setText(hoje.formatoBrasileiro())
    }
}