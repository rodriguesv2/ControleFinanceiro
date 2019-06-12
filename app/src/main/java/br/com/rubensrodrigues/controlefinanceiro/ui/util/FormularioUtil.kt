package br.com.rubensrodrigues.controlefinanceiro.ui.util

import android.widget.EditText
import java.util.regex.Pattern

object FormularioUtil {

    fun seCamposMalPreechidos(campoTitulo: EditText, campoValor: EditText): Boolean {
        val titulo = campoTitulo.text.toString()
        val valor = campoValor.text.toString()

        val regexValor = "^(\\d+)(,\\d{2})?$"

        val tituloVazio = if (titulo.isEmpty()){
            campoTitulo.error = "Titulo obrigatório"; true
        } else {false}

        val valorMalFormatado = if(valor.isEmpty() && !Pattern.matches(regexValor, valor)){
            campoValor.error = "Padrão monetário invalido"; true
        } else {false}

        val valorVazio = if(valor.isEmpty()){
            campoValor.error = "Valor obrigatório"; true
        } else {false}

        return tituloVazio || valorVazio || valorMalFormatado
    }
}