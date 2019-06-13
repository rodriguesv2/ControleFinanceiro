package br.com.rubensrodrigues.controlefinanceiro.persistence.converter

import android.arch.persistence.room.TypeConverter
import java.math.BigDecimal

class ConversorBigDecimal {

    @TypeConverter
    fun paraFloat(valor: BigDecimal) : Float{
        return valor.toFloat()
    }

    @TypeConverter
    fun paraBigDecimal(valor: Float) : BigDecimal{
        return valor.toBigDecimal()
    }
}