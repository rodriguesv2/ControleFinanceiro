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