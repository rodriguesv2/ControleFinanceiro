package br.com.rubensrodrigues.controlefinanceiro.ui.util.abstracts

import android.text.Editable
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern

abstract class CamposValor {

    fun excluiVirgulaIrregular(
        campoValor: TextInputEditText,
        editavel: Editable?
    ) {
        val posicaoCursor = campoValor.selectionStart

        val padrao = Pattern.compile(",")
        val matcher = padrao.matcher(editavel)

        var quantidadeDeVirgulas = 0
        while (matcher.find())
            quantidadeDeVirgulas++

        if (editavel!!.isNotEmpty()) {
            if (quantidadeDeVirgulas >= 2 || editavel[0] == ',' && posicaoCursor != 0)
                editavel.delete(posicaoCursor - 1, posicaoCursor)
        }
    }
}