package br.com.rubensrodrigues.controlefinanceiro.extensions

import java.text.DecimalFormat

fun Float.duasCasasVirgula() : String{
    val df = DecimalFormat("0.00")
    return df.format(this).replace(".", ",")
}