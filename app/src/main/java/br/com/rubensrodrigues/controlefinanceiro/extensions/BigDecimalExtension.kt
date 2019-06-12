package br.com.rubensrodrigues.controlefinanceiro.extensions

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

fun BigDecimal.formatoBrasileiroMonetario() : String{
    val formatoBrasileiro = DecimalFormat.getCurrencyInstance(Locale("pt", "br"))
    return formatoBrasileiro.format(this)
        .replace("R$", "R$ ")
        .replace("-R$", "R$ -")
}

fun BigDecimal.duasCasas() : String{
    val df = DecimalFormat("0.00")
    return df.format(this).replace(",", ".")
}