package br.com.rubensrodrigues.controlefinanceiro.util

import android.support.design.widget.TextInputEditText
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiro
import java.util.*

object DateUtil {

    fun setaDataAtualNoCampoData(campoData: TextInputEditText){
        val hoje = Calendar.getInstance()
        campoData.setText(hoje.formatoBrasileiro())
    }
}