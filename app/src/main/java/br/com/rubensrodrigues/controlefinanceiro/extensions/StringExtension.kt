package br.com.rubensrodrigues.controlefinanceiro.extensions

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

fun String.toCalendar() : Calendar {
    val dataFormatada = SimpleDateFormat("dd/MM/yyyy").parse(this)
    val diaSelecionado = Calendar.getInstance()
    diaSelecionado.time = dataFormatada

    return diaSelecionado
}

fun String.converterReaisParaBigDecimal() : BigDecimal{
    val valorLimpo = this.replace("R$ ", "").replace(",", ".")
    return valorLimpo.toBigDecimal()
}

fun CharSequence.converterReaisParaBigDecimal() : BigDecimal{
    val valor = this.toString()
    val valorLimpo = valor.replace("R$ ", "").replace(",", ".")
    return valorLimpo.toBigDecimal()
}

fun String.toBigDecimalOrNullDeVirgulaParaPonto() : BigDecimal?{
    val novaString = this.replace(",", ".")
    return novaString.toBigDecimalOrNull()
}

fun String.vinteCaracteres() : String{
    if(this.length > 20){
        return "${this.substring(0, 20)}..."
    }
    return this
}