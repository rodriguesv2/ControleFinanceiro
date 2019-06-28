package br.com.rubensrodrigues.controlefinanceiro.persistence.converter

import androidx.room.TypeConverter
import java.util.*

class ConversorCalendar {

    @TypeConverter
    fun paraLong(valor: Calendar?): Long?{
        return valor?.timeInMillis

    }

    @TypeConverter
    fun paraCalendar(valor: Long?) : Calendar?{
        return if (valor != null){
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = valor
            calendar
        } else {
            null
        }
    }

}