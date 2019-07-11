package br.com.rubensrodrigues.controlefinanceiro.extensions

import java.util.*

fun Long.millisToCalendar(): Calendar {
    val data = Calendar.getInstance()
    data.timeInMillis = this
    return data
}