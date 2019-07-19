package br.com.rubensrodrigues.controlefinanceiro.ui.util.abstracts

import android.text.Editable
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class CamposValor {

    fun mudarPontuacaoIrregular(
        campoValor: TextInputEditText,
        editavel: Editable?
    ) {
        val posicaoCursor = campoValor.selectionStart

        if (editavel!!.isNotEmpty()) {
            val caractereDoCursor = editavel[posicaoCursor - 1]

            when(caractereDoCursor){
                ',' -> {excluirVirgulaIrregular(editavel, posicaoCursor)}
                '.' -> {trocaPontoPorVirgula(editavel, posicaoCursor)}
            }
        }
    }

    private fun trocaPontoPorVirgula(editavel: Editable, posicaoCursor: Int) {
        editavel.replace(posicaoCursor - 1, posicaoCursor, ",")
    }

    private fun excluirVirgulaIrregular(editavel: Editable, posicaoCursor: Int) {
        val quantidadeDeVirgulas = totalDeCaracteres(',', editavel)

        if (quantidadeDeVirgulas >= 2 || editavel[0] == ',' && posicaoCursor != 0)
            editavel.delete(posicaoCursor - 1, posicaoCursor)
    }

    private fun totalDeCaracteres(caractere: Char, editavel: Editable?): Int {
        var quantidade = 0
        val matcher = pegarMatcher(caractere, editavel)
        while (matcher.find())
            quantidade++

        return quantidade
    }

    private fun pegarMatcher(caractere: Char, editavel: Editable?): Matcher {
        val padrao = Pattern.compile(caractere.toString())
        val matcher = padrao.matcher(editavel)
        return matcher
    }
}