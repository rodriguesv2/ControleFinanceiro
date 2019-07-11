package br.com.rubensrodrigues.controlefinanceiro.preferences

import android.content.Context
import android.content.SharedPreferences
import java.util.*

abstract class Preferences {

    fun getValorPorChaveInt(context: Context, chave: String): Int{
        return getPreferences(context).getInt(chave, 0)
    }

    fun getValorPorChaveLong(context: Context, chave: String): Long{
        return getPreferences(context).getLong(chave, 1L)
    }

    fun getValorPorChaveFloat(context: Context, chave: String): Float{
        return getPreferences(context).getFloat(chave, 0F)
    }

    fun setaValorAndChaveInt(preferences: SharedPreferences, valor: Int, chave: String){
        val editor = preferences.edit()
        editor.putInt(chave, valor)
        editor.apply()
    }

    fun setaValorAndChaveLong(preferences: SharedPreferences, valor: Long, chave: String){
        val editor = preferences.edit()
        editor.putLong(chave, valor)
        editor.apply()
    }

    fun setaValorAndChaveFloat(preferences: SharedPreferences, valor: Float, chave: String){
        val editor = preferences.edit()
        editor.putFloat(chave, valor)
        editor.apply()
    }

    fun getPreferences(context: Context) = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    fun seChaveExiste(context: Context, chave: String) = getPreferences(context).contains(chave)
}