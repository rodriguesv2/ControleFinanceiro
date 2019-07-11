package br.com.rubensrodrigues.controlefinanceiro.ui.util

import com.google.android.material.textfield.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import br.com.rubensrodrigues.controlefinanceiro.extensions.duasCasasComVirgula
import br.com.rubensrodrigues.controlefinanceiro.extensions.toBigDecimalOrNullDeVirgulaParaPonto
import br.com.rubensrodrigues.controlefinanceiro.ui.util.abstracts.CamposValor
import java.math.BigDecimal
import java.util.regex.Pattern

object CampoValorUtil : CamposValor(){

    fun unicaVirgula(campoValor: TextInputEditText){

        campoValor.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                excluiVirgulaIrregular(campoValor, s)
            }
        })
    }

    fun editaValorPeloValorEstrangeiro(textoValorEstrangeiro: String,
                                       campoValor: TextInputEditText,
                                       valorEstrangeiro: BigDecimal,
                                       valorSeEdicao: String = "") {
        campoValor.isEnabled =
            if (!textoValorEstrangeiro.isEmpty()) {
                val valor = textoValorEstrangeiro.toBigDecimalOrNullDeVirgulaParaPonto()
                val valorCalculado = valor!!.multiply(valorEstrangeiro)
                campoValor.setText(valorCalculado.duasCasasComVirgula())
                false
            } else {
                campoValor.setText(valorSeEdicao)
                true
            }
    }
}