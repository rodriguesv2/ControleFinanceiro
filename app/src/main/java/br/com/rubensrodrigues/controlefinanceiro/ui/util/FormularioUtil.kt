package br.com.rubensrodrigues.controlefinanceiro.ui.util

import android.widget.EditText
import java.util.regex.Pattern

object FormularioUtil {

    fun ehCamposMalPreechidos(campoTitulo: EditText, campoValor: EditText): Boolean {
        val titulo = campoTitulo.text.toString()
        val valor = campoValor.text.toString()

        return ehTituloVazio(titulo, campoTitulo) ||
                ehValorMalFormatado(valor, campoValor) ||
                ehValorVazio(valor, campoValor)
    }

    fun ehCampoValorMalPreenchido(campoValor: EditText): Boolean{
        val valor = campoValor.text.toString()

        return ehValorMalFormatado(valor, campoValor) || ehValorVazio(valor, campoValor)
    }

    private fun ehValorVazio(valor: String, campoValor: EditText): Boolean {
        return if (valor.isEmpty()) {
            campoValor.error = "Valor obrigatório"; true
        } else {
            false
        }
    }

    private fun ehValorMalFormatado(valor: String, campoValor: EditText): Boolean {
        return if (valor.isEmpty() && !Pattern.matches(getRegexValor(), valor)) {
            campoValor.error = "Padrão monetário invalido"; true
        } else {
            false
        }
    }

    private fun ehTituloVazio(titulo: String, campoTitulo: EditText): Boolean {
        return if (titulo.isEmpty()) {
            campoTitulo.error = "Titulo obrigatório"; true
        } else {
            false
        }
    }

    private fun getRegexValor() = "^(\\d+)(,\\d{2})?$"
}