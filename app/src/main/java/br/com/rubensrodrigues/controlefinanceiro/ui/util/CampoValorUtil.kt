package br.com.rubensrodrigues.controlefinanceiro.ui.util

import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import java.util.regex.Pattern

object CampoValorUtil {

    fun unicaVirgula(campoValor: TextInputEditText){

        campoValor.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                excluiVirgulaIrregular(campoValor, s)
            }
        })
    }

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