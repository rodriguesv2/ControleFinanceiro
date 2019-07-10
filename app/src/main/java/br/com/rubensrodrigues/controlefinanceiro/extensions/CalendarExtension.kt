package br.com.rubensrodrigues.controlefinanceiro.extensions

import android.content.Context
import br.com.rubensrodrigues.controlefinanceiro.preferences.PeriodoListasPreferences
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

fun Calendar.dataPorPeriodo(tipoPeriodo: Int, quantidadeUltimosPeriodo: Int): Calendar{
    return when(tipoPeriodo){
        Calendar.DAY_OF_MONTH -> {this.dataPorDiasAtras(quantidadeUltimosPeriodo)}
        Calendar.MONTH -> {this.dataPorMesesAtras(quantidadeUltimosPeriodo)}
        Calendar.YEAR -> {this.dataPorAnosAtras(quantidadeUltimosPeriodo)}
        else -> {
            this.dataPorAnosAtras(100)
        }
    }
}

fun Calendar.primeiroDiaMes(mes: Int, ano: Int): Calendar{
    this.set(Calendar.MONTH, mes)
    this.set(Calendar.YEAR, ano)
    this.set(Calendar.DAY_OF_MONTH, this.getActualMinimum(Calendar.DAY_OF_MONTH))

    return this.dataHorarioZerado()
}

fun Calendar.ultimoDiaMes(mes: Int, ano: Int): Calendar{
    this.set(Calendar.MONTH, mes)
    this.set(Calendar.YEAR, ano)
    this.set(Calendar.DAY_OF_MONTH, this.getActualMaximum(Calendar.DAY_OF_MONTH))

    return this
}

fun Calendar.primeiroDataRange(tipoPeriodo: Int): Calendar{
    return when(tipoPeriodo){
        PeriodoListasPreferences.MES_ATUAL -> {
            this.primeiroDiaMes(this.get(Calendar.MONTH), this.get(Calendar.YEAR))
        }
        PeriodoListasPreferences.MES_ANTERIOR -> {
            this.add(Calendar.MONTH, -1)
            this.primeiroDiaMes(this.get(Calendar.MONTH), this.get(Calendar.YEAR))
        }
        PeriodoListasPreferences.ANO_ATUAL -> {
            this.primeiroDiaMes(0, this.get(Calendar.YEAR))
        }
        PeriodoListasPreferences.ANO_ANTERIOR -> {
            this.add(Calendar.YEAR, -1)
            this.primeiroDiaMes(0, this.get(Calendar.YEAR))
        }
        else -> {
            this.dataPorAnosAtras(100)
        }
    }
}

fun Calendar.ultimoDataRange(tipoPeriodo: Int): Calendar{
    return when(tipoPeriodo){
        PeriodoListasPreferences.MES_ATUAL -> {
            this.ultimoDiaMes(this.get(Calendar.MONTH), this.get(Calendar.YEAR))
        }
        PeriodoListasPreferences.MES_ANTERIOR -> {
            this.add(Calendar.MONTH, -1)
            this.ultimoDiaMes(this.get(Calendar.MONTH), this.get(Calendar.YEAR))
        }
        PeriodoListasPreferences.ANO_ATUAL -> {
            this.ultimoDiaMes(11, this.get(Calendar.YEAR))
        }
        PeriodoListasPreferences.ANO_ANTERIOR -> {
            this.add(Calendar.YEAR, -1)
            this.ultimoDiaMes(11, this.get(Calendar.YEAR))
        }
        else -> {
            this.dataPorAnosAtras(100)
        }
    }
}

fun Calendar.getDataInicialPeriodo(context: Context): Calendar {
    val quantidadeUltimosPeriodo = PeriodoListasPreferences
        .getValorPorChave(context, PeriodoListasPreferences.CHAVE_QUANTIDADE_ULTIMOS_PERIODO)
    val tipoPeriodo = PeriodoListasPreferences
        .getValorPorChave(context, PeriodoListasPreferences.CHAVE_TIPO_PERIODO)

    return if (quantidadeUltimosPeriodo != 0) {
        Calendar.getInstance().dataPorPeriodo(tipoPeriodo, quantidadeUltimosPeriodo)
    } else {
        Calendar.getInstance().primeiroDataRange(tipoPeriodo)
    }
}

fun Calendar.getDataFinalPeriodo(context: Context): Calendar {
    val quantidadeUltimosPeriodo = PeriodoListasPreferences
        .getValorPorChave(context, PeriodoListasPreferences.CHAVE_QUANTIDADE_ULTIMOS_PERIODO)
    val tipoPeriodo = PeriodoListasPreferences
        .getValorPorChave(context, PeriodoListasPreferences.CHAVE_TIPO_PERIODO)

    return if (quantidadeUltimosPeriodo != 0 ||
        tipoPeriodo == PeriodoListasPreferences.MES_ATUAL ||
        tipoPeriodo == PeriodoListasPreferences.ANO_ATUAL
    ) {
        Calendar.getInstance()
    } else {
        Calendar.getInstance().ultimoDataRange(tipoPeriodo)
    }
}

fun Calendar.dataParaAPI(): String{
    val ano = this.get(Calendar.YEAR)
    val mes = this.get(Calendar.MONTH)+1
    val dia = this.get(Calendar.DAY_OF_MONTH)

    val mesComZero = if (mes < 10) "0$mes" else mes.toString()
    val diaComZero = if (dia < 10) "0$dia" else dia.toString()

    return "$ano$mesComZero$diaComZero"
}




