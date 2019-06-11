package br.com.rubensrodrigues.controlefinanceiro.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.formatoBrasileiro() : String{
    val formatoBrasileiro = "dd/MM/yyyy"
    val format = SimpleDateFormat(formatoBrasileiro)
    return format.format(this.time)
}