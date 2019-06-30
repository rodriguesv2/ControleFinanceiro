package br.com.rubensrodrigues.controlefinanceiro.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.formatoBrasileiro() : String{
    val formatoBrasileiro = "dd/MM/yyyy"
    val format = SimpleDateFormat(formatoBrasileiro)
    return format.format(this.time)
}

fun Calendar.dataHorarioZerado() : Calendar{
    val ano = this.get(Calendar.YEAR)
    val mes = this.get(Calendar.MONTH)
    val dia = this.get(Calendar.DAY_OF_MONTH)

    this.clear()
    this.set(ano, mes, dia)
    return this
}