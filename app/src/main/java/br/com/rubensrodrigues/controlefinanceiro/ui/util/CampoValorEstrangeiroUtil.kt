package br.com.rubensrodrigues.controlefinanceiro.ui.util

import com.google.android.material.textfield.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import br.com.rubensrodrigues.controlefinanceiro.ui.util.abstracts.CamposValor

object CampoValorEstrangeiroUtil : CamposValor(){

    fun unicaVirgula(campoValor: TextInputEditText, acaoPosDigitarUmaTecla: (texto: String)->Unit){

        campoValor.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                mudarPontuacaoIrregular(campoValor, s)
                acaoPosDigitarUmaTecla(s.toString())
            }
        })
    }



}