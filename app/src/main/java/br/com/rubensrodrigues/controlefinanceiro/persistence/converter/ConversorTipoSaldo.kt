package br.com.rubensrodrigues.controlefinanceiro.persistence.converter

import androidx.room.TypeConverter
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo

class ConversorTipoSaldo {

    @TypeConverter
    fun paraString(valor: TipoSaldo?) : String?{
        return valor?.name
    }

    @TypeConverter
    fun paraTipoSaldo(valor: String?) : TipoSaldo? {
        return if (valor != null)
            TipoSaldo.valueOf(valor)
        else
            null
    }
}