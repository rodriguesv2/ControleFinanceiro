package br.com.rubensrodrigues.controlefinanceiro.persistence.converter

import androidx.room.TypeConverter
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo

class ConversorTipo {

    @TypeConverter
    fun paraString(valor: Tipo?) : String?{
        return if (valor != null)
            valor.name
        else
            null
    }

    @TypeConverter
    fun paraTipo(valor: String?) : Tipo?{
        return if (valor != null)
            Tipo.valueOf(valor)
        else
            null
    }
}