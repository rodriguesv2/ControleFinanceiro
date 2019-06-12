package br.com.rubensrodrigues.controlefinanceiro.ui.util

import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher

object PrefixoCampoValorUtil {

    fun insereSimboloDeReaisEsquerda(campoValor: TextInputEditText) {
        campoValor.setText("R$ ")
        Selection.setSelection(campoValor.text, campoValor.text!!.length)

        configuraListenerDeTextoAlterado(campoValor)
    }

    private fun configuraListenerDeTextoAlterado(campoValor: TextInputEditText) {
        campoValor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                adicionaSimboloDeReais(campoValor, editable)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun adicionaSimboloDeReais(campoValor: TextInputEditText, editable: Editable?): String {
        val valorStr = editable.toString()
        if (!valorStr.startsWith("R$ ")) {
            campoValor.setText("R$ ")
            Selection.setSelection(campoValor.text, campoValor.text!!.length)
        }
        return valorStr
    }
}