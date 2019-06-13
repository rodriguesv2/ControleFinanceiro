package br.com.rubensrodrigues.controlefinanceiro.persistence.converter

import android.arch.persistence.room.TypeConverter
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo

class ConversorTipo {

    @TypeConverter
    fun paraString(valor: Tipo) : String{
        return valor.name
    }

    @TypeConverter
    fun paraTipo(valor: String) : Tipo{
        return Tipo.valueOf(valor)
    }
}