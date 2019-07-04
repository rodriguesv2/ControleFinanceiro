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

fun Calendar.dataPorDiasAtras(dias: Int): Calendar{
    val diasNegativo = dias - dias - dias
    this.add(Calendar.DAY_OF_MONTH, diasNegativo)

    return this.dataHorarioZerado()
}

fun Calendar.dataPorMesesAtras(meses: Int): Calendar{
    val diasNegativo = meses - meses - meses
    this.add(Calendar.MONTH, diasNegativo)

    return this.dataHorarioZerado()
}

fun Calendar.dataPorAnosAtras(anos: Int): Calendar{
    val diasNegativo = anos - anos - anos
    this.add(Calendar.YEAR, diasNegativo)

    return this.dataHorarioZerado()
}

fun Calendar.dataPorPeriodo(tipoPeriodo: Int, quantidadePeriodo: Int): Calendar{
    return when(tipoPeriodo){
        Calendar.DAY_OF_MONTH -> {this.dataPorDiasAtras(quantidadePeriodo)}
        Calendar.MONTH -> {this.dataPorMesesAtras(quantidadePeriodo)}
        Calendar.YEAR -> {this.dataPorAnosAtras(quantidadePeriodo)}
        else -> {
            this
        }
    }
}



