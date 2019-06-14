package br.com.rubensrodrigues.controlefinanceiro.persistence.converter

import android.arch.persistence.room.TypeConverter
import java.math.BigDecimal

class ConversorBigDecimal {

    @TypeConverter
    fun paraFloat(valor: BigDecimal?) : Float?{
        return if(valor != null)
            valor.toFloat()
        else
            0f
    }

    @TypeConverter
    fun paraBigDecimal(valor: Float?) : BigDecimal?{
        return if (valor != null)
            valor.toBigDecimal()
        else
            BigDecimal.ZERO
    }
}