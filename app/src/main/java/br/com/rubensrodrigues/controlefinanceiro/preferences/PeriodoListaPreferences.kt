package br.com.rubensrodrigues.controlefinanceiro.preferences

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object PeriodoListaPreferences {

    const val CHAVE_QUANTIDADE_PERIODO = "quantidade_periodo"
    const val CHAVE_TIPO_PERIODO = "tipo_periodo"

    const val DAY_OF_MONTH = Calendar.DAY_OF_MONTH
    const val MONTH = Calendar.MONTH
    const val YEAR = Calendar.YEAR

    fun salvaValores(context: Context, quantidadePeriodo: Int, tipoPeriodo: Int){
        setaValorAndChave(getPreferences(context),quantidadePeriodo, CHAVE_QUANTIDADE_PERIODO)
        setaValorAndChave(getPreferences(context),tipoPeriodo, CHAVE_TIPO_PERIODO)
    }

    fun seChavesExistem(context: Context)
            = seChaveExiste(context, CHAVE_QUANTIDADE_PERIODO) && seChaveExiste(context, CHAVE_TIPO_PERIODO)

    fun getValorPorChave(context: Context, chave: String): Int{
        return getPreferences(context).getInt(chave, 0)
    }

    private fun getPreferences(context: Context) = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    private fun seChaveExiste(context: Context, chave: String) = getPreferences(context).contains(chave)

    private fun setaValorAndChave(preferences: SharedPreferences, valor: Int, chave: String){
        val editor = preferences.edit()
        editor.putInt(chave, valor)
        editor.apply()
    }


}