package br.com.rubensrodrigues.controlefinanceiro.preferences

import android.content.Context
import java.util.*

object PeriodoListasPreferences : Preferences(){

    const val CHAVE_QUANTIDADE_ULTIMOS_PERIODO = "quantidade_periodo"
    const val CHAVE_TIPO_PERIODO = "tipo_periodo"

    const val DAY_OF_MONTH = Calendar.DAY_OF_MONTH
    const val MONTH = Calendar.MONTH
    const val YEAR = Calendar.YEAR
    const val MES_ATUAL = 1000
    const val MES_ANTERIOR = 1001
    const val ANO_ATUAL = 1004
    const val ANO_ANTERIOR = 1005
    const val PERSONALIZADO = 1099
    const val TUDO = -1000

    fun salvaValores(context: Context, quantidadeUltimosPeriodo: Int, tipoPeriodo: Int){
        setaValorAndChaveInt(getPreferences(context),quantidadeUltimosPeriodo, CHAVE_QUANTIDADE_ULTIMOS_PERIODO)
        setaValorAndChaveInt(getPreferences(context),tipoPeriodo, CHAVE_TIPO_PERIODO)
    }

    fun seChavesExistem(context: Context)
            = seChaveExiste(context, CHAVE_QUANTIDADE_ULTIMOS_PERIODO) && seChaveExiste(context, CHAVE_TIPO_PERIODO)
}